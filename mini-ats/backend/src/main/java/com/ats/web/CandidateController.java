package com.ats.web;

import com.ats.dto.Dtos.*;
import com.ats.model.Stage;
import com.ats.service.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CandidateController {
    private static final Set<String> SORTABLE = Set.of("createdAt", "fullName", "experienceYears", "stage", "position");
    private final CandidateService service;
    private final DashboardService dashboard;

    public CandidateController(CandidateService service, DashboardService dashboard) {
        this.service = service; this.dashboard = dashboard;
    }

    @PostMapping("/candidates")
    public ResponseEntity<CandidateResponse> create(@Valid @RequestBody CandidateRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }

    @GetMapping("/candidates")
    public PageResponse<CandidateResponse> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Stage stage,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String dir) {
        if (!SORTABLE.contains(sortBy)) sortBy = "createdAt";
        Sort sort = "asc".equalsIgnoreCase(dir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable p = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), sort);
        return service.search(q, stage, position, minExperience, p);
    }

    @GetMapping("/candidates/{id}")
    public CandidateResponse get(@PathVariable Long id) { return service.get(id); }

    @PutMapping("/candidates/{id}")
    public CandidateResponse update(@PathVariable Long id, @Valid @RequestBody CandidateRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/candidates/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/candidates/{id}/stage")
    public CandidateResponse changeStage(@PathVariable Long id, @Valid @RequestBody StageChangeRequest r) {
        return service.changeStage(id, r);
    }

    @GetMapping("/candidates/{id}/history")
    public List<HistoryResponse> history(@PathVariable Long id) { return service.history(id); }

    @GetMapping("/dashboard/metrics")
    public MetricsResponse metrics() { return dashboard.metrics(); }
}
