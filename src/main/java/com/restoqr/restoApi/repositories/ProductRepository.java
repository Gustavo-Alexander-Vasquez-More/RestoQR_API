package com.restoqr.restoApi.repositories;

import com.restoqr.restoApi.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByNameProduct(String nameProduct);
    List<Product> findByNameProductContainingIgnoreCase(String nameProduct);
    // Buscar productos cuya lista de subCategory (ids) contiene el id dado
    List<Product> findBySubCategoryContaining(String subCategoryId);
}
