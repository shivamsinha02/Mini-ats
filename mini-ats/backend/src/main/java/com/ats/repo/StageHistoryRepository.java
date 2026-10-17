package com.ats.repo;

import com.ats.model.StageHistory;
import java.util.List;
import org.springframework.data.jpa.repository.*;

public interface StageHistoryRepository extends JpaRepository<StageHistory, Long> {
    List<StageHistory> findByCandidateIdOrderByChangedAtAsc(Long candidateId);
    void deleteByCandidateId(Long candidateId);

    @Query("select c.createdAt, h.changedAt from StageHistory h join h.candidate c where h.toStage = com.ats.model.Stage.HIRED")
    List<Object[]> hireTimes();
}
