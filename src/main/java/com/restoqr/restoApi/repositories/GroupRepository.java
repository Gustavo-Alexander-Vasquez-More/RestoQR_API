package com.restoqr.restoApi.repositories;

import com.restoqr.restoApi.models.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupRepository extends MongoRepository<Group,String> {

}
