package kigali.visitor.visitormanagementsystem.repositories;

import kigali.visitor.visitormanagementsystem.models.Visit;
import kigali.visitor.visitormanagementsystem.models.VisitStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface VisitRepository extends JpaRepository<Visit, UUID> {

    List<Visit> findByStatusOrderByVisitDateAsc(VisitStatus status);

    List<Visit> findByVisitDateBetweenOrderByVisitDateAsc(LocalDate start, LocalDate end);

    boolean existsByHostEmployeeIdAndVisitDateAndStatusNot(
            UUID hostEmployeeId, LocalDate visitDate, VisitStatus status);

    boolean existsByHostEmployeeIdAndVisitDateAndStatusNotAndIdNot(
            UUID hostEmployeeId, LocalDate visitDate, VisitStatus status, UUID id);

    boolean existsByVisitorId(UUID visitorId);

    boolean existsByHostEmployeeId(UUID hostEmployeeId);
}
