package com.ecom.orderservice.payload.response;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class OrderResponse {
	private Long orderID;

	private UUID order_customer_id;

	private List<ItemResponseApi> items;

	private Date orderedAt;

	private Date updatedAt;

	private String order_status;

	private Double order_subtotal;

	private Double order_tax;

	private Double total;

	private String delivery_method;

	private AddressResponse order_shipping_address;

	private AddressResponse order_billing_address;

	private List<PaymentResponse> payments;
}
