package com.ecom.orderservice.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.ecom.orderservice.payload.request.BulkOrdersCancelRequest;
import com.ecom.orderservice.payload.request.BulkOrdersCreateRequest;
import com.ecom.orderservice.payload.response.MessageResponse;
import com.ecom.orderservice.service.KafkaService;
import com.ecom.orderservice.util.SequenceGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "OrderResource")
@RequestMapping(value = "/api/v1/bulkOrders", produces = "application/json", consumes = { "application/json", "*/*" })
public class BulkOrdersController {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	private KafkaService kafkaService;

	@Value(value = "${kafka.orderservice.create.topic}")
	private String orderCreateTopic;

	@Value(value = "${kafka.orderservice.cancel.topic}")
	private String orderCancelTopic;

	@Autowired
	private SequenceGenerator squenceGenerator;

	@ApiOperation(httpMethod = "POST", value = "Bulk Create Order", response = String.class, responseContainer = "")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Order Item Not Found!"),
			@ApiResponse(code = 400, message = "Order Bad Input Data!"),
			@ApiResponse(code = 500, message = "Order Create failed!") })
	@PostMapping("")
	public <T, E> ResponseEntity<?> createBulkOrders(@Valid @RequestBody BulkOrdersCreateRequest ordersReq) {

		LOG.debug("Starting calls");
		List<Long> orderIds = new ArrayList<>();
		try {
			ordersReq.getOrders().forEach(order -> {
				Long id = squenceGenerator.nextId();
				orderIds.add(id);
				kafkaService.sendMessage(orderCreateTopic, id.toString(), order);
			});
		} catch (Exception e) {
			LOG.debug(e.getStackTrace().toString());
		}

		return ResponseEntity.ok(orderIds);
	}

	@ApiOperation(httpMethod = "POST", value = "Bulk Cancel Order", response = String.class, responseContainer = "")
	@ApiResponses(value = { @ApiResponse(code = 202, message = "Bulk Order Cancel request Accepted") })
	@PostMapping("/cancellation")
	public <T, E> ResponseEntity<?> updateBulkOrders(@Valid @RequestBody BulkOrdersCancelRequest ordersReq) {

		LOG.debug("Starting calls");
		try {
			kafkaService.sendMessage(orderCancelTopic, "/api/v1/bulkOrders/cancellation", ordersReq.getOrders());
		} catch (Exception e) {
			LOG.debug(e.getStackTrace().toString());
		}

		return new ResponseEntity<>(new MessageResponse("Bulk Order Cancel request Accepted"), HttpStatus.ACCEPTED);
	}
}
