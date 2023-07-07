package com.example.RealTimeMessageSharingHub.repository;

import com.example.RealTimeMessageSharingHub.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String name);
}
