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

        if (message.equals("Workstation saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } else if (message.endsWith("does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateWorkstation(@PathVariable UUID id, @RequestBody Workstation workstation) {
        String message = workstationService.updateWorkstation(id, workstation);

        if (message.equals("Workstation updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.endsWith("does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteWorkstation(@PathVariable UUID id) {
        String message = workstationService.deleteWorkstation(id);

        if (message.equals("Workstation deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.endsWith("does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
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
