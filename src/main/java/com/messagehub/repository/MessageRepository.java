package com.messagehub.repository;


import com.messagehub.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    public boolean existsBySender(String sender);
    public boolean existsByReceiver(String receiver);
}
