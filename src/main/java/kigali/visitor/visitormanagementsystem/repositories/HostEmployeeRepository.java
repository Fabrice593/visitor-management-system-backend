package kigali.visitor.visitormanagementsystem.repositories;

import kigali.visitor.visitormanagementsystem.models.HostEmployee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HostEmployeeRepository extends JpaRepository<HostEmployee, UUID> {

    boolean existsByWorkstationId(UUID workstationId);

    boolean existsByVisitPurposesId(UUID visitPurposeId);
}
