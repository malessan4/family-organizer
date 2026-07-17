package com.familyorganizer.backend.service;

import com.familyorganizer.backend.model.Message;
import com.familyorganizer.backend.model.User;
import com.familyorganizer.backend.repository.MessageRepository;
import com.familyorganizer.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public List<Message> getFamilyHistory(Long familyId) {
        return messageRepository.findByFamilyIdOrderByTimestampAsc(familyId);
    }

    public Message saveMessage(String content, String username) {
        User sender = userRepository.findByUsername(username).orElseThrow();
        
        Message message = Message.builder()
                .content(content)
                .timestamp(LocalDateTime.now())
                .sender(sender)
                .family(sender.getFamily())
                .build();
                
        return messageRepository.save(message);
    }
}
