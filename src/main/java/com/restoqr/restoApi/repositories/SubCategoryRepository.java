package com.restoqr.restoApi.repositories;

import com.restoqr.restoApi.models.SubCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubCategoryRepository extends MongoRepository<SubCategory, String> {

}
