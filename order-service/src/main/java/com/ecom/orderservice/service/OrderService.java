package com.ecom.orderservice.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.transaction.Transactional;

import com.ecom.orderservice.models.Address;
import com.ecom.orderservice.models.AddressTypeEnum;
import com.ecom.orderservice.models.Item;
import com.ecom.orderservice.models.Order;
import com.ecom.orderservice.models.OrderStatusEnum;
import com.ecom.orderservice.models.Payment;
import com.ecom.orderservice.payload.request.AddressRequest;
import com.ecom.orderservice.payload.request.ItemRequest;
import com.ecom.orderservice.payload.request.OrderCreateRequest;
import com.ecom.orderservice.payload.response.ItemResponseApi;
import com.ecom.orderservice.payload.response.PaymentResponseApi;
import com.ecom.orderservice.repository.AddressRepository;
import com.ecom.orderservice.repository.OrderRepository;
import com.ecom.orderservice.util.SequenceGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrderService {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Value(value = "${http.timeout:20}")
	private long timeout;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private SequenceGenerator squenceGenerator;

	public Order saveOrder(Long orderID, OrderCreateRequest orderReq, List<PaymentResponseApi> transactions,
			List<ItemResponseApi> inventoryItems) {

		Address shipping_addr = getOrCreateAddressMapping(orderReq.getOrder_shipping_address(),
				AddressTypeEnum.SHIPPING_ADDRESS);
		Address billing_addr = getOrCreateAddressMapping(orderReq.getOrder_billing_address(),
				AddressTypeEnum.BILLING_ADDRESS);

		Order order = new Order();
		order.setOrderID(orderID == null ? squenceGenerator.nextId() : orderID);
		order.setOrder_customer_id(orderReq.getOrder_customer_id());
		order.setOrder_tax(orderReq.getOrder_tax());
		order.setOrder_shipping_address(shipping_addr);
		order.setOrder_billing_address(billing_addr);
		order.setOrder_status(OrderStatusEnum.ORDER_ACCEPTED);
		order = orderRepository.save(order);

		Set<Payment> pays = new HashSet<>();
		for (PaymentResponseApi tran : transactions) {
			Payment payment = paymentService.savePayment(tran, order);
			pays.add(payment);
		}
		order.setPayments(pays);

		List<Item> items = new ArrayList<>();
		for (ItemResponseApi itemRes : inventoryItems) {
			Item item = itemService.saveItem(itemRes, order);
			items.add(item);
		}
		order.setItems(items);

		return order;
	}

	public Address getOrCreateAddressMapping(AddressRequest addressReq, AddressTypeEnum type) {
		Address addr = null;
		if (null != addressReq.getAddress_id() && !addressReq.getAddress_id().toString().isEmpty()) {
			Optional<Address> temp = addressRepository.findById(addressReq.getAddress_id());
			if (temp.isPresent() && temp.get().getType().equals(type))
				return addressRepository.findById(addressReq.getAddress_id()).get();
		}

		addr = new Address();
		addr.setAddressline1(addressReq.getAddressline1());
		addr.setAddressline2(addressReq.getAddressline2());
		addr.setCity(addressReq.getCity());
		addr.setState(addressReq.getState());
		addr.setZip(addressReq.getZip());
		addr.setType(type);
		addressRepository.save(addr);
		return addr;
	}

	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	public Optional<Order> getOrderByOrderId(Long orderID) {
		return orderRepository.findByOrderID(orderID);
	}

	public Order cancelOrder(Order order) {
		order.setOrder_status(OrderStatusEnum.ORDER_CANCELLED);
		return orderRepository.save(order);
	}

	public void createBulkOrder(Long orderID, OrderCreateRequest orderReq) {
		try {
			LOG.debug("Starting calls");
			List<Future<?>> itemFuture = itemService.initiateInventoryCheck(orderReq);
			List<Future<?>> paymentFuture = paymentService.intiatePayment(orderReq);
			LOG.debug("End of calls.");

			List<PaymentResponseApi> transactions = new ArrayList<>();
			for (Future<?> paymentResponse : paymentFuture) {
				PaymentResponseApi trans = (PaymentResponseApi) paymentResponse.get(timeout, TimeUnit.SECONDS);
				trans.setAmount(trans.getAmount() / 100);
				transactions.add(trans);
			}

			
			List<ItemResponseApi> items = new ArrayList<>();
			for (Future<?> itemResponse : itemFuture) {
				List<ItemResponseApi> itemsList = (List<ItemResponseApi>) itemResponse.get(timeout, TimeUnit.SECONDS);

				if (itemsList == null || itemsList.isEmpty()) {
					LOG.error("Order Create failed reason='{}' for OrderID={}", "Unable to find Item", orderID);
					// Call a Cancel Payment API
					return;
				}
				Optional<ItemRequest> reqitem = orderReq.getItems().stream()
						.filter(item -> item.getItemName().equals(itemsList.get(0).getItemName())).findFirst();
				if (reqitem.get().getItemQuantity() > itemsList.get(0).getItemQuantity()) {
					LOG.error("Order Create failed reason='{}' for OrderID={}", "Requested Item Quantity Unavailable",
							orderID);
					// Call a Cancel Payment API
					return;
				}
				itemsList.get(0).setItemQuantity(reqitem.get().getItemQuantity());
				items.addAll(itemsList);
			}
			LOG.debug("Save Action Initiated.");
			try {
				saveOrder(orderID, orderReq, transactions, items);
			} catch (Exception e) {
				// Call a Cancel Payment API
				LOG.debug(e.getStackTrace().toString());
				LOG.error("Order Create failed reason='{}' for OrderID={}", "Unable to Save Order", orderID);
				return;
			}
			LOG.info("Order Create successfull for OrderID={}", orderID);
		} catch (TimeoutException e) {
			// Call a Cancel Payment API
			LOG.error("Order Create failed reason='{}' for OrderID={}",
					"Unable to create order due to timeout from one of the services.", orderID);
		} catch (InterruptedException | ExecutionException e) {
			// Call a Cancel Payment API
			e.printStackTrace();
			LOG.error("Order Create failed reason='{}' for OrderID={}",
					"Unable to create order due to unspecified IO error.", orderID);
		}
	}

	public void cancelBulkOrder(Long orderID) {
		try {
			Optional<Order> order = getOrderByOrderId(orderID);
			if (!order.isPresent()) {
				LOG.error("Order Cancel failed reason='{}' for OrderID={}", "Order Not Found", orderID);
				return;
			}
			if (OrderStatusEnum.ORDER_CANCELLED.equals(order.get().getOrder_status())) {
				LOG.error("Order Cancel failed reason='{}' for OrderID={}", "Order cannot be updated once cancelled!",
						orderID);
				return;
			}
			cancelOrder(order.get());
		} catch (Exception e) {
			LOG.debug(e.getStackTrace().toString());
			LOG.error("Order Create failed reason='{}' for OrderID={}", "Unable to Cancel Order", orderID);
		}
	}
}
