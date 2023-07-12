package com.messagehub.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Role {
    @Id
    public String id;

    private String name;

    public Role(String name) {
        this.name = name;
    }
}
