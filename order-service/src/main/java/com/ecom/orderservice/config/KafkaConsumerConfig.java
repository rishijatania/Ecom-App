package com.ecom.orderservice.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ecom.orderservice.payload.request.OrderCreateRequest;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.BatchLoggingErrorHandler;
import org.springframework.kafka.listener.LoggingErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

	@Value(value = "${kafka.bootstrapAddress}")
	private String bootstrapAddress;

	@Value(value = "${kafka.groupId}")
	private String groupId;

	@Value(value = "${kafka.auto.offset.reset}")
	private String reset;

	@Value(value = "${kafka.enable.auto.commit}")
	private Boolean autoCommit;

	@Value(value = "${kafka.max.poll.records}")
	private String max_poll_records;

	@Bean
	public ConsumerFactory<String, OrderCreateRequest> consumerFactoryCreateOrder() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, reset);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, max_poll_records);
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000");
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.orderservice");
		ErrorHandlingDeserializer<OrderCreateRequest> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(
				new JsonDeserializer<>(OrderCreateRequest.class, objectMapper()));
		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), errorHandlingDeserializer);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, OrderCreateRequest> kafkaListenerContainerFactoryCreateOrder() {

		ConcurrentKafkaListenerContainerFactory<String, OrderCreateRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactoryCreateOrder());
		factory.setBatchListener(true);
		factory.getContainerProperties();
		factory.setBatchErrorHandler(new BatchLoggingErrorHandler());
		return factory;
	}

	@Bean
	public ConsumerFactory<String, List<Long>> consumerFactoryUpdateOrder() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, reset);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, max_poll_records);
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000");
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.orderservice");
		ErrorHandlingDeserializer<List<Long>> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(
				new JsonDeserializer<>(List.class, objectMapper()));
		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), errorHandlingDeserializer);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, List<Long>> kafkaListenerContainerFactoryUpdateOrder() {

		ConcurrentKafkaListenerContainerFactory<String, List<Long>> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactoryUpdateOrder());
		factory.setBatchListener(true);
		factory.getContainerProperties();
		factory.setBatchErrorHandler(new BatchLoggingErrorHandler());
		return factory;
	}

	@Bean
	public LoggingErrorHandler errorHandler() {
		return new LoggingErrorHandler();
	}

	private ObjectMapper objectMapper() {
		return Jackson2ObjectMapperBuilder.json().visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
				.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
	}
}
