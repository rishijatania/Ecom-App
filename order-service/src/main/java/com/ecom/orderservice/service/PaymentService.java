package com.ecom.orderservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.transaction.Transactional;
import javax.xml.bind.DatatypeConverter;

import com.ecom.orderservice.models.CardDetail;
import com.ecom.orderservice.models.Order;
import com.ecom.orderservice.models.Payment;
import com.ecom.orderservice.payload.request.OrderCreateRequest;
import com.ecom.orderservice.payload.request.PaymentRequest;
import com.ecom.orderservice.payload.response.PaymentResponseApi;
import com.ecom.orderservice.repository.CardDetailRepository;
import com.ecom.orderservice.repository.PaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@Transactional
public class PaymentService {

	@Value("${service.payment.api.url}")
	private String paymentURI;

	@Autowired
	private CardDetailRepository cardDetailRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private RestTemplateHelper restTemplateHelper;

	public Payment savePayment(PaymentResponseApi paymentsData, Order order) {

		CardDetail cardDetail = saveCardDetail(paymentsData);
		Payment payment = new Payment();
		payment.setId(paymentsData.getId());
		payment.setAmount(paymentsData.getAmount());
		payment.setCurrency(paymentsData.getCurrency());
		payment.setPaid(paymentsData.isPaid());
		payment.setPayment_method(paymentsData.getPayment_method_details().getType());
		payment.setReceipt_url(paymentsData.getReceipt_url());
		payment.setStatus(paymentsData.getStatus());
		payment.setOrder(order);
		payment.setCardDetail(cardDetail);

		return paymentRepository.save(payment);
	}

	public CardDetail saveCardDetail(PaymentResponseApi paymentsData) {
		CardDetail cardDetail = new CardDetail();
		cardDetail.setBrand(paymentsData.getPayment_method_details().getCard().getBrand());
		cardDetail.setCountry(paymentsData.getPayment_method_details().getCard().getCountry());
		cardDetail.setExp_month(paymentsData.getPayment_method_details().getCard().getExp_month());
		cardDetail.setExp_year(paymentsData.getPayment_method_details().getCard().getExp_year());
		cardDetail.setFingerprint(paymentsData.getPayment_method_details().getCard().getFingerprint());
		cardDetail.setFunding(paymentsData.getPayment_method_details().getCard().getFunding());
		cardDetail.setLast4(paymentsData.getPayment_method_details().getCard().getLast4());
		cardDetail.setNetwork(paymentsData.getPayment_method_details().getCard().getNetwork());
		return cardDetailRepository.save(cardDetail);
	}

	public MultiValueMap<String, String> generatePaymentsPayload(PaymentRequest payment) {
		// setting up the request body
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("amount", String.valueOf((long) (Double.parseDouble(payment.getAmount()) * 100)));
		body.add("currency", payment.getCurrency());
		body.add("source", payment.getStored_card_name());
		return body;
	}

	public MultiValueMap<String, String> getPaymentHeaders() {
		// set up the basic authentication header
		String authorizationHeader = "Basic "
				+ DatatypeConverter.printBase64Binary(("sk_test_4eC39HqLyjWDarjtT1zdp7dc" + ":" + "").getBytes());
		// setting up the request headers
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add("Authorization", authorizationHeader);
		return headers;
	}

	public List<Future<?>> intiatePayment(OrderCreateRequest orderReq) {

		List<Future<?>> paymentFuture = new ArrayList<>();
		for (PaymentRequest payment : orderReq.getPayments()) {
			Future<?> paymentResponse = restTemplateHelper.postForEntity(PaymentResponseApi.class,
					List.class, paymentURI, getPaymentHeaders(), generatePaymentsPayload(payment));
			paymentFuture.add(paymentResponse);
		}
		return paymentFuture;
	}
}
