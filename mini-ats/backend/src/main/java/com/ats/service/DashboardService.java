package com.ats.service;

import com.ats.dto.Dtos.*;
import com.ats.model.Stage;
import com.ats.repo.*;
import java.time.*;
import java.util.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {
    private final CandidateRepository repo;
    private final StageHistoryRepository history;

    public DashboardService(CandidateRepository repo, StageHistoryRepository history) {
        this.repo = repo; this.history = history;
    }

    // Candidate for caching (Caffeine/Redis, short TTL) when traffic grows.
    @Transactional(readOnly = true)
    public MetricsResponse metrics() {
        Map<Stage, Long> byStage = new EnumMap<>(Stage.class);
        for (Stage s : Stage.values()) byStage.put(s, 0L);
        long total = 0;
        for (Object[] row : repo.countByStage()) {
            long n = (Long) row[1];
            byStage.put((Stage) row[0], n);
            total += n;
        }
        long hired = byStage.get(Stage.HIRED);
        double rate = total == 0 ? 0 : Math.round(hired * 1000.0 / total) / 10.0;

        OptionalDouble avg = history.hireTimes().stream()
                .mapToDouble(r -> Duration.between((Instant) r[0], (Instant) r[1]).toSeconds() / 86400.0)
                .average();
        Double avgDays = avg.isPresent() ? Math.round(avg.getAsDouble() * 10) / 10.0 : null;

        List<PositionCount> top = repo.topPositions(PageRequest.of(0, 5)).stream()
                .map(r -> new PositionCount((String) r[0], (Long) r[1])).toList();

        return new MetricsResponse(total, byStage, repo.countByCreatedAtAfter(Instant.now().minus(Duration.ofDays(7))),
                hired, rate, avgDays, top);
    }
}
