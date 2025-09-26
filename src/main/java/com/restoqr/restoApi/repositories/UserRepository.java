package com.restoqr.restoApi.repositories;
import com.restoqr.restoApi.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository  extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    @Query("{ 'group_id': ?0 }")
    List<User> findByGroupId(String group_id);

    @Query(value = "{ 'group_id': ?0 }", count = true)
    long countByGroupId(String group_id);
}
