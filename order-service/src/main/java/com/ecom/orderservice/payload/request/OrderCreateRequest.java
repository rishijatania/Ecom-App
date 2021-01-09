package com.ecom.orderservice.payload.request;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class OrderCreateRequest {

	@NotNull
	private UUID order_customer_id;

	private String order_shipping_charges;

	@Valid
	private List<ItemRequest> items;

	@Valid
	private List<PaymentRequest> payments;

	@NotNull
	private Double order_tax;

	@NotNull
	private AddressRequest order_shipping_address;

	@NotNull
	private AddressRequest order_billing_address;

	private String order_status;
}
