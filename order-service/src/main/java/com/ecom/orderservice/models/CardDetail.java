package com.ecom.orderservice.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table (name = "cardDetails")
@EntityListeners(AuditingEntityListener.class)
public class CardDetail {

	@Id
    @GeneratedValue
	@Column
	private long id;
	
	private String brand;
	private String country;
	private int exp_month;
	private int exp_year;
	private String fingerprint;
	private String funding;
	private String last4;
	private String network;
	
	@CreatedDate
	@Temporal(TemporalType.DATE)
	@Column(name = "card_created_at",updatable = false)
    private Date created_at;

	@LastModifiedDate
	@Temporal(TemporalType.DATE)
	@Column(name = "card_updated_at")
    private Date updated_at;
}
