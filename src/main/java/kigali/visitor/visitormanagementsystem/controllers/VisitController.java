package kigali.visitor.visitormanagementsystem.controllers;

import kigali.visitor.visitormanagementsystem.models.Visit;
import kigali.visitor.visitormanagementsystem.models.VisitStatus;

import kigali.visitor.visitormanagementsystem.services.VisitService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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

        HttpStatus httpStatus;

        if (message.equals("Visit saved successfully")) {
            httpStatus = HttpStatus.CREATED;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (message.equals("Host employee already receives a visitor on that date")) {
            httpStatus = HttpStatus.CONFLICT;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateVisit(@PathVariable UUID id, @RequestBody Visit visit) {
        String message = visitService.updateVisit(id, visit);

        HttpStatus httpStatus;

        if (message.equals("Visit updated successfully")) {
            httpStatus = HttpStatus.OK;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (message.equals("Host employee already receives a visitor on that date")) {
            httpStatus = HttpStatus.CONFLICT;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> changeStatus(@PathVariable UUID id, @RequestParam VisitStatus status) {
        String message = visitService.changeStatus(id, status);

        HttpStatus httpStatus;

        if (message.equals("Visit status updated successfully")) {
            httpStatus = HttpStatus.OK;
        } else if (message.equals("The visit with that id does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteVisit(@PathVariable UUID id) {
        String message = visitService.deleteVisit(id);

        HttpStatus httpStatus;

        if (message.equals("Visit deleted successfully")) {
            httpStatus = HttpStatus.OK;
        } else if (message.endsWith("does not exist")) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (message.equals("That visit has visit notes, delete them first")) {
            httpStatus = HttpStatus.CONFLICT;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(message, httpStatus);
    }

    @GetMapping("/all")
    public List<Visit> getAllVisits() {
        return visitService.getAllVisits();
    }

    @GetMapping("/page")
    public ResponseEntity<?> getVisitsPage(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "5") int size,
                                          @RequestParam(defaultValue = "visitDate,desc") String sort) {
        if (page < 0 || size < 1) {
            return new ResponseEntity<>("Page must be 0 or more and size at least 1", HttpStatus.BAD_REQUEST);
        }

        String[] sortParts = sort.split(",", -1);

        if (sortParts.length != 2) {
            return new ResponseEntity<>("Sort must look like visitDate,desc", HttpStatus.BAD_REQUEST);
        }

        String property = sortParts[0].trim();
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortParts[1].trim()).orElse(null);

        if (!List.of("id", "visitDate", "status", "purposeNote").contains(property) || direction == null) {
            return new ResponseEntity<>("Sort must use id, visitDate, status or purposeNote with asc or desc",
                    HttpStatus.BAD_REQUEST);
        }

        Sort sortOrder = Sort.by(direction, property);

        if (!property.equals("id")) {
            sortOrder = sortOrder.and(Sort.by("id"));
        }

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        return new ResponseEntity<>(visitService.getVisitsPage(pageable), HttpStatus.OK);
    }

    @GetMapping("/stats/by-status")
    public List<Object[]> getVisitCountsByStatus() {
        return visitService.getVisitCountsByStatus();
    }

    @PatchMapping("/cancel-day")
    public ResponseEntity<String> cancelHostEmployeeDay(
            @RequestParam UUID hostEmployeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String message = visitService.cancelHostEmployeeDay(hostEmployeeId, date);

        return new ResponseEntity<>(message, HttpStatus.OK);
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
