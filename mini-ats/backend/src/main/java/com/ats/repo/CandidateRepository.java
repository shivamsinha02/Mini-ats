package com.ats.repo;

import com.ats.model.Candidate;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

public interface CandidateRepository extends JpaRepository<Candidate, Long>, JpaSpecificationExecutor<Candidate> {
    boolean existsByEmailIgnoreCase(String email);
    long countByCreatedAtAfter(Instant since);

    @Query("select c.stage, count(c) from Candidate c group by c.stage")
    List<Object[]> countByStage();

    @Query("select c.position, count(c) from Candidate c group by c.position order by count(c) desc")
    List<Object[]> topPositions(Pageable pageable);
}
