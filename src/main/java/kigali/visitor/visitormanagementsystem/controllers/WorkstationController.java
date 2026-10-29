package kigali.visitor.visitormanagementsystem.controllers;

import kigali.visitor.visitormanagementsystem.models.Workstation;

import kigali.visitor.visitormanagementsystem.services.WorkstationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/workstations")
public class WorkstationController {

    @Autowired
    private WorkstationService workstationService;

    @PostMapping("/save")
    public ResponseEntity<String> saveWorkstation(@RequestBody Workstation workstation) {
        String message = workstationService.saveWorkstation(workstation);

        HttpStatus httpStatus;

        if (message.equals("Workstation saved successfully")) {
            httpStatus = HttpStatus.CREATED;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateWorkstation(@PathVariable UUID id, @RequestBody Workstation workstation) {
        String message = workstationService.updateWorkstation(id, workstation);

        HttpStatus httpStatus;

        if (message.equals("Workstation updated successfully")) {
            httpStatus = HttpStatus.OK;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteWorkstation(@PathVariable UUID id) {
        String message = workstationService.deleteWorkstation(id);

        HttpStatus httpStatus;

        if (message.equals("Workstation deleted successfully")) {
            httpStatus = HttpStatus.OK;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else {
            httpStatus = HttpStatus.CONFLICT;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @GetMapping("/all")
    public List<Workstation> getAllWorkstations() {
        return workstationService.getAllWorkstations();
    }

    @GetMapping("/free")
    public List<Workstation> getFreeWorkstations() {
        return workstationService.getFreeWorkstations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWorkstationById(@PathVariable UUID id) {
        Optional<Workstation> workstation = workstationService.getWorkstationById(id);

        if (workstation.isPresent()) {
            return new ResponseEntity<>(workstation.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("The workstation with that id does not exist", HttpStatus.NOT_FOUND);
        }
    }
}
