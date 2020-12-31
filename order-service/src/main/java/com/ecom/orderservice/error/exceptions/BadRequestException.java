package com.ecom.orderservice.error.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BadRequestException {
	private String timestamp;
	private int status;
	private String error;
	private String message;
	private String path;
}
