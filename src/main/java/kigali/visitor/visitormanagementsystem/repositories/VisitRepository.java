package kigali.visitor.visitormanagementsystem.repositories;

import kigali.visitor.visitormanagementsystem.models.Visit;
import kigali.visitor.visitormanagementsystem.models.VisitStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface VisitRepository extends JpaRepository<Visit, UUID> {

    @Query("SELECT v.status, COUNT(v) FROM Visit v GROUP BY v.status")
    List<Object[]> countVisitsByStatus();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Visit v SET v.status = :denied "
            + "WHERE v.hostEmployee.id = :hostEmployeeId AND v.visitDate = :date "
            + "AND v.status <> :checkedOut")
    int cancelHostEmployeeDay(@Param("hostEmployeeId") UUID hostEmployeeId,
                              @Param("date") LocalDate date,
                              @Param("denied") VisitStatus denied,
                              @Param("checkedOut") VisitStatus checkedOut);

    List<Visit> findByStatusOrderByVisitDateAsc(VisitStatus status);

    List<Visit> findByVisitDateBetweenOrderByVisitDateAsc(LocalDate start, LocalDate end);

    boolean existsByHostEmployeeIdAndVisitDateAndStatusNot(
            UUID hostEmployeeId, LocalDate visitDate, VisitStatus status);

    boolean existsByHostEmployeeIdAndVisitDateAndStatusNotAndIdNot(
            UUID hostEmployeeId, LocalDate visitDate, VisitStatus status, UUID id);

    boolean existsByVisitorId(UUID visitorId);

    boolean existsByHostEmployeeId(UUID hostEmployeeId);
}
