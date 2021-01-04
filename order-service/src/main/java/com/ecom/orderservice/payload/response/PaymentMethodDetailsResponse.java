package com.ecom.orderservice.payload.response;

import lombok.Data;

@Data
public class PaymentMethodDetailsResponse {
	private CardResponse card;
	private String type;
}
