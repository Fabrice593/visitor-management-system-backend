package kigali.visitor.visitormanagementsystem.controllers;

import kigali.visitor.visitormanagementsystem.models.HostEmployee;

import kigali.visitor.visitormanagementsystem.services.HostEmployeeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/host-employees")
public class HostEmployeeController {

    @Autowired
    private HostEmployeeService hostEmployeeService;

    @PostMapping("/save")
    public ResponseEntity<String> saveHostEmployee(@RequestBody HostEmployee hostEmployee) {
        String message = hostEmployeeService.saveHostEmployee(hostEmployee);

        HttpStatus httpStatus;

        if (message.equals("Host employee saved successfully")) {
            httpStatus = HttpStatus.CREATED;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (message.equals("That workstation is taken by another host employee")) {
            httpStatus = HttpStatus.CONFLICT;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateHostEmployee(@PathVariable UUID id, @RequestBody HostEmployee hostEmployee) {
        String message = hostEmployeeService.updateHostEmployee(id, hostEmployee);

        HttpStatus httpStatus;

        if (message.equals("Host employee updated successfully")) {
            httpStatus = HttpStatus.OK;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (message.equals("That workstation is taken by another host employee")) {
            httpStatus = HttpStatus.CONFLICT;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @PutMapping("/{id}/visit-purposes/{purposeId}")
    public ResponseEntity<String> addVisitPurpose(@PathVariable UUID id, @PathVariable UUID purposeId) {
        String message = hostEmployeeService.addVisitPurpose(id, purposeId);

        HttpStatus httpStatus;

        if (message.equals("Visit purpose added to the host employee")) {
            httpStatus = HttpStatus.OK;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else {
            httpStatus = HttpStatus.CONFLICT;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @PatchMapping("/{id}/workstation")
    public ResponseEntity<String> changeWorkstation(@PathVariable UUID id, @RequestParam int stationNumber) {
        String message = hostEmployeeService.changeWorkstation(id, stationNumber);

        HttpStatus httpStatus;

        if (message.equals("Workstation changed successfully")) {
            httpStatus = HttpStatus.OK;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else {
            httpStatus = HttpStatus.CONFLICT;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteHostEmployee(@PathVariable UUID id) {
        String message = hostEmployeeService.deleteHostEmployee(id);

        HttpStatus httpStatus;

        if (message.equals("Host employee deleted successfully")) {
            httpStatus = HttpStatus.OK;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else {
            httpStatus = HttpStatus.CONFLICT;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @GetMapping("/all")
    public List<HostEmployee> getAllHostEmployees() {
        return hostEmployeeService.getAllHostEmployees();
    }

    @GetMapping("/by-visit-purpose")
    public List<HostEmployee> getHostEmployeesByVisitPurpose(@RequestParam String name) {
        return hostEmployeeService.getHostEmployeesByVisitPurpose(name);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHostEmployeeById(@PathVariable UUID id) {
        Optional<HostEmployee> hostEmployee = hostEmployeeService.getHostEmployeeById(id);

        if (hostEmployee.isPresent()) {
            return new ResponseEntity<>(hostEmployee.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("The host employee with that id does not exist", HttpStatus.NOT_FOUND);
        }
    }
}
