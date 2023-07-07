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

        // Verificar se o remetente existe
        if (!senderExists(sender)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Remetente não encontrado!");
        }

        // Verificar se o destinatário existe
        if (!receiverExists(receiver)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Destinatário não encontrado!");
        }

        // Lógica para validar os dados e processar o envio da mensagem

        // Enviar a mensagem para o Kafka
        kafkaTemplate.send("messages-topic", content);

        return ResponseEntity.status(HttpStatus.CREATED).body("Mensagem enviada com sucesso!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessage(@PathVariable String id, MessageDTO messageDTO) {
        // Buscar a mensagem pelo ID no repositório
        Optional<Message> optionalMessage = messageRepository.findById(messageDTO.getIdMessage());
        if (optionalMessage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(optionalMessage.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable("id") String id, @RequestBody MessageDTO messageDTO) {
        // Buscar a mensagem pelo ID no repositório
        Message message = messageRepository.findById(messageDTO.getIdMessage()).orElse(null));
        if (message.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Atualizar os campos da mensagem com base nos dados fornecidos
        Message existingMessage = optionalExistingMessage.get();
        existingMessage.setContent(messageDTO.getContent());
        existingMessage.setSender(messageDTO.getSender());
        existingMessage.setReceiver(messageDTO.getReceiver());

        // Salvar a mensagem atualizada no repositório
        Message updatedMessage = messageRepository.save(existingMessage);

        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMessage(@PathVariable String id) {
        // Verificar se a mensagem existe antes de excluí-la
        if (!messageRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Excluir a mensagem pelo ID
        messageRepository.deleteById(id);

        return ResponseEntity.ok("Mensagem excluída com sucesso!");
    }

    private boolean senderExists(String sender) {
        // Implemente a lógica para verificar se o remetente existe no seu sistema
        // Retorne true se o remetente existir e false caso contrário
        // Exemplo:
        // return userService.userExists(sender);
        return true;
    }

    private boolean receiverExists(String receiver) {
        // Implemente a lógica para verificar se o destinatário existe no seu sistema
        // Retorne true se o destinatário existir e false caso contrário
        // Exemplo:
        // return userService.userExists(receiver);
        return true;
    }
}