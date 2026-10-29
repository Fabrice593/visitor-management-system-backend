package kigali.visitor.visitormanagementsystem.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "host_employee")
public class HostEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    @ManyToOne
    @JoinColumn(name = "workstation_id")
    private Workstation workstation;

    @ManyToMany
    @JoinTable(
            name = "host_employee_visit_purpose",
            joinColumns = @JoinColumn(name = "host_employee_id"),
            inverseJoinColumns = @JoinColumn(name = "visit_purpose_id")
    )
    private List<VisitPurpose> visitPurposes = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Workstation getWorkstation() {
        return workstation;
    }

    public void setWorkstation(Workstation workstation) {
        this.workstation = workstation;
    }

    public List<VisitPurpose> getVisitPurposes() {
        return visitPurposes;
    }

    public void setVisitPurposes(List<VisitPurpose> visitPurposes) {
        this.visitPurposes = visitPurposes;
    }
}
