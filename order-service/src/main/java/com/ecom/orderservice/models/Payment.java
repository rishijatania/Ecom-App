package com.ecom.orderservice.models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Data
@Entity
@Table (name = "payments")
@EntityListeners(AuditingEntityListener.class)
public class Payment {
	@Id
	private String id;
	private long amount;
	private String currency;
	private boolean paid;
	private String payment_method;

	@OneToOne(optional = false,cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	private CardDetail CardDetail;

	private String receipt_url;
	private String status;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinTable(name = "order_payments", joinColumns = { @JoinColumn(name = "payment_ID") }, inverseJoinColumns = {
		@JoinColumn(name = "order_ID", referencedColumnName = "orderID") })
	private Order order;
	
	@CreatedDate
	@Temporal(TemporalType.DATE)
	@Column(name = "payment_date",updatable = false)
	private Date payment_date;

}