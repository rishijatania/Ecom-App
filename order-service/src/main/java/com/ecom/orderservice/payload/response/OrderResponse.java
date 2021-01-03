package com.ecom.orderservice.payload.response;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponse {
	private Long orderID;
	
	private UUID order_customer_id;
	// private String order_item_name
	// private String order_item_qty

	private Double order_shipping_charges;

	// order_payment_method
	// order_payment_date
	// order_payment_confirmation_number
    private Date orderedAt;

    private Date updatedAt;

    private String order_status;

    private Double order_subtotal;

    private Double order_tax;

    private Double total;

    private AddressResponse order_shipping_address;

    private AddressResponse order_billing_address;

	private List<PaymentResponse> payments;
}
