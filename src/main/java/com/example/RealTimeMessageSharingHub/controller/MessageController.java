import com.example.RealTimeMessageSharingHub.dto.MessageDTO;
import com.example.RealTimeMessageSharingHub.model.Message;
import com.example.RealTimeMessageSharingHub.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MessageRepository messageRepository;

    @Autowired
    public MessageController(KafkaTemplate<String, String> kafkaTemplate, MessageRepository messageRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.messageRepository = messageRepository;
    }

    @PostMapping
    public ResponseEntity<String> sendMessage(@RequestBody MessageDTO messageDTO) {
        String content = messageDTO.getContent();
        String sender = messageDTO.getSender();
        String receiver = messageDTO.getReceiver();

        if (!senderExists(sender)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sender not found!");
        }

        if (!receiverExists(receiver)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receiver not found!");
        }

        Message message = new Message(content, sender, receiver);
        messageRepository.save(message);

        // Enviar a mensagem para o Kafka
        kafkaTemplate.send("messages-topic", content);

        return ResponseEntity.status(HttpStatus.CREATED).body("Mensagem enviada com sucesso!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable("id") String id, MessageDTO messageDTO) {
        Optional<Message> optionalMessage = messageRepository.findById(messageDTO.getIdMessage());
        if (optionalMessage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(optionalMessage.get());
    }

    @PutMapping("/{id}")
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