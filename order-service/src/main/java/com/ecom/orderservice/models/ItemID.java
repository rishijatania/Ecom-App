package com.ecom.orderservice.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orderid == null) ? 0 : orderid.hashCode());
		result = prime * result + ((skuID == null) ? 0 : skuID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemID other = (ItemID) obj;
		if (orderid == null) {
			if (other.orderid != null)
				return false;
		} else if (!orderid.equals(other.orderid))
			return false;
		if (skuID == null) {
			if (other.skuID != null)
				return false;
		} else if (!skuID.equals(other.skuID))
			return false;
		return true;
	}
	

}
