package com.restoqr.restoApi.repositories;

import com.restoqr.restoApi.models.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByNumOrden(int numOrden);
}
