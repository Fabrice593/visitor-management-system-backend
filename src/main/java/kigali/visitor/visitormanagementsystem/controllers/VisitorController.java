package kigali.visitor.visitormanagementsystem.controllers;

import kigali.visitor.visitormanagementsystem.models.Visitor;

import kigali.visitor.visitormanagementsystem.services.VisitorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/visitors")
public class VisitorController {

    @Autowired
    private VisitorService visitorService;

    @PostMapping("/save")
    public ResponseEntity<String> saveVisitor(@RequestBody Visitor visitor) {
        String message = visitorService.saveVisitor(visitor);

        HttpStatus httpStatus;

        if (message.equals("Visitor saved successfully")) {
            httpStatus = HttpStatus.CREATED;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (message.equals("A visitor with that ID number already exists")) {
            httpStatus = HttpStatus.CONFLICT;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateVisitor(@PathVariable UUID id, @RequestBody Visitor visitor) {
        String message = visitorService.updateVisitor(id, visitor);

        HttpStatus httpStatus;

        if (message.equals("Visitor updated successfully")) {
            httpStatus = HttpStatus.OK;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (message.equals("A visitor with that ID number already exists")) {
            httpStatus = HttpStatus.CONFLICT;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteVisitor(@PathVariable UUID id) {
        String message = visitorService.deleteVisitor(id);

        HttpStatus httpStatus;

        if (message.equals("Visitor deleted successfully")) {
            httpStatus = HttpStatus.OK;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else {
            httpStatus = HttpStatus.CONFLICT;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @GetMapping("/all")
    public List<Visitor> getAllVisitors() {
        return visitorService.getAllVisitors();
    }

    @GetMapping("/by-company")
    public List<Visitor> getVisitorsByCompany(@RequestParam String company) {
        return visitorService.getVisitorsByCompany(company);
    }

    @GetMapping("/of-host-employee/{hostEmployeeId}")
    public List<Visitor> getVisitorsOfHostEmployee(@PathVariable UUID hostEmployeeId) {
        return visitorService.getVisitorsOfHostEmployee(hostEmployeeId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVisitorById(@PathVariable UUID id) {
        Optional<Visitor> visitor = visitorService.getVisitorById(id);

        if (visitor.isPresent()) {
            return new ResponseEntity<>(visitor.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("The visitor with that id does not exist", HttpStatus.NOT_FOUND);
        }
    }
}
