package kigali.visitor.visitormanagementsystem.services;

import kigali.visitor.visitormanagementsystem.models.HostEmployee;
import kigali.visitor.visitormanagementsystem.models.VisitPurpose;
import kigali.visitor.visitormanagementsystem.models.Workstation;
import kigali.visitor.visitormanagementsystem.repositories.HostEmployeeRepository;
import kigali.visitor.visitormanagementsystem.repositories.VisitPurposeRepository;
import kigali.visitor.visitormanagementsystem.repositories.VisitRepository;
import kigali.visitor.visitormanagementsystem.repositories.WorkstationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HostEmployeeService {

    @Autowired
    private HostEmployeeRepository hostEmployeeRepository;

    @Autowired
    private WorkstationRepository workstationRepository;

    @Autowired
    private VisitPurposeRepository visitPurposeRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Transactional
    public String saveHostEmployee(HostEmployee hostEmployee) {

        String message = validateHostEmployee(hostEmployee, null);

        if (message != null) {
            return message;
        }

        hostEmployee.setId(null);
        hostEmployeeRepository.save(hostEmployee);

        return "Host employee saved successfully";
    }

    @Transactional
    public String updateHostEmployee(UUID id, HostEmployee hostEmployee) {

        HostEmployee existing = hostEmployeeRepository.findById(id).orElse(null);

        if (existing == null) {
            return "The host employee with that id does not exist";
        }

        String message = validateHostEmployee(hostEmployee, id);

        if (message != null) {
            return message;
        }

        existing.setFirstName(hostEmployee.getFirstName());
        existing.setLastName(hostEmployee.getLastName());
        existing.setEmail(hostEmployee.getEmail());
        existing.setWorkstation(hostEmployee.getWorkstation());
        existing.setVisitPurposes(hostEmployee.getVisitPurposes());
        hostEmployeeRepository.save(existing);

        return "Host employee updated successfully";
    }

    public List<HostEmployee> getAllHostEmployees() {
        return hostEmployeeRepository.findAll();
    }

    public List<HostEmployee> getHostEmployeesByVisitPurpose(String name) {
        return hostEmployeeRepository.findByVisitPurposeName(name);
    }

    public Optional<HostEmployee> getHostEmployeeById(UUID id) {
        return hostEmployeeRepository.findById(id);
    }

    @Transactional
    public String deleteHostEmployee(UUID id) {

        HostEmployee existing = hostEmployeeRepository.findById(id).orElse(null);

        if (existing == null) {
            return "The host employee with that id does not exist";
        }

        if (visitRepository.existsByHostEmployeeId(id)) {
            return "That host employee has visits, delete them first";
        }

        hostEmployeeRepository.delete(existing);

        return "Host employee deleted successfully";
    }

    @Transactional
    public String addVisitPurpose(UUID id, UUID purposeId) {
        HostEmployee hostEmployee = hostEmployeeRepository.findById(id).orElse(null);

        if (hostEmployee == null) {
            return "The host employee with that id does not exist";
        }

        VisitPurpose visitPurpose = visitPurposeRepository.findById(purposeId).orElse(null);

        if (visitPurpose == null) {
            return "The visit purpose with that id does not exist";
        }

        if (hostEmployee.getVisitPurposes().contains(visitPurpose)) {
            return "The host employee already has that visit purpose";
        }

        hostEmployee.getVisitPurposes().add(visitPurpose);
        hostEmployeeRepository.save(hostEmployee);

        return "Visit purpose added to the host employee";
    }

    @Transactional
    public String changeWorkstation(UUID id, int stationNumber) {
        HostEmployee hostEmployee = hostEmployeeRepository.findById(id).orElse(null);

        if (hostEmployee == null) {
            return "The host employee with that id does not exist";
        }

        Workstation workstation = workstationRepository.findByStationNumber(stationNumber).orElse(null);

        if (workstation == null) {
            return "The workstation with that station number does not exist";
        }

        if (hostEmployeeRepository.existsByWorkstationIdAndIdNot(workstation.getId(), id)) {
            return "That workstation is taken by another host employee";
        }

        hostEmployee.setWorkstation(workstation);
        hostEmployeeRepository.save(hostEmployee);

        return "Workstation changed successfully";
    }

    private String validateHostEmployee(HostEmployee hostEmployee, UUID id) {

        if (hostEmployee.getFirstName() == null || hostEmployee.getFirstName().isBlank()) {
            return "Host employee first name is required";
        }

        if (hostEmployee.getLastName() == null || hostEmployee.getLastName().isBlank()) {
            return "Host employee last name is required";
        }

        if (hostEmployee.getEmail() == null || hostEmployee.getEmail().isBlank()) {
            return "Host employee email is required";
        }

        if (hostEmployee.getWorkstation() != null) {
            if (hostEmployee.getWorkstation().getId() == null) {
                return "Workstation ID is required";
            }

            Workstation workstation = workstationRepository.findById(hostEmployee.getWorkstation().getId()).orElse(null);

            if (workstation == null) {
                return "The workstation with that id does not exist";
            }

            boolean taken = id == null
                    ? hostEmployeeRepository.existsByWorkstationId(workstation.getId())
                    : hostEmployeeRepository.existsByWorkstationIdAndIdNot(workstation.getId(), id);

            if (taken) {
                return "That workstation is taken by another host employee";
            }

            hostEmployee.setWorkstation(workstation);
        }

        List<VisitPurpose> visitPurposes = new ArrayList<>();

        if (hostEmployee.getVisitPurposes() != null) {
            for (VisitPurpose item : hostEmployee.getVisitPurposes()) {
                if (item == null || item.getId() == null) {
                    return "Visit purpose ID is required";
                }

                VisitPurpose visitPurpose = visitPurposeRepository.findById(item.getId()).orElse(null);

                if (visitPurpose == null) {
                    return "The visit purpose with that id does not exist";
                }

                if (!visitPurposes.contains(visitPurpose)) {
                    visitPurposes.add(visitPurpose);
                }
            }
        }

        hostEmployee.setVisitPurposes(visitPurposes);

        return null;
    }
}
