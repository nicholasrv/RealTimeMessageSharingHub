package com.messagehub.controller;

import com.messagehub.dto.MessageDTO;
import com.messagehub.model.Message;
import com.messagehub.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MessageRepository messageRepository;

    @Autowired
    public MessageController(KafkaTemplate<String, String> kafkaTemplate, MessageRepository messageRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.messageRepository = messageRepository;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        if (messages.size() > 0){
            return new ResponseEntity<List<Message>>(messages, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("There are no messages.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/send")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> sendMessage(@RequestBody MessageDTO messageDTO) {

        String content = messageDTO.getContent();
        String sender = messageDTO.getSender();
        String receiver = messageDTO.getReceiver();

        Message message = new Message(content, sender, receiver);
        messageRepository.save(message);

        kafkaTemplate.send("messages-topic", content);

        return ResponseEntity.status(HttpStatus.CREATED).body("Mensagem enviada com sucesso!");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Message> getMessageById(@PathVariable("id") String id, MessageDTO messageDTO) {
        Optional<Message> optionalMessage = messageRepository.findById(messageDTO.getIdMessage());
        if (optionalMessage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(optionalMessage.get());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> updateMessage(@PathVariable("id") String id, @RequestBody MessageDTO messageDTO) {

        Message message = messageRepository.findById(messageDTO.getIdMessage()).orElse(null);
        if (message == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Message existingMessage = message;
        existingMessage.setContent(messageDTO.getContent());
        existingMessage.setSender(messageDTO.getSender());
        existingMessage.setReceiver(messageDTO.getReceiver());


        Message updatedMessage = messageRepository.save(existingMessage);

        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteMessage(@PathVariable("id") String id) {
        if (!messageRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        messageRepository.deleteById(id);

        return ResponseEntity.ok("Message Deleted!");
    }

    private boolean senderExists(String sender) {
        return messageRepository.existsBySender(sender);
    }

    private boolean receiverExists(String receiver) {
        return messageRepository.existsByReceiver(receiver);
    }
}