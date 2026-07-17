package com.familyorganizer.backend.dto;

import lombok.Data;

@Data
public class ChatMessageDto {
    private String content;
    private String senderUsername;
}
