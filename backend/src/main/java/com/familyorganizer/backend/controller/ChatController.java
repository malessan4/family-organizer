package com.familyorganizer.backend.controller;

import com.familyorganizer.backend.dto.ChatMessageDto;
import com.familyorganizer.backend.model.Message;
import com.familyorganizer.backend.security.CustomUserDetails;
import com.familyorganizer.backend.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final MessageService messageService;

    // Endpoint REST para obtener el historial (usado al cargar la página de chat)
    @GetMapping("/api/messages")
    public ResponseEntity<List<Message>> getChatHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long familyId = userDetails.getUser().getFamily().getId();
        return ResponseEntity.ok(messageService.getFamilyHistory(familyId));
    }

    // Endpoint REST para enviar un mensaje (alternativa al WebSocket)
    @PostMapping("/api/messages/send")
    public ResponseEntity<Message> sendMessageRest(@RequestBody ChatMessageDto chatMessageDto) {
        Message saved = messageService.saveMessage(chatMessageDto.getContent(), chatMessageDto.getSenderUsername());
        return ResponseEntity.ok(saved);
    }

    // Endpoint WebSocket para enviar y recibir mensajes
    @MessageMapping("/chat/{familyId}")
    @SendTo("/topic/family/{familyId}")
    public Message sendMessage(@DestinationVariable Long familyId, @Payload ChatMessageDto chatMessageDto, Principal principal) {
        // En STOMP, si configuramos la seguridad, el principal será el usuario autenticado
        // Por ahora confiamos en el username que viaja en el payload (o podríamos interceptar el JWT en el WS)
        return messageService.saveMessage(chatMessageDto.getContent(), chatMessageDto.getSenderUsername());
    }
}
