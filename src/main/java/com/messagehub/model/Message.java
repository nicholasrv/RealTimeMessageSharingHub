package com.messagehub.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Getter
@Setter

@Data
@Document
public class Message {

    @Id
    private String id;

    private String content;
    private String sender;
    private String receiver;

    @DBRef
    private UserEntity userEntity;

    public Message(String content, String sender, String receiver, UserEntity userEntity) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.userEntity = userEntity;
    }

}
