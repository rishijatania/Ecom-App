package com.ecom.orderservice.payload.response;

import java.util.UUID;

import lombok.Data;

@Data
public class AddressResponse {
	private UUID address_id;
	private String addressline1;
	private String addressline2;
	private String city;
	private String state;
	private String zip;
	private String type;
}
