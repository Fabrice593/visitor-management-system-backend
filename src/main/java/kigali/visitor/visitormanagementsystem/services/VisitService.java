package kigali.visitor.visitormanagementsystem.services;

import kigali.visitor.visitormanagementsystem.models.HostEmployee;
import kigali.visitor.visitormanagementsystem.models.Visit;
import kigali.visitor.visitormanagementsystem.models.VisitStatus;
import kigali.visitor.visitormanagementsystem.models.Visitor;
import kigali.visitor.visitormanagementsystem.repositories.HostEmployeeRepository;
import kigali.visitor.visitormanagementsystem.repositories.VisitNoteRepository;
import kigali.visitor.visitormanagementsystem.repositories.VisitRepository;
import kigali.visitor.visitormanagementsystem.repositories.VisitorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private VisitNoteRepository visitNoteRepository;

    @Transactional
    public String saveVisit(Visit visit) {

        String message = validateAndResolveVisit(visit);

        if (message != null) {
            return message;
        }

        String scheduleMessage = validateSchedule(visit, null);

        if (scheduleMessage != null) {
            return scheduleMessage;
        }

        visit.setId(null);
        visit.setStatus(VisitStatus.EXPECTED);
        visitRepository.save(visit);

        return "Visit saved successfully";
    }

    @Transactional
    public String updateVisit(UUID id, Visit visit) {

        Visit existing = visitRepository.findById(id).orElse(null);

        if (existing == null) {
            return "The visit with that id does not exist";
        }

        String message = validateAndResolveVisit(visit);

        if (message != null) {
            return message;
        }

        if (hasScheduleChanged(existing, visit) && existing.getStatus() != VisitStatus.DENIED) {
            String scheduleMessage = validateSchedule(visit, id);

            if (scheduleMessage != null) {
                return scheduleMessage;
            }
        }

        existing.setVisitDate(visit.getVisitDate());
        existing.setPurposeNote(visit.getPurposeNote());
        existing.setVisitor(visit.getVisitor());
        existing.setHostEmployee(visit.getHostEmployee());
        visitRepository.save(existing);

        return "Visit updated successfully";
    }

    @Transactional
    public String changeStatus(UUID id, VisitStatus status) {
        Visit visit = visitRepository.findById(id).orElse(null);

        if (visit == null) {
            return "The visit with that id does not exist";
        }

        if (!canChangeStatus(visit.getStatus(), status)) {
            return "Cannot change status from " + visit.getStatus() + " to " + status;
        }

        visit.setStatus(status);
        visitRepository.save(visit);

        return "Visit status updated successfully";
    }

    private boolean canChangeStatus(VisitStatus currentStatus, VisitStatus newStatus) {
        if (currentStatus == VisitStatus.EXPECTED) {
            return newStatus == VisitStatus.CHECKED_IN || newStatus == VisitStatus.DENIED;
        }

        if (currentStatus == VisitStatus.CHECKED_IN) {
            return newStatus == VisitStatus.CHECKED_OUT || newStatus == VisitStatus.DENIED;
        }

        return false;
    }

    public List<Visit> getAllVisits() {
        return visitRepository.findAll();
    }

    public Page<Visit> getVisitsPage(Pageable pageable) {
        return visitRepository.findAll(pageable);
    }

    public List<Object[]> getVisitCountsByStatus() {
        return visitRepository.countVisitsByStatus();
    }

    @Transactional
    public String cancelHostEmployeeDay(UUID hostEmployeeId, LocalDate date) {
        int cancelled = visitRepository.cancelHostEmployeeDay(
                hostEmployeeId, date, VisitStatus.DENIED, VisitStatus.CHECKED_OUT);

        return cancelled + " visits cancelled";
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

        if (visitNoteRepository.existsByVisit(existing)) {
            return "That visit has visit notes, delete them first";
        }

        visitRepository.delete(existing);

        return "Visit deleted successfully";
    }

    private boolean hasScheduleChanged(Visit existing, Visit requested) {
        return !existing.getVisitDate().equals(requested.getVisitDate())
                || !existing.getHostEmployee().getId().equals(requested.getHostEmployee().getId());
    }

    private String validateSchedule(Visit visit, UUID excludedVisitId) {
        if (visit.getVisitDate().isBefore(LocalDate.now())) {
            return "Visit date cannot be in the past";
        }

        UUID hostEmployeeId = visit.getHostEmployee().getId();
        LocalDate visitDate = visit.getVisitDate();
        boolean occupied = excludedVisitId == null
                ? visitRepository.existsByHostEmployeeIdAndVisitDateAndStatusNot(
                        hostEmployeeId, visitDate, VisitStatus.DENIED)
                : visitRepository.existsByHostEmployeeIdAndVisitDateAndStatusNotAndIdNot(
                        hostEmployeeId, visitDate, VisitStatus.DENIED, excludedVisitId);

        if (occupied) {
            return "Host employee already receives a visitor on that date";
        }

        return null;
    }

    private String validateAndResolveVisit(Visit visit) {

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
