package kigali.visitor.visitormanagementsystem.repositories;

import kigali.visitor.visitormanagementsystem.models.Workstation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkstationRepository extends JpaRepository<Workstation, UUID> {

    Optional<Workstation> findByStationNumber(int stationNumber);

    @Query("SELECT w FROM Workstation w WHERE NOT EXISTS "
            + "(SELECT h FROM HostEmployee h WHERE h.workstation = w) "
            + "ORDER BY w.stationNumber ASC")
    List<Workstation> findFreeWorkstations();
}
