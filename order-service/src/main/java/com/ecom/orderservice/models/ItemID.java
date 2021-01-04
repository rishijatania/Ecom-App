package com.ecom.orderservice.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ItemID implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Long orderid;
	
	@Column
	private String skuID;

}
