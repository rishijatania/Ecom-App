package com.ecom.orderservice.error.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadRequestException {
	private String timestamp;
	private int status;
	private String error;
	private String message;
	private String path;
}
