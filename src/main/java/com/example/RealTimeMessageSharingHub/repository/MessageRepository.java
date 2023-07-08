package com.example.RealTimeMessageSharingHub.repository;

import com.example.RealTimeMessageSharingHub.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {
    public boolean existsBySender(String sender);
    public boolean existsByReceiver(String receiver);
}
