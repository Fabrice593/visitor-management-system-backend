package kigali.visitor.visitormanagementsystem.controllers;

import kigali.visitor.visitormanagementsystem.models.VisitNote;
import kigali.visitor.visitormanagementsystem.services.VisitNoteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/visit-notes")
public class VisitNoteController {

    @Autowired
    private VisitNoteService visitNoteService;

    @PostMapping("/save")
    public ResponseEntity<String> saveVisitNote(@RequestParam UUID visitId, @RequestBody VisitNote visitNote) {
        String message = visitNoteService.saveVisitNote(visitId, visitNote);

        HttpStatus httpStatus;

        if (message.equals("Visit note saved successfully")) {
            httpStatus = HttpStatus.CREATED;
        } else if (message.equals("The visit with that id does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @GetMapping("/visitor/{idNumber}")
    public List<VisitNote> getVisitorNoteHistory(@PathVariable String idNumber) {
        return visitNoteService.getVisitorNoteHistory(idNumber);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteVisitNote(@PathVariable UUID id) {
        String message = visitNoteService.deleteVisitNote(id);

        HttpStatus httpStatus;

        if (message.equals("Visit note deleted successfully")) {
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.NOT_FOUND;
        }

        return new ResponseEntity<>(message, httpStatus);
    }
}
