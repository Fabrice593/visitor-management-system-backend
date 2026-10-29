package kigali.visitor.visitormanagementsystem.controllers;

import kigali.visitor.visitormanagementsystem.models.Visit;

import kigali.visitor.visitormanagementsystem.services.VisitService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    @Autowired
    private VisitService visitService;

    @PostMapping("/save")
    public ResponseEntity<String> saveVisit(@RequestBody Visit visit) {
        String message = visitService.saveVisit(visit);

        if (message.equals("Visit saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } else if (message.endsWith("does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateVisit(@PathVariable UUID id, @RequestBody Visit visit) {
        String message = visitService.updateVisit(id, visit);

        if (message.equals("Visit updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.endsWith("does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteVisit(@PathVariable UUID id) {
        String message = visitService.deleteVisit(id);

        if (message.equals("Visit deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.endsWith("does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public List<Visit> getAllVisits() {
        return visitService.getAllVisits();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVisitById(@PathVariable UUID id) {
        Optional<Visit> visit = visitService.getVisitById(id);

        if (visit.isPresent()) {
            return new ResponseEntity<>(visit.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("The visit with that id does not exist", HttpStatus.NOT_FOUND);
        }
    }
}
