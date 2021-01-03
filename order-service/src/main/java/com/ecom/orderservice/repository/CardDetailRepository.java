package com.ecom.orderservice.repository;

import com.ecom.orderservice.models.CardDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardDetailRepository extends JpaRepository<CardDetail, Long> {
}
