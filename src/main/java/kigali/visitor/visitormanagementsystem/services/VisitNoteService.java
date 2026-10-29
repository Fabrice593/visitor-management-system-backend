package kigali.visitor.visitormanagementsystem.services;

import kigali.visitor.visitormanagementsystem.models.Visit;
import kigali.visitor.visitormanagementsystem.models.VisitNote;
import kigali.visitor.visitormanagementsystem.models.VisitStatus;
import kigali.visitor.visitormanagementsystem.repositories.VisitNoteRepository;
import kigali.visitor.visitormanagementsystem.repositories.VisitRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class VisitNoteService {

    @Autowired
    private VisitNoteRepository visitNoteRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Transactional
    public String saveVisitNote(UUID visitId, VisitNote visitNote) {
        Visit visit = visitRepository.findById(visitId).orElse(null);

        if (visit == null) {
            return "The visit with that id does not exist";
        }

        if (visit.getStatus() != VisitStatus.CHECKED_OUT) {
            return "Visit notes are only for CHECKED_OUT visits";
        }

        visitNote.setId(null);
        visitNote.setLoggedDate(LocalDate.now());
        visitNote.setVisit(visit);
        visitNoteRepository.save(visitNote);

        return "Visit note saved successfully";
    }

    public List<VisitNote> getVisitorNoteHistory(String idNumber) {
        return visitNoteRepository.findVisitorNoteHistory(idNumber);
    }

    @Transactional
    public String deleteVisitNote(UUID id) {
        VisitNote visitNote = visitNoteRepository.findById(id).orElse(null);

        if (visitNote == null) {
            return "The visit note with that id does not exist";
        }

        visitNoteRepository.delete(visitNote);

        return "Visit note deleted successfully";
    }
}
