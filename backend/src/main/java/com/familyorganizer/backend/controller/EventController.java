package com.familyorganizer.backend.controller;

import com.familyorganizer.backend.model.Event;
import com.familyorganizer.backend.model.User;
import com.familyorganizer.backend.security.CustomUserDetails;
import com.familyorganizer.backend.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<Event>> getFamilyEvents(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long familyId = userDetails.getUser().getFamily().getId();
        return ResponseEntity.ok(eventService.getEventsByFamilyId(familyId));
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(eventService.createEvent(event, user.getFamily().getId(), user.getUsername()));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}
