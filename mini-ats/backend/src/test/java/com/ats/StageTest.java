package com.ats;

import static org.junit.jupiter.api.Assertions.*;

import com.ats.model.Stage;
import org.junit.jupiter.api.Test;

class StageTest {
    @Test void happyPath() {
        assertTrue(Stage.APPLIED.canMoveTo(Stage.SCREENING));
        assertTrue(Stage.OFFER.canMoveTo(Stage.HIRED));
    }
    @Test void cannotSkipStages() { assertFalse(Stage.APPLIED.canMoveTo(Stage.OFFER)); }
    @Test void terminalStages() {
        assertTrue(Stage.HIRED.next().isEmpty());
        assertTrue(Stage.REJECTED.next().isEmpty());
    }
    @Test void anyActiveStageCanBeRejected() {
        for (Stage s : new Stage[]{Stage.APPLIED, Stage.SCREENING, Stage.INTERVIEW, Stage.OFFER})
            assertTrue(s.canMoveTo(Stage.REJECTED));
    }
}
