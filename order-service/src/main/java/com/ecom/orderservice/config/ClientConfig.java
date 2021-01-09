package com.ecom.orderservice.config;

import com.ecom.orderservice.util.SequenceGenerator;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

	@Value("${spring.nodeID:0}")
	private int nodeID;

	// @Bean
	// public ErrorDecoder errorDecoder() {
	// return new CustomErrorDecoder();
	// }

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public SequenceGenerator sequenceGenerator() {
		if (nodeID == 0) {
			return new SequenceGenerator();
		}
		return new SequenceGenerator(nodeID);
	}

}
