package kigali.visitor.visitormanagementsystem.services;

import kigali.visitor.visitormanagementsystem.models.VisitPurpose;
import kigali.visitor.visitormanagementsystem.repositories.HostEmployeeRepository;
import kigali.visitor.visitormanagementsystem.repositories.VisitPurposeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VisitPurposeService {

    @Autowired
    private VisitPurposeRepository visitPurposeRepository;

    @Autowired
    private HostEmployeeRepository hostEmployeeRepository;

    @Transactional
    public String saveVisitPurpose(VisitPurpose visitPurpose) {

        String message = validateVisitPurpose(visitPurpose);

        if (message != null) {
            return message;
        }

        visitPurpose.setId(null);
        visitPurposeRepository.save(visitPurpose);

        return "Visit purpose saved successfully";
    }

    @Transactional
    public String updateVisitPurpose(UUID id, VisitPurpose visitPurpose) {

        VisitPurpose existing = visitPurposeRepository.findById(id).orElse(null);

        if (existing == null) {
            return "The visit purpose with that id does not exist";
        }

        String message = validateVisitPurpose(visitPurpose);

        if (message != null) {
            return message;
        }

        existing.setName(visitPurpose.getName());
        visitPurposeRepository.save(existing);

        return "Visit purpose updated successfully";
    }

    public List<VisitPurpose> getAllVisitPurposes() {
        return visitPurposeRepository.findAll();
    }

    public List<VisitPurpose> getUnusedVisitPurposes() {
        return visitPurposeRepository.findUnusedVisitPurposes();
    }

    public Optional<VisitPurpose> getVisitPurposeById(UUID id) {
        return visitPurposeRepository.findById(id);
    }

    @Transactional
    public String deleteVisitPurpose(UUID id) {

        VisitPurpose existing = visitPurposeRepository.findById(id).orElse(null);

        if (existing == null) {
            return "The visit purpose with that id does not exist";
        }

        if (hostEmployeeRepository.existsByVisitPurposesId(id)) {
            return "That visit purpose is assigned to host employees, remove the links first";
        }

        visitPurposeRepository.delete(existing);

        return "Visit purpose deleted successfully";
    }

    private String validateVisitPurpose(VisitPurpose visitPurpose) {

        if (visitPurpose.getName() == null || visitPurpose.getName().isBlank()) {
            return "Visit purpose name is required";
        }

        return null;
    }
}
