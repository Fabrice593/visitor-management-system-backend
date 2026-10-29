package kigali.visitor.visitormanagementsystem.controllers;

import kigali.visitor.visitormanagementsystem.models.Visit;
import kigali.visitor.visitormanagementsystem.models.VisitStatus;

import kigali.visitor.visitormanagementsystem.services.VisitService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
        } else if (message.equals("Host employee already receives a visitor on that date")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
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
        } else if (message.equals("Host employee already receives a visitor on that date")) {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> changeStatus(@PathVariable UUID id, @RequestParam VisitStatus status) {
        String message = visitService.changeStatus(id, status);

        if (message.equals("Visit status updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.equals("The visit with that id does not exist")) {
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

    @GetMapping("/by-status")
    public List<Visit> getVisitsByStatus(@RequestParam VisitStatus status) {
        return visitService.getVisitsByStatus(status);
    }

    @GetMapping("/between")
    public ResponseEntity<?> getVisitsBetween(@RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        if (endDate.isBefore(startDate)) {
            return new ResponseEntity<>("End date must not be before start date", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(visitService.getVisitsBetween(startDate, endDate), HttpStatus.OK);
        }
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> handleInvalidDate(DateTimeParseException exception) {
        return new ResponseEntity<>("Dates must use yyyy-MM-dd format", HttpStatus.BAD_REQUEST);
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
