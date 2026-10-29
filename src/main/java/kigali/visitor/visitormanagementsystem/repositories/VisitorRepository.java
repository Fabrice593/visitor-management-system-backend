package kigali.visitor.visitormanagementsystem.repositories;

import kigali.visitor.visitormanagementsystem.models.Visitor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, UUID> {

    List<Visitor> findByCompanyIgnoreCaseOrderByLastNameAsc(String company);

    @Query("SELECT DISTINCT v.visitor FROM Visit v WHERE v.hostEmployee.id = :hostEmployeeId")
    List<Visitor> findVisitorsOfHostEmployee(@Param("hostEmployeeId") UUID hostEmployeeId);

    boolean existsByIdNumber(String idNumber);

    boolean existsByIdNumberAndIdNot(String idNumber, UUID id);
}
