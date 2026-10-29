package kigali.visitor.visitormanagementsystem.repositories;

import kigali.visitor.visitormanagementsystem.models.Visitor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, UUID> {

    boolean existsByIdNumber(String idNumber);

    boolean existsByIdNumberAndIdNot(String idNumber, UUID id);
}
