package com.ecom.orderservice.models;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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
@Table (name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order {

	@Id
	@Column(unique = true)
	private Long orderID;
	
	private UUID order_customer_id;
	// private String order_item_name
	// private String order_item_qty

	private Double order_shipping_charges;

	// order_payment_method
	// order_payment_date
	// order_payment_confirmation_number

	@Column(name = "order_created_at", updatable = false)
	@Temporal(TemporalType.DATE)
	@CreatedDate
    private Date orderedAt;

	@Column(name = "order_updated_at")
	@Temporal(TemporalType.DATE)
	@LastModifiedDate
    private Date updatedAt;

	@Enumerated(EnumType.STRING)
    private OrderStatusEnum order_status;

    private Double order_subtotal;

    private Double order_tax;

    private Double total;

	@ManyToOne(optional = false)
    @JoinColumn
    private Address order_shipping_address;

	@ManyToOne(optional = false)
    @JoinColumn
    private Address order_billing_address;

    // @ManyToMany (cascade = CascadeType.ALL)
    // @JoinTable (name = "cart" , joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn (name = "item_id"))
    // private List<Item> items;

    // @ManyToOne (cascade = CascadeType.ALL)
    // @JoinColumn (name = "user_id")
    // private User user;
	
	@PreUpdate
	@PrePersist
	public void calcTotal() {
		DecimalFormat df = new DecimalFormat("00.00");
		this.total = Double.parseDouble(df.format((1 + (order_tax/100)) * order_subtotal));
	}
}