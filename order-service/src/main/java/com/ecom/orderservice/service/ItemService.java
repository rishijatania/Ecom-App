package com.ecom.orderservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.ecom.orderservice.models.Item;
import com.ecom.orderservice.models.ItemID;
import com.ecom.orderservice.models.Order;
import com.ecom.orderservice.payload.request.ItemRequest;
import com.ecom.orderservice.payload.request.OrderCreateRequest;
import com.ecom.orderservice.payload.response.ErrorMessageResponse;
import com.ecom.orderservice.payload.response.ItemResponseApi;
import com.ecom.orderservice.repository.ItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

	@Value("${service.inventory.api.inventoryCheck}")
	private String inventoryServiceURI;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private RestTemplateHelper restTemplateHelper;

	public Item saveItem(ItemResponseApi itemRes, Order order) {
		Item item = new Item();
		item.setItemID(new ItemID(order.getOrderID(), itemRes.getSkuId()));
		item.setItemName(itemRes.getItemName());
		item.setItemCost(itemRes.getItemCost());
		item.setItemQuantity(itemRes.getItemQuantity());
		item.setItemDescription(itemRes.getItemDescription());
		item.setOrder(order);
		return itemRepository.save(item);
	}

	public List<Future<?>> initiateInventoryCheck(OrderCreateRequest orderReq) {
		List<Future<?>> itemFuture = new ArrayList<>();
		for (ItemRequest item : orderReq.getItems()) {
			Future<?> itemResponse = restTemplateHelper.getForEntity(ItemResponseApi.class, ErrorMessageResponse.class,
					List.class, inventoryServiceURI, null, item.getItemName());
			itemFuture.add(itemResponse);
		}
		return itemFuture;
	}
}
