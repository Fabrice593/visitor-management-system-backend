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

        HttpStatus httpStatus;

        if (message.equals("Visit purpose saved successfully")) {
            httpStatus = HttpStatus.CREATED;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateVisitPurpose(@PathVariable UUID id, @RequestBody VisitPurpose visitPurpose) {
        String message = visitPurposeService.updateVisitPurpose(id, visitPurpose);

        HttpStatus httpStatus;

        if (message.equals("Visit purpose updated successfully")) {
            httpStatus = HttpStatus.OK;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteVisitPurpose(@PathVariable UUID id) {
        String message = visitPurposeService.deleteVisitPurpose(id);

        HttpStatus httpStatus;

        if (message.equals("Visit purpose deleted successfully")) {
            httpStatus = HttpStatus.OK;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else {
            httpStatus = HttpStatus.CONFLICT;
        }

        return new ResponseEntity<>(message, httpStatus);
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
