package kigali.visitor.visitormanagementsystem.services;

import kigali.visitor.visitormanagementsystem.models.Workstation;
import kigali.visitor.visitormanagementsystem.repositories.HostEmployeeRepository;
import kigali.visitor.visitormanagementsystem.repositories.WorkstationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WorkstationService {

    @Autowired
    private WorkstationRepository workstationRepository;

    @Autowired
    private HostEmployeeRepository hostEmployeeRepository;

    @Transactional
    public String saveWorkstation(Workstation workstation) {

        String message = validateWorkstation(workstation);

        if (message != null) {
            return message;
        }

        workstation.setId(null);
        workstationRepository.save(workstation);

        return "Workstation saved successfully";
    }

    @Transactional
    public String updateWorkstation(UUID id, Workstation workstation) {

        Workstation existing = workstationRepository.findById(id).orElse(null);

        if (existing == null) {
            return "The workstation with that id does not exist";
        }

        String message = validateWorkstation(workstation);

        if (message != null) {
            return message;
        }

        existing.setStationNumber(workstation.getStationNumber());
        existing.setName(workstation.getName());
        workstationRepository.save(existing);

        return "Workstation updated successfully";
    }

    public List<Workstation> getAllWorkstations() {
        return workstationRepository.findAll();
    }

    public Optional<Workstation> getWorkstationById(UUID id) {
        return workstationRepository.findById(id);
    }

    @Transactional
    public String deleteWorkstation(UUID id) {

        Workstation existing = workstationRepository.findById(id).orElse(null);

        if (existing == null) {
            return "The workstation with that id does not exist";
        }

        if (hostEmployeeRepository.existsByWorkstationId(id)) {
            return "That workstation is assigned to a host employee, remove the assignment first";
        }

        workstationRepository.delete(existing);

        return "Workstation deleted successfully";
    }

    private String validateWorkstation(Workstation workstation) {

        if (workstation.getName() == null || workstation.getName().isBlank()) {
            return "Workstation name is required";
        }

        return null;
    }
}
