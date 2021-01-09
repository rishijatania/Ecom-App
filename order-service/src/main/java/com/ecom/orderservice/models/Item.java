package com.ecom.orderservice.models;

import java.io.Serializable;
import java.text.DecimalFormat;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "items")
public class Item implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1080759618752192764L;

	@EmbeddedId
	private ItemID itemID = new ItemID();

	@MapsId("orderid")
	@JoinColumn(name = "orderID")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Order order;

	private String itemName;
	private int itemQuantity;
	private double itemCost;
	private double totalCost;
	private String itemDescription;

	@PreUpdate
	@PrePersist
	public void calcTotal() {
		DecimalFormat df = new DecimalFormat("00.00");
		this.totalCost = Double.parseDouble(df.format(itemCost * itemQuantity));
	}
}
