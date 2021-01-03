package com.ecom.orderservice.payload.request;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreateRequest {
	
	@NotNull
	private UUID order_customer_id;
	// private String order_item_name
	// private String order_item_qty


	private String order_shipping_charges;

	// order_payment_method
	// order_payment_date
	// order_payment_confirmation_number

	// @NotEmpty
	// @NotNull
	@Valid
	private List<PaymentRequest> payments;
    // private Double order_subtotal;

	@NotNull
    private Double order_tax;

    // private Double total;

	@NotNull
    private AddressRequest order_shipping_address;

	@NotNull
    private AddressRequest order_billing_address;

	private String order_status;
}

