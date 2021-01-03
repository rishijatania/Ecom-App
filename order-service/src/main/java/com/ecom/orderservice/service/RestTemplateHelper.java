package com.ecom.orderservice.service;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Future;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class RestTemplateHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateHelper.class);

	private RestTemplate restTemplate;
	private ObjectMapper objectMapper;

	@Autowired
	public RestTemplateHelper(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
		this.restTemplate = restTemplateBuilder.build();
		this.objectMapper = objectMapper;
	}

	@Async
	public <T> Future<List<T>> getDataList(URI url, ParameterizedTypeReference<List<T>> type)
			throws InterruptedException, IOException {
		RequestEntity<Void> request = RequestEntity.get(url).accept(MediaType.APPLICATION_JSON).build();
		LOGGER.debug("Requesting: " + request.toString());
		List<T> body = restTemplate.exchange(request, type).getBody();
		LOGGER.debug("Received: " + body.toString());
		return new AsyncResult<>(body);
	}

	@Async
	public <T, E> Future<?> getForEntity(Class<T> clazz, Class<E> errorClazz, String url,
			MultiValueMap<String, String> headers, Object... uriVariables) {
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
					new HttpEntity<Object>(headers), String.class, uriVariables);

			JavaType javaType = null;
			if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
				javaType = objectMapper.getTypeFactory().constructType(clazz);
			} else {
				javaType = objectMapper.getTypeFactory().constructType(errorClazz);
			}
			return new AsyncResult<>(readValue(response, javaType));
		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
				LOGGER.info("No data found {}", url);
			} else {
				LOGGER.info("rest client exception", exception.getMessage());
			}
			throw exception;
		}
	}

	public <T> List<T> getForList(Class<T> clazz, String url, Object... uriVariables) {
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, uriVariables);
			CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
			return readValue(response, collectionType);
		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
				LOGGER.info("No data found {}", url);
			} else {
				LOGGER.info("rest client exception", exception.getMessage());
			}
		}
		return null;
	}

	// public <T, R> T postForEntity(Class<T> clazz, String url, R body, Object...
	// uriVariables) {
	// HttpEntity<R> request = new HttpEntity<>(body);
	// ResponseEntity<String> response = restTemplate.postForEntity(url, request,
	// String.class, uriVariables);
	// JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
	// return readValue(response, javaType);
	// }

	@Async
	public <T, E> Future<?> postForEntity(Class<T> clazz, Class<E> errorClazz, String url,
			MultiValueMap<String, String> headers, Object body, Object... uriVariables) {
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,
					new HttpEntity<Object>(body, headers), String.class, uriVariables);

			JavaType javaType = null;
			if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
				javaType = objectMapper.getTypeFactory().constructType(clazz);
			} else {
				javaType = objectMapper.getTypeFactory().constructType(errorClazz);
			}
			return new AsyncResult<>(readValue(response, javaType));
		} catch (HttpClientErrorException exception) {
			if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
				LOGGER.info("No data found {}", url);
			} else {
				LOGGER.info("rest client exception", exception.getMessage());
			}
			throw exception;
		}
	}

	public <T, R> T putForEntity(Class<T> clazz, String url, R body, Object... uriVariables) {
		HttpEntity<R> request = new HttpEntity<>(body);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class,
				uriVariables);
		JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
		return readValue(response, javaType);
	}

	public void delete(String url, Object... uriVariables) {
		try {
			restTemplate.delete(url, uriVariables);
		} catch (RestClientException exception) {
			LOGGER.info(exception.getMessage());
		}
	}

	private <T> T readValue(ResponseEntity<String> response, JavaType javaType) {
		T result = null;
		try {
			result = objectMapper.readValue(response.getBody(), javaType);
		} catch (IOException e) {
			LOGGER.info(e.getMessage());
		}
		return result;
	}

}