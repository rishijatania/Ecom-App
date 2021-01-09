package com.ecom.orderservice.models;

import lombok.Data;

@Data
public class User {
	private int userId;
	private int id;
	private String title;
	private boolean completed;
}
