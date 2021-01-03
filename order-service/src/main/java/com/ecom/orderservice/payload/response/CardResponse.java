package com.ecom.orderservice.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardResponse {
	private String brand;
	private String country;
	private int exp_month;
	private int exp_year;
	private String fingerprint;
	private String funding;
	private String last4;
	private String network;
}
