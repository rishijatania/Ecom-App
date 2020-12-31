package com.ecom.orderservice.payload.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdersListResponse {
	private List<OrderResponse> orders = new ArrayList<>();
}