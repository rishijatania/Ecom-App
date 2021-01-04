package com.ecom.orderservice.payload.response;

import lombok.Data;

@Data
public class ItemResponse {
	private String skuId;
	private String itemName;
	private int itemQuantity;
	private double itemCost;
	private String itemDescription;
}
