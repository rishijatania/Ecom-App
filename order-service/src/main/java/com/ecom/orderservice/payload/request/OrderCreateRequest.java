package com.ecom.orderservice.payload.request;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class OrderCreateRequest {

	@NotNull
	private UUID order_customer_id;

	@Valid
	@NotEmpty
	private List<ItemRequest> items;

	@Valid
	@NotEmpty
	private List<PaymentRequest> payments;

	@NotNull
	@NotEmpty
	private String delivery_method;

	@NotNull
	private Double order_tax;

	@NotNull
	@Valid
	private AddressRequest order_shipping_address;

	@NotNull
	@Valid
	private AddressRequest order_billing_address;

}
