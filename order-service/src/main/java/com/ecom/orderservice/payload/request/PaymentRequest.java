package com.ecom.orderservice.payload.request;

import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class PaymentRequest {

	@Pattern(regexp="^\\d+(\\.\\d{1,2})?$",message="Please round off the amount to 2 Decimal Places")
	private String amount;	
	private String currency;
	private String stored_card_name;
}
