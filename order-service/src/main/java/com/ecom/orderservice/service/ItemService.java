package com.ecom.orderservice.service;

import com.ecom.orderservice.models.Item;
import com.ecom.orderservice.models.ItemID;
import com.ecom.orderservice.models.Order;
import com.ecom.orderservice.payload.response.ItemResponseApi;
import com.ecom.orderservice.repository.ItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

	@Autowired
	private ItemRepository itemRepository;

	public Item saveItem(ItemResponseApi itemRes, Order order) {
		Item item = new Item();
		item.setItemID(new ItemID(order.getOrderID(),itemRes.getSkuId()));
		item.setItemName(itemRes.getItemName());
		item.setItemCost(itemRes.getItemCost());
		item.setItemQuantity(itemRes.getItemQuantity());
		item.setItemDescription(itemRes.getItemDescription());
		item.setOrder(order);
		return itemRepository.save(item);
	}
}
