package com.ecom.orderservice.payload.request;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class AddressRequest {

	private UUID address_id;

	@NotBlank
	@NotNull
	private String addressline1;

	@NotBlank
	@NotNull
	private String addressline2;

	@NotBlank
	@NotNull
	private String city;

	@NotBlank
	@NotNull
	private String state;

	@NotBlank
	@NotNull
	@Pattern(regexp="^[0-9]{5}(?:-[0-9]{4})?$", message="Invalid zipcode!")
	private String zip;
	
	@NotBlank
	@NotNull
	private String type;
}
