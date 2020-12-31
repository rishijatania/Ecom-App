package com.ecom.orderservice.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.validation.Valid;

import com.ecom.orderservice.models.Order;
import com.ecom.orderservice.models.OrderStatusEnum;
import com.ecom.orderservice.models.User;
import com.ecom.orderservice.payload.request.OrderCreateRequest;
import com.ecom.orderservice.payload.response.ErrorMessageResponse;
import com.ecom.orderservice.payload.response.OrderResponse;
import com.ecom.orderservice.payload.response.OrdersListResponse;
import com.ecom.orderservice.service.OrderService;
import com.ecom.orderservice.service.RestTemplateHelper;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping(value = "/api/v1/orders", produces = "application/json", consumes = { "application/json", "*/*" })
public class OrdersController {
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderService orderService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private RestTemplateHelper restTemplateHelper;

	@Value(value = "${http.timeout:5}")
    private long timeout;

	@ApiOperation(httpMethod = "POST", value = "Create Order", response = OrderResponse.class, responseContainer = "")
	@ApiResponses(value = { 
			// @ApiResponse(code = 404, message = "Order Not Found!"),
			// @ApiResponse(code = 422, message = "Order Cannot Be Updated Once Cancelled!"),
			@ApiResponse(code = 500, message = "Order Create failed!") })
	@PostMapping("")
	public <T,E> ResponseEntity<?> createOrder(@Valid @RequestBody OrderCreateRequest orderReq) {
		// Get order id, check inventory, do payment, get customer, set transaction
		// Call API
		try{
			LOG.debug("Starting calls");
			Future<?> response = restTemplateHelper.getForEntity(User.class, ErrorMessageResponse.class,"https://jsonplaceholder.typicode.com/todos/{id}",Integer.valueOf(1));
			System.out.println(response.get(timeout, TimeUnit.SECONDS));
			
			LOG.debug("End of calls.");
			Order order = null;
			try {
				order = orderService.saveOrder(orderReq);
			} catch (Exception e) {
				return new ResponseEntity<>(
						new ErrorMessageResponse(DateToString(), 500, "Order Create failed!", "Unable to Save Order", "/orders"),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
			return ResponseEntity.ok(modelMapper.map(order, OrderResponse.class));
		} catch (TimeoutException e) {
			return new ResponseEntity<>(
						new ErrorMessageResponse(DateToString(), 500, "Order Create failed!", "Unable to create order due to timeout from one of the services.", "/orders"),
						HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (InterruptedException | ExecutionException e) {
			return new ResponseEntity<>(
						new ErrorMessageResponse(DateToString(), 500, "Order Create failed!", "Unable to create order due to unspecified IO error.", "/orders"),
						HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation(httpMethod = "GET", value = "Get All Orders", response = OrdersListResponse.class, responseContainer = "")
	@ApiResponses(value = {
			// @ApiResponse(code = 404, message = "Countries not found"),
			@ApiResponse(code = 500, message = "Orders Fetch All Failed") })
	@GetMapping("")
	public ResponseEntity<?> fetchAllOrders() {
		List<Order> orderList = null;
		OrdersListResponse orders = new OrdersListResponse();
		try {
			orderList = orderService.getAllOrders();
			orderList.forEach(order -> {
				orders.getOrders().add(modelMapper.map(order, OrderResponse.class));
			});
		} catch (Exception e) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), 500, "", "Orders Fetch All Failed!", "/orders"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return ResponseEntity.ok(orders);
	}

	@ApiOperation(httpMethod = "GET", value = "Get Order By ID", response = OrderResponse.class, responseContainer = "")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Order Not Found!"),
			@ApiResponse(code = 500, message = "Order Fetch By ID Failed!") })
	@GetMapping("/{orderID}")
	public ResponseEntity<?> fetchOrderByID(@PathVariable Long orderID) {
		Optional<Order> order = null;
		try {
			order = orderService.getOrderByOrderId(orderID);
			if (!order.isPresent()) {
				return new ResponseEntity<>(
						new ErrorMessageResponse(DateToString(), 404, "", "Order Not Found!", "/orders/{orderID}"),
						HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(
					new ErrorMessageResponse(DateToString(), 500, "", "Order Fetch By ID Failed!", "/orders/{orderID}"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return ResponseEntity.ok(modelMapper.map(order.get(), OrderResponse.class));
	}

	@ApiOperation(httpMethod = "POST", value = "Cancel Order By ID", response = OrderResponse.class, responseContainer = "")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Order Not Found!"),
			@ApiResponse(code = 422, message = "Order Cannot Be Updated Once Cancelled!"),
			@ApiResponse(code = 500, message = "Order Fetch By ID Failed!") })
	@PostMapping("/{orderID}/cancellation")
	public ResponseEntity<?> cancelOrderByID(@PathVariable Long orderID) {
		Optional<Order> order = null;
		Order cancelledOrder = null;
		try {
			order = orderService.getOrderByOrderId(orderID);
			if (!order.isPresent()) {
				return new ResponseEntity<>(new ErrorMessageResponse(DateToString(), 404, "", "Order Not Found!",
						"/orders/{orderID}/cancellation"), HttpStatus.NOT_FOUND);
			}
			if (OrderStatusEnum.ORDER_CANCELLED.equals(order.get().getOrder_status())) {
				return new ResponseEntity<>(new ErrorMessageResponse(DateToString(), 422, "",
						"Order cannot be updated once cancelled!", "/orders/{orderID}/cancellation"),
						HttpStatus.UNPROCESSABLE_ENTITY);
			}
			cancelledOrder = orderService.cancelOrder(order.get());
		} catch (Exception e) {
			return new ResponseEntity<>(new ErrorMessageResponse(DateToString(), 500, "", "Order Cancel By ID failed!",
					"/orders/{orderID}/cancellation"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return ResponseEntity.ok(modelMapper.map(cancelledOrder, OrderResponse.class));
	}

	public String DateToString() {
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		return dateFormat.format(date);
	}
}
