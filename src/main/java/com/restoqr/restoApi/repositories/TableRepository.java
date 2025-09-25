package com.restoqr.restoApi.repositories;
import com.restoqr.restoApi.models.Table;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TableRepository extends MongoRepository<Table, String> {
    Optional<Table> findByNumber(int number);
}