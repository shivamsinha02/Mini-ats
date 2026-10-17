package com.ats.service;

import com.ats.dto.Dtos.*;
import com.ats.model.*;
import com.ats.repo.*;
import com.ats.service.Exceptions.*;
import jakarta.persistence.criteria.Predicate;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CandidateService {
    private final CandidateRepository repo;
    private final StageHistoryRepository history;

    public CandidateService(CandidateRepository repo, StageHistoryRepository history) {
        this.repo = repo; this.history = history;
    }

    @Transactional
    public CandidateResponse create(CandidateRequest r) {
        if (repo.existsByEmailIgnoreCase(r.email()))
            throw new ConflictException("A candidate with this email already exists");
        Candidate c = new Candidate();
        apply(c, r);
        c.setStage(Stage.APPLIED);
        repo.save(c);
        history.save(new StageHistory(c, null, Stage.APPLIED, "Application received"));
        return CandidateResponse.from(c);
    }

    @Transactional(readOnly = true)
    public CandidateResponse get(Long id) { return CandidateResponse.from(find(id)); }

    @Transactional
    public CandidateResponse update(Long id, CandidateRequest r) {
        Candidate c = find(id);
        if (!c.getEmail().equalsIgnoreCase(r.email()) && repo.existsByEmailIgnoreCase(r.email()))
            throw new ConflictException("A candidate with this email already exists");
        apply(c, r);
        return CandidateResponse.from(repo.save(c));
    }

    @Transactional
    public void delete(Long id) {
        Candidate c = find(id);
        history.deleteByCandidateId(id);
        repo.delete(c);
    }

    @Transactional
    public CandidateResponse changeStage(Long id, StageChangeRequest req) {
        Candidate c = find(id);
        Stage from = c.getStage();
        if (!from.canMoveTo(req.stage()))
            throw new InvalidTransitionException("Cannot move from " + from + " to " + req.stage()
                    + ". Allowed: " + from.next());
        c.setStage(req.stage());
        repo.save(c);
        history.save(new StageHistory(c, from, req.stage(), req.comment()));
        return CandidateResponse.from(c);
    }

    @Transactional(readOnly = true)
    public List<HistoryResponse> history(Long id) {
        find(id);
        return history.findByCandidateIdOrderByChangedAtAsc(id).stream().map(HistoryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<CandidateResponse> search(String q, Stage stage, String position,
                                                  Integer minExp, Pageable pageable) {
        Specification<Candidate> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";
                ps.add(cb.or(
                        cb.like(cb.lower(root.get("fullName")), like),
                        cb.like(cb.lower(root.get("email")), like),
                        cb.like(cb.lower(root.get("position")), like),
                        cb.like(cb.lower(cb.coalesce(root.<String>get("skills"), "")), like)));
            }
            if (stage != null) ps.add(cb.equal(root.get("stage"), stage));
            if (position != null && !position.isBlank())
                ps.add(cb.equal(cb.lower(root.get("position")), position.trim().toLowerCase()));
            if (minExp != null) ps.add(cb.greaterThanOrEqualTo(root.get("experienceYears"), minExp));
            return cb.and(ps.toArray(new Predicate[0]));
        };
        Page<Candidate> p = repo.findAll(spec, pageable);
        return new PageResponse<>(p.getContent().stream().map(CandidateResponse::from).toList(),
                p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    private Candidate find(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Candidate " + id + " not found"));
    }

    private void apply(Candidate c, CandidateRequest r) {
        c.setFullName(r.fullName().trim());
        c.setEmail(r.email().trim().toLowerCase());
        c.setPhone(r.phone());
        c.setPosition(r.position().trim());
        c.setSkills(r.skills());
        c.setExperienceYears(r.experienceYears());
        c.setNotes(r.notes());
    }
}
