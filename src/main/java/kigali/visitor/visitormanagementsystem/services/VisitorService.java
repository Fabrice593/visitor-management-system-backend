package kigali.visitor.visitormanagementsystem.services;

import kigali.visitor.visitormanagementsystem.models.Visitor;
import kigali.visitor.visitormanagementsystem.repositories.VisitRepository;
import kigali.visitor.visitormanagementsystem.repositories.VisitorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VisitorService {

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Transactional
    public String saveVisitor(Visitor visitor) {

        String message = validateVisitor(visitor, null);

        if (message != null) {
            return message;
        }

        visitor.setId(null);
        visitorRepository.save(visitor);

        return "Visitor saved successfully";
    }

    @Transactional
    public String updateVisitor(UUID id, Visitor visitor) {

        Visitor existing = visitorRepository.findById(id).orElse(null);

        if (existing == null) {
            return "The visitor with that id does not exist";
        }

        String message = validateVisitor(visitor, id);

        if (message != null) {
            return message;
        }

        existing.setFirstName(visitor.getFirstName());
        existing.setLastName(visitor.getLastName());
        existing.setIdNumber(visitor.getIdNumber());
        existing.setPhone(visitor.getPhone());
        existing.setCompany(visitor.getCompany());
        visitorRepository.save(existing);

        return "Visitor updated successfully";
    }

    public List<Visitor> getAllVisitors() {
        return visitorRepository.findAll();
    }

    public Optional<Visitor> getVisitorById(UUID id) {
        return visitorRepository.findById(id);
    }

    @Transactional
    public String deleteVisitor(UUID id) {

        Visitor existing = visitorRepository.findById(id).orElse(null);

        if (existing == null) {
            return "The visitor with that id does not exist";
        }

        if (visitRepository.existsByVisitorId(id)) {
            return "That visitor has visits, delete them first";
        }

        visitorRepository.delete(existing);

        return "Visitor deleted successfully";
    }

    private String validateVisitor(Visitor visitor, UUID id) {

        if (visitor.getFirstName() == null || visitor.getFirstName().isBlank()) {
            return "Visitor first name is required";
        }

        if (visitor.getLastName() == null || visitor.getLastName().isBlank()) {
            return "Visitor last name is required";
        }

        if (visitor.getIdNumber() == null || visitor.getIdNumber().isBlank()) {
            return "Visitor ID number is required";
        }

        boolean duplicate = id == null
                ? visitorRepository.existsByIdNumber(visitor.getIdNumber())
                : visitorRepository.existsByIdNumberAndIdNot(visitor.getIdNumber(), id);

        if (duplicate) {
            return "A visitor with that ID number already exists";
        }

        return null;
    }
}
