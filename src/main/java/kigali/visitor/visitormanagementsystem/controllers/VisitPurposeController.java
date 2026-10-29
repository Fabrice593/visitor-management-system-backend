package kigali.visitor.visitormanagementsystem.controllers;

import kigali.visitor.visitormanagementsystem.models.VisitPurpose;

import kigali.visitor.visitormanagementsystem.services.VisitPurposeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/visit-purposes")
public class VisitPurposeController {

    @Autowired
    private VisitPurposeService visitPurposeService;

    @PostMapping("/save")
    public ResponseEntity<String> saveVisitPurpose(@RequestBody VisitPurpose visitPurpose) {
        String message = visitPurposeService.saveVisitPurpose(visitPurpose);

        if (message.equals("Visit purpose saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } else if (message.endsWith("does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateVisitPurpose(@PathVariable UUID id, @RequestBody VisitPurpose visitPurpose) {
        String message = visitPurposeService.updateVisitPurpose(id, visitPurpose);

        if (message.equals("Visit purpose updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.endsWith("does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteVisitPurpose(@PathVariable UUID id) {
        String message = visitPurposeService.deleteVisitPurpose(id);

        if (message.equals("Visit purpose deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.endsWith("does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/all")
    public List<VisitPurpose> getAllVisitPurposes() {
        return visitPurposeService.getAllVisitPurposes();
    }

    @GetMapping("/unused")
    public List<VisitPurpose> getUnusedVisitPurposes() {
        return visitPurposeService.getUnusedVisitPurposes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVisitPurposeById(@PathVariable UUID id) {
        Optional<VisitPurpose> visitPurpose = visitPurposeService.getVisitPurposeById(id);

        if (visitPurpose.isPresent()) {
            return new ResponseEntity<>(visitPurpose.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("The visit purpose with that id does not exist", HttpStatus.NOT_FOUND);
        }
    }
}
