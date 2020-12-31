package com.ecom.orderservice.repository;

import java.util.Optional;

import com.ecom.orderservice.models.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	Optional<Order> findByOrderID(Long orderID);
}
