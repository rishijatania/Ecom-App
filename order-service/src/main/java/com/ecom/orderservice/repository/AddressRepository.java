package com.ecom.orderservice.repository;

import java.util.UUID;

import com.ecom.orderservice.models.Address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
}
