package com.ecom.orderservice.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessageResponse {
	private String timestamp;
	private int status;
	private String error;
	private String message;
	private String path;
}
