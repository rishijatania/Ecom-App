package com.ecom.orderservice.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponseApi {
	private String id;
	private String skuId;
	private String itemName;
	private int itemQuantity;
	private double itemCost;
	private String itemDescription;
}
