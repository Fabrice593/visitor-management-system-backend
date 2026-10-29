package kigali.visitor.visitormanagementsystem.repositories;

import kigali.visitor.visitormanagementsystem.models.Visit;
import kigali.visitor.visitormanagementsystem.models.VisitNote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VisitNoteRepository extends JpaRepository<VisitNote, UUID> {

    @Query("SELECT n FROM VisitNote n WHERE n.visit.visitor.idNumber = :idNumber "
            + "ORDER BY n.loggedDate DESC")
    List<VisitNote> findVisitorNoteHistory(@Param("idNumber") String idNumber);

    boolean existsByVisit(Visit visit);
}
