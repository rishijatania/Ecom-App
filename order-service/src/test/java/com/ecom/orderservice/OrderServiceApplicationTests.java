package com.ecom.orderservice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ecom.orderservice.models.DeliveryMethodEnum;
import com.ecom.orderservice.models.OrderStatusEnum;
import com.ecom.orderservice.payload.request.AddressRequest;
import com.ecom.orderservice.payload.request.ItemRequest;
import com.ecom.orderservice.payload.request.OrderCreateRequest;
import com.ecom.orderservice.payload.request.PaymentRequest;
import com.ecom.orderservice.payload.response.OrderResponse;
import com.ecom.orderservice.payload.response.OrdersListResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
class OrderServiceApplicationTests extends MVCRestTest {

	public static final String BASE_URL = "/api/v1/orders";

	private static Long orderId;

	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();
	}

	@Test
	public void testCreateOrder() throws Exception {

		OrderCreateRequest orderCreateRequest = new OrderCreateRequest();

		List<PaymentRequest> payments = new ArrayList<>();

		PaymentRequest paymentRequest = new PaymentRequest();
		paymentRequest.setAmount("50");
		paymentRequest.setCurrency("usd");
		paymentRequest.setStored_card_name("tok_mastercard");
		payments.add(paymentRequest);

		paymentRequest = new PaymentRequest();
		paymentRequest.setAmount("60.78");
		paymentRequest.setCurrency("usd");
		paymentRequest.setStored_card_name("tok_visa");
		payments.add(paymentRequest);

		List<ItemRequest> items = new ArrayList<>();
		ItemRequest itemRequest = new ItemRequest();
		itemRequest.setItemName("ITEMA");
		itemRequest.setItemQuantity(1);
		items.add(itemRequest);

		itemRequest = new ItemRequest();
		itemRequest.setItemName("ITEMB");
		itemRequest.setItemQuantity(1);
		items.add(itemRequest);

		AddressRequest billing = new AddressRequest();
		billing.setAddress_id(UUID.randomUUID());
		billing.setAddressline1("1171 Boylston St");
		billing.setAddressline2("Apt 3");
		billing.setCity("Boston");
		billing.setState("MA");
		billing.setZip("02125");

		AddressRequest shipping = new AddressRequest();
		shipping.setAddress_id(UUID.randomUUID());
		shipping.setAddressline1("1171 Boylston St");
		shipping.setAddressline2("Apt 3");
		shipping.setCity("Boston");
		shipping.setState("MA");
		shipping.setZip("02125");

		orderCreateRequest.setOrder_billing_address(billing);
		orderCreateRequest.setOrder_shipping_address(shipping);
		orderCreateRequest.setOrder_tax(6.52);
		orderCreateRequest.setPayments(payments);
		orderCreateRequest.setItems(items);
		orderCreateRequest.setOrder_customer_id(UUID.randomUUID());
		orderCreateRequest.setDelivery_method(DeliveryMethodEnum.DELIVERY.name());

		String json = mapToJson(orderCreateRequest);

		String uri = BASE_URL;
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).content(json)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		Assertions.assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();
		OrderResponse orderResponse = super.mapFromJson(content, OrderResponse.class);
		Assertions.assertEquals(OrderStatusEnum.ORDER_ACCEPTED.name(), orderResponse.getOrder_status());
		orderId = orderResponse.getOrderID();

		testGetOrderById();
		testCancelOrderById();
	}

	@Test
	public void testGetOrders() throws Exception {
		String uri = BASE_URL;
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		Assertions.assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();
		OrdersListResponse ordersListResponse = super.mapFromJson(content, OrdersListResponse.class);
		Assertions.assertTrue(ordersListResponse.getOrders() != null && ordersListResponse.getOrders().size() >= 0);
	}

	public void testGetOrderById() throws Exception {
		String uri = BASE_URL + "/" + orderId;
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		Assertions.assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();
		OrderResponse orderResponse = super.mapFromJson(content, OrderResponse.class);
		Assertions.assertEquals(orderId, orderResponse.getOrderID());
	}

	public void testCancelOrderById() throws Exception {
		String uri = BASE_URL + "/" + orderId + "/cancellation";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		Assertions.assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();
		OrderResponse orderResponse = super.mapFromJson(content, OrderResponse.class);
		Assertions.assertEquals(OrderStatusEnum.ORDER_CANCELLED.name(), orderResponse.getOrder_status());
	}

}
