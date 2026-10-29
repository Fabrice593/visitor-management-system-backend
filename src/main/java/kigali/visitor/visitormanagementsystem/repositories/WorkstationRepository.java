package kigali.visitor.visitormanagementsystem.repositories;

import kigali.visitor.visitormanagementsystem.models.Workstation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkstationRepository extends JpaRepository<Workstation, UUID> {


}
