package com.ecom.orderservice.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessageResponse {
	private String timestamp;
	private int status;
	private String error;
	private String message;
	private String path;
}
