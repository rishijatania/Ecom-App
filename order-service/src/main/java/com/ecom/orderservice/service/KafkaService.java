package com.ecom.orderservice.service;

import java.util.List;

import com.ecom.orderservice.payload.request.OrderCreateRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class KafkaService {

	private static final Logger LOG = LoggerFactory.getLogger(KafkaService.class);

	@Value(value = "${kafka.orderservice.create.topic}")
	private String orderCreateTopic;

	@Value(value = "${kafka.orderservice.cancel.topic}")
	private String orderCancelTopic;

	@Value(value = "${kafka.timeinterval.sleep:1000}")
	private Integer sleepTimeInterval;

	@Autowired
	private OrderService orderService;

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Async("threadPoolTaskExecutorForKafkaProducer")
	public void sendMessage(String topic, String service, Object message) {
		ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, service, message);
		future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
			@Override
			public void onSuccess(SendResult<String, Object> result) {
				System.out.println(
						"Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
			}

			@Override
			public void onFailure(Throwable ex) {
				System.out.println("Unable to send message=[" + message + "] due to : " + ex.getMessage());
			}
		});
	}

	@KafkaListener(topics = "${kafka.orderservice.create.topic}", containerFactory = "kafkaListenerContainerFactoryCreateOrder")
	public void listenGroupCreate(@Payload List<OrderCreateRequest> messages,
			@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys) {
		LOG.info("Starting to consume batch messages");
		try {
			for (int i = 0; i < messages.size(); i++) {
				LOG.info("received message Order ID='{}'", keys.get(i));
				LOG.debug("received message='{}'", messages.get(i));
				orderService.createBulkOrder(Long.parseLong(keys.get(i)), messages.get(i));
			}
			Thread.sleep(sleepTimeInterval);
		} catch (InterruptedException e) {
			LOG.error(e.getStackTrace().toString());
		}
		LOG.info("Messages consumed");
	}

	@KafkaListener(topics = "${kafka.orderservice.cancel.topic}", containerFactory = "kafkaListenerContainerFactoryUpdateOrder")
	public void listenGroupUpdate(@Payload List<List<Long>> messages) {
		LOG.info("Starting to consume batch messages");
		LOG.info("received message='{}'", messages);
		try {
			for (int i = 0; i < messages.size(); i++) {
				LOG.info("received message='{}'", messages.get(i));
				messages.get(i).forEach(id -> orderService.cancelBulkOrder(id));
			}
			Thread.sleep(sleepTimeInterval);
		} catch (InterruptedException e) {
			LOG.error(e.getStackTrace().toString());
		}
		LOG.info("Messages consumed");
	}
}
