package com.ecom.orderservice.config;

import java.util.concurrent.Executor;

import com.ecom.orderservice.error.handler.CustomAsyncExceptionHandler;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class SpringAsyncConfig extends AsyncConfigurerSupport {

	@Bean(name = "threadPoolTaskExecutorForKafkaProducer")
	public Executor threadPoolTaskExecutorForKafkaProducer() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.afterPropertiesSet();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new CustomAsyncExceptionHandler();
	}
}
