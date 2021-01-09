package com.ecom.orderservice.models;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Data
@Entity
@Table(name = "address")
@EntityListeners(AuditingEntityListener.class)
public class Address {

	@Id
	@GeneratedValue
	@Column
	private UUID address_id;

	private String addressline1;

	private String addressline2;

	private String city;

	private String state;

	private String zip;

	@Enumerated(EnumType.STRING)
	private AddressTypeEnum type;

	@CreatedDate
	@Temporal(TemporalType.DATE)
	@Column(name = "address_created_at", updatable = false)
	private Date created_at;

	@LastModifiedDate
	@Temporal(TemporalType.DATE)
	@Column(name = "address_updated_at")
	private Date updated_at;
}
