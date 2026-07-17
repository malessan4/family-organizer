package com.familyorganizer.backend.repository;

import com.familyorganizer.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByFamilyIdOrderByTimestampAsc(Long familyId);
}
