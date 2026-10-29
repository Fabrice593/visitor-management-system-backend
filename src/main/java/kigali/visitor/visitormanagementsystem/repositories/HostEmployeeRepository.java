package kigali.visitor.visitormanagementsystem.repositories;

import kigali.visitor.visitormanagementsystem.models.HostEmployee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HostEmployeeRepository extends JpaRepository<HostEmployee, UUID> {

    @Query("SELECT h FROM HostEmployee h JOIN h.visitPurposes p "
            + "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<HostEmployee> findByVisitPurposeName(@Param("name") String name);

    boolean existsByWorkstationId(UUID workstationId);

    boolean existsByWorkstationIdAndIdNot(UUID workstationId, UUID id);

    boolean existsByVisitPurposesId(UUID visitPurposeId);
}
