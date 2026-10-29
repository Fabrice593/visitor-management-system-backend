package kigali.visitor.visitormanagementsystem.services;

import kigali.visitor.visitormanagementsystem.models.HostEmployee;
import kigali.visitor.visitormanagementsystem.models.Visit;
import kigali.visitor.visitormanagementsystem.models.VisitStatus;
import kigali.visitor.visitormanagementsystem.models.Visitor;
import kigali.visitor.visitormanagementsystem.repositories.HostEmployeeRepository;
import kigali.visitor.visitormanagementsystem.repositories.VisitRepository;
import kigali.visitor.visitormanagementsystem.repositories.VisitorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VisitService {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private HostEmployeeRepository hostEmployeeRepository;

    @Transactional
    public String saveVisit(Visit visit) {

        String message = validateVisit(visit);

        if (message != null) {
            return message;
        }

        visit.setId(null);

        if (visit.getStatus() == null) {
            visit.setStatus(VisitStatus.EXPECTED);
        }

        visitRepository.save(visit);

        return "Visit saved successfully";
    }

    @Transactional
    public String updateVisit(UUID id, Visit visit) {

        Visit existing = visitRepository.findById(id).orElse(null);

        if (existing == null) {
            return "The visit with that id does not exist";
        }

        String message = validateVisit(visit);

        if (message != null) {
            return message;
        }

        existing.setVisitDate(visit.getVisitDate());
        existing.setPurposeNote(visit.getPurposeNote());
        existing.setVisitor(visit.getVisitor());
        existing.setHostEmployee(visit.getHostEmployee());
        visitRepository.save(existing);

        return "Visit updated successfully";
    }

    public List<Visit> getAllVisits() {
        return visitRepository.findAll();
    }

    public List<Visit> getVisitsByStatus(VisitStatus status) {
        return visitRepository.findByStatusOrderByVisitDateAsc(status);
    }

    public List<Visit> getVisitsBetween(LocalDate start, LocalDate end) {
        return visitRepository.findByVisitDateBetweenOrderByVisitDateAsc(start, end);
    }

    public Optional<Visit> getVisitById(UUID id) {
        return visitRepository.findById(id);
    }

    @Transactional
    public String deleteVisit(UUID id) {

        Visit existing = visitRepository.findById(id).orElse(null);

        if (existing == null) {
            return "The visit with that id does not exist";
        }

        visitRepository.delete(existing);

        return "Visit deleted successfully";
    }

    private String validateVisit(Visit visit) {

        if (visit.getVisitDate() == null) {
            return "Visit date is required";
        }

        if (visit.getVisitor() == null || visit.getVisitor().getId() == null) {
            return "Visitor is required";
        }

        if (visit.getHostEmployee() == null || visit.getHostEmployee().getId() == null) {
            return "Host employee is required";
        }

        Visitor visitor = visitorRepository.findById(visit.getVisitor().getId()).orElse(null);

        if (visitor == null) {
            return "The visitor with that id does not exist";
        }

        HostEmployee hostEmployee = hostEmployeeRepository.findById(visit.getHostEmployee().getId()).orElse(null);

        if (hostEmployee == null) {
            return "The host employee with that id does not exist";
        }

        visit.setVisitor(visitor);
        visit.setHostEmployee(hostEmployee);

        return null;
    }
}
