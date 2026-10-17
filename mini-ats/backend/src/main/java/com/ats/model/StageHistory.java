package com.ats.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "stage_history", indexes = @Index(name = "idx_history_candidate", columnList = "candidate_id"))
public class StageHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "candidate_id")
    private Candidate candidate;
    @Enumerated(EnumType.STRING) @Column(length = 20) private Stage fromStage;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Stage toStage;
    private String comment;
    @Column(nullable = false) private Instant changedAt = Instant.now();

    protected StageHistory() {}
    public StageHistory(Candidate c, Stage from, Stage to, String comment) {
        this.candidate = c; this.fromStage = from; this.toStage = to; this.comment = comment;
    }
    public Long getId() { return id; }
    public Stage getFromStage() { return fromStage; }
    public Stage getToStage() { return toStage; }
    public String getComment() { return comment; }
    public Instant getChangedAt() { return changedAt; }
}
