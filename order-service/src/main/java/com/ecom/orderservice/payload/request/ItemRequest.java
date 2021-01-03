package com.ecom.orderservice.payload.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRequest {
	@NotNull
	@NotEmpty
	private String itemName;
	
	@NotNull
	private int itemQuantity;
}
