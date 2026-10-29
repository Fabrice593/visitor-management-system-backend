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

        if (message.equals("Visit note saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } else if (message.equals("The visit with that id does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/visitor/{idNumber}")
    public List<VisitNote> getVisitorNoteHistory(@PathVariable String idNumber) {
        return visitNoteService.getVisitorNoteHistory(idNumber);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteVisitNote(@PathVariable UUID id) {
        String message = visitNoteService.deleteVisitNote(id);

        if (message.equals("Visit note deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }
}
