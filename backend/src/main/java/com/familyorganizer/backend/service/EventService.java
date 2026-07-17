package com.familyorganizer.backend.service;

import com.familyorganizer.backend.model.Event;
import com.familyorganizer.backend.model.User;
import com.familyorganizer.backend.repository.EventRepository;
import com.familyorganizer.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public List<Event> getEventsByFamilyId(Long familyId) {
        return eventRepository.findByFamilyId(familyId);
    }

    public Event createEvent(Event event, Long familyId, String username) {
        User creator = userRepository.findByUsername(username).orElseThrow();
        event.setFamily(creator.getFamily());
        event.setCreatedBy(creator);
        return eventRepository.save(event);
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }
}
