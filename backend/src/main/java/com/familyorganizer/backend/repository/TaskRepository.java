package com.familyorganizer.backend.repository;

import com.familyorganizer.backend.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByFamilyId(Long familyId);
}
