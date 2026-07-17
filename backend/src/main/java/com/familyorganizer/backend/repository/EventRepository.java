package com.familyorganizer.backend.repository;

import com.familyorganizer.backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByFamilyId(Long familyId);
}
