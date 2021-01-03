package com.ecom.orderservice.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import com.ecom.orderservice.models.Address;
import com.ecom.orderservice.models.AddressTypeEnum;
import com.ecom.orderservice.models.Order;
import com.ecom.orderservice.models.OrderStatusEnum;
import com.ecom.orderservice.models.Payment;
import com.ecom.orderservice.payload.request.AddressRequest;
import com.ecom.orderservice.payload.request.OrderCreateRequest;
import com.ecom.orderservice.payload.response.PaymentResponseApi;
import com.ecom.orderservice.repository.AddressRepository;
import com.ecom.orderservice.repository.OrderRepository;
import com.ecom.orderservice.repository.PaymentRepository;
import com.ecom.orderservice.util.SequenceGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private SequenceGenerator squenceGenerator;

	public Order saveOrder(OrderCreateRequest orderReq, List<PaymentResponseApi> transactions) {

		Address shipping_addr = getOrCreateAddressMapping(orderReq.getOrder_shipping_address(),
				AddressTypeEnum.SHIPPING_ADDRESS);
		Address billing_addr = getOrCreateAddressMapping(orderReq.getOrder_billing_address(),
				AddressTypeEnum.BILLING_ADDRESS);
		Set<Payment> pays = new HashSet<>();
		transactions.forEach(tran -> {
			Payment payment = paymentService.savePayment(tran);
			pays.add(payment);
		});

		Order order = new Order();
		order.setOrderID(squenceGenerator.nextId());
		order.setOrder_subtotal(1.00);
		order.setOrder_customer_id(orderReq.getOrder_customer_id());
		order.setOrder_tax(orderReq.getOrder_tax());
		order.setOrder_shipping_address(shipping_addr);
		order.setOrder_billing_address(billing_addr);
		order.setOrder_status(OrderStatusEnum.ORDER_ACCEPTED);
		order.setPayments(pays);
		order = orderRepository.save(order);
		for (Payment payment : pays) {
			payment.setOrder(order);
			paymentRepository.save(payment);
		}

		return order;
	}

	public Address getOrCreateAddressMapping(AddressRequest addressReq, AddressTypeEnum type) {
		Address addr = null;
		if (null != addressReq.getAddress_id() && !addressReq.getAddress_id().toString().isBlank()) {
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
}
