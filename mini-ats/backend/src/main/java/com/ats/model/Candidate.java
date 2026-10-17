package com.ats.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "candidates", indexes = {
        @Index(name = "idx_candidate_stage", columnList = "stage"),
        @Index(name = "idx_candidate_position", columnList = "position"),
        @Index(name = "idx_candidate_created", columnList = "createdAt")
}, uniqueConstraints = @UniqueConstraint(name = "uk_candidate_email", columnNames = "email"))
public class Candidate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String fullName;
    @Column(nullable = false) private String email;
    private String phone;
    @Column(nullable = false) private String position;
    private String skills;               // comma separated
    private Integer experienceYears;
    @Column(length = 2000) private String notes;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private Stage stage = Stage.APPLIED;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    @Column(nullable = false) private Instant updatedAt;

    @PrePersist void onCreate() { createdAt = updatedAt = Instant.now(); }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { fullName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { phone = v; }
    public String getPosition() { return position; }
    public void setPosition(String v) { position = v; }
    public String getSkills() { return skills; }
    public void setSkills(String v) { skills = v; }
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer v) { experienceYears = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { notes = v; }
    public Stage getStage() { return stage; }
    public void setStage(Stage v) { stage = v; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
