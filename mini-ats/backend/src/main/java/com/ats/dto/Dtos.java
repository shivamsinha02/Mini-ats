package com.ats.dto;

import com.ats.model.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class Dtos {
    private Dtos() {}

    public record CandidateRequest(
            @NotBlank @Size(max = 120) String fullName,
            @NotBlank @Email String email,
            @Size(max = 30) String phone,
            @NotBlank @Size(max = 120) String position,
            @Size(max = 500) String skills,
            @Min(0) @Max(60) Integer experienceYears,
            @Size(max = 2000) String notes) {}

    public record StageChangeRequest(@NotNull Stage stage, @Size(max = 255) String comment) {}

    public record CandidateResponse(Long id, String fullName, String email, String phone, String position,
                                    String skills, Integer experienceYears, String notes, Stage stage,
                                    List<Stage> allowedNext, Instant createdAt, Instant updatedAt) {
        public static CandidateResponse from(Candidate c) {
            return new CandidateResponse(c.getId(), c.getFullName(), c.getEmail(), c.getPhone(), c.getPosition(),
                    c.getSkills(), c.getExperienceYears(), c.getNotes(), c.getStage(),
                    List.copyOf(c.getStage().next()), c.getCreatedAt(), c.getUpdatedAt());
        }
    }

    public record HistoryResponse(Stage fromStage, Stage toStage, String comment, Instant changedAt) {
        public static HistoryResponse from(StageHistory h) {
            return new HistoryResponse(h.getFromStage(), h.getToStage(), h.getComment(), h.getChangedAt());
        }
    }

    public record PageResponse<T>(List<T> items, int page, int size, long totalItems, int totalPages) {}

    public record PositionCount(String position, long count) {}

    public record MetricsResponse(long totalCandidates, Map<Stage, Long> byStage, long newLast7Days,
                                  long hired, double hireRatePercent, Double avgDaysToHire,
                                  List<PositionCount> topPositions) {}
}
