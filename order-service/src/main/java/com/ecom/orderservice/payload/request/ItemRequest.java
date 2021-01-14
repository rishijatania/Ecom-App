package com.ecom.orderservice.payload.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ItemRequest {
	@NotNull
	@NotEmpty
	private String itemName;

	@Min(1)
	private int itemQuantity;
}
