package com.ecom.orderservice.payload.response;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponse {
    private String id;
	private long amount;
	private String currency;
	private boolean paid;
	private String payment_method;
	private CardResponse card;
	private String receipt_url;
	private String status;
	private Date payment_date;
}