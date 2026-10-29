package kigali.visitor.visitormanagementsystem.repositories;

import kigali.visitor.visitormanagementsystem.models.VisitPurpose;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VisitPurposeRepository extends JpaRepository<VisitPurpose, UUID> {


}
