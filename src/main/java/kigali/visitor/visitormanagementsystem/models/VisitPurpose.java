package kigali.visitor.visitormanagementsystem.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "visit_purpose")
public class VisitPurpose {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToMany(mappedBy = "visitPurposes")
    @JsonIgnore
    private List<HostEmployee> hostEmployees = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HostEmployee> getHostEmployees() {
        return hostEmployees;
    }

    public void setHostEmployees(List<HostEmployee> hostEmployees) {
        this.hostEmployees = hostEmployees;
    }
}
