package com.ats.model;

import java.util.EnumSet;
import java.util.Set;

/** Hiring pipeline. Transitions are validated so candidates can't skip stages. */
public enum Stage {
    APPLIED, SCREENING, INTERVIEW, OFFER, HIRED, REJECTED;

    public Set<Stage> next() {
        return switch (this) {
            case APPLIED -> EnumSet.of(SCREENING, REJECTED);
            case SCREENING -> EnumSet.of(INTERVIEW, REJECTED);
            case INTERVIEW -> EnumSet.of(OFFER, REJECTED);
            case OFFER -> EnumSet.of(HIRED, REJECTED);
            case HIRED, REJECTED -> EnumSet.noneOf(Stage.class);
        };
    }

    public boolean canMoveTo(Stage target) {
        return next().contains(target);
    }
}
