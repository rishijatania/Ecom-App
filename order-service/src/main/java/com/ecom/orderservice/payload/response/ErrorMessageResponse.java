package com.ecom.orderservice.payload.response;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;

@Data
public class ErrorMessageResponse {
	private String timestamp;
	private int status;
	private String error;
	private String message;
	private String path;

	public ErrorMessageResponse(int status, String error, String message, String path) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		this.timestamp = dateFormat.format(new Date());
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
	}
}
