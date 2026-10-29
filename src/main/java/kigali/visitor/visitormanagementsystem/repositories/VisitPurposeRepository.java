package kigali.visitor.visitormanagementsystem.repositories;

import kigali.visitor.visitormanagementsystem.models.VisitPurpose;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VisitPurposeRepository extends JpaRepository<VisitPurpose, UUID> {

    @Query("SELECT p FROM VisitPurpose p WHERE p.hostEmployees IS EMPTY")
    List<VisitPurpose> findUnusedVisitPurposes();
}
