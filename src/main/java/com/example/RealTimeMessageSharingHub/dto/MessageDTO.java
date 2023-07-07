package com.example.RealTimeMessageSharingHub.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageDTO {
    public String content;
    public String sender;
    public String receiver;
    public String idMessage;
}
