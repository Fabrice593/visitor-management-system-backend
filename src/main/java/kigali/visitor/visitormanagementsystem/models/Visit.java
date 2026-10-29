package kigali.visitor.visitormanagementsystem.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "visit")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate visitDate;

    @Enumerated(EnumType.STRING)
    private VisitStatus status;

    private String purposeNote;

    @ManyToOne
    @JoinColumn(name = "visitor_id")
    private Visitor visitor;

    @ManyToOne
    @JoinColumn(name = "host_employee_id")
    private HostEmployee hostEmployee;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public VisitStatus getStatus() {
        return status;
    }

    public void setStatus(VisitStatus status) {
        this.status = status;
    }

    public String getPurposeNote() {
        return purposeNote;
    }

    public void setPurposeNote(String purposeNote) {
        this.purposeNote = purposeNote;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public HostEmployee getHostEmployee() {
        return hostEmployee;
    }

    public void setHostEmployee(HostEmployee hostEmployee) {
        this.hostEmployee = hostEmployee;
    }
}
