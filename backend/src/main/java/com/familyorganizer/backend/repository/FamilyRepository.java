package com.familyorganizer.backend.repository;

import com.familyorganizer.backend.model.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FamilyRepository extends JpaRepository<Family, Long> {
    Optional<Family> findBySecretCode(String secretCode);
}
