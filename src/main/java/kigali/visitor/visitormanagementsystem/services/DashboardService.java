package kigali.visitor.visitormanagementsystem.services;

import kigali.visitor.visitormanagementsystem.models.VisitStatus;
import kigali.visitor.visitormanagementsystem.repositories.HostEmployeeRepository;
import kigali.visitor.visitormanagementsystem.repositories.VisitNoteRepository;
import kigali.visitor.visitormanagementsystem.repositories.VisitRepository;
import kigali.visitor.visitormanagementsystem.repositories.VisitorRepository;
import kigali.visitor.visitormanagementsystem.repositories.WorkstationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private HostEmployeeRepository hostEmployeeRepository;

    @Autowired
    private WorkstationRepository workstationRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private VisitNoteRepository visitNoteRepository;

    @Transactional(readOnly = true)
    public Map<String, Long> getSummary() {
        Map<String, Long> summary = new LinkedHashMap<>();
        summary.put("visitors", visitorRepository.count());
        summary.put("hostEmployees", hostEmployeeRepository.count());
        summary.put("freeWorkstations", (long) workstationRepository.findFreeWorkstations().size());
        summary.put("visitsToday", visitRepository.countByVisitDate(LocalDate.now()));
        summary.put("expected", visitRepository.countByStatus(VisitStatus.EXPECTED));
        summary.put("visitNotes", visitNoteRepository.count());

        return summary;
    }
}
