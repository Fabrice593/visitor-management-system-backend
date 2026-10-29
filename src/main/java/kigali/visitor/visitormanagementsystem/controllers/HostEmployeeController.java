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

        if (message.equals("Host employee saved successfully")) {
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } else if (message.endsWith("does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateHostEmployee(@PathVariable UUID id, @RequestBody HostEmployee hostEmployee) {
        String message = hostEmployeeService.updateHostEmployee(id, hostEmployee);

        if (message.equals("Host employee updated successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.endsWith("does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteHostEmployee(@PathVariable UUID id) {
        String message = hostEmployeeService.deleteHostEmployee(id);

        if (message.equals("Host employee deleted successfully")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else if (message.endsWith("does not exist")) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/all")
    public List<HostEmployee> getAllHostEmployees() {
        return hostEmployeeService.getAllHostEmployees();
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
