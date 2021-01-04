package com.ecom.orderservice.payload.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ItemRequest {
	@NotNull
	@NotEmpty
	private String itemName;
	
	@NotNull
	private int itemQuantity;
}
