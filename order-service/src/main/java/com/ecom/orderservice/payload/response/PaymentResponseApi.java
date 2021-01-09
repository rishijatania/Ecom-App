package com.ecom.orderservice.payload.response;

import java.util.Date;

import lombok.Data;

@Data
public class PaymentResponseApi {

	private String id;
	private long amount;
	private long amount_captured;
	private long amount_refunded;
	private String currency;
	private boolean paid;
	private String payment_method;
	private PaymentMethodDetailsResponse payment_method_details;
	private String receipt_url;
	private String status;
	private Date payment_date;
}
