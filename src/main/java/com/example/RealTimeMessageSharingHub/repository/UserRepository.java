package com.example.RealTimeMessageSharingHub.repository;


import com.example.RealTimeMessageSharingHub.model.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(String username);
    public Boolean existsByUsername(String username);
}
