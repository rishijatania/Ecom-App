package com.ecom.orderservice.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodDetailsResponse {
	private CardResponse card;
	private String type;
}
