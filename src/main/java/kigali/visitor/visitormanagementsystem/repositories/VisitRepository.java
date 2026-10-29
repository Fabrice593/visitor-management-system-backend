package kigali.visitor.visitormanagementsystem.repositories;

import kigali.visitor.visitormanagementsystem.models.Visit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VisitRepository extends JpaRepository<Visit, UUID> {

    boolean existsByVisitorId(UUID visitorId);

    boolean existsByHostEmployeeId(UUID hostEmployeeId);
}
