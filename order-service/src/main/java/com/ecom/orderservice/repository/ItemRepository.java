package com.ecom.orderservice.repository;

import java.util.Optional;

import com.ecom.orderservice.models.Item;
import com.ecom.orderservice.models.ItemID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, ItemID> {
	Optional<Item> findByItemID(ItemID itemID);
}
