package com.ecom.orderservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

	@GetMapping("/apiHealthCheck")
	public String healthCheck() {
		return "Service Up and Running.";
	}
}
