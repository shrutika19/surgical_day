package com.hospital.surgicalday.scheduling;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeIntervalTest {

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 9, 8, 8, 0);

    @Test
    void detectsOverlappingIntervals() {
        TimeInterval first = new TimeInterval(BASE, BASE.plusMinutes(60));
        TimeInterval second = new TimeInterval(BASE.plusMinutes(30), BASE.plusMinutes(90));

        assertTrue(first.overlaps(second));
    }

    @Test
    void allowsAdjacentIntervals() {
        TimeInterval first = new TimeInterval(BASE, BASE.plusMinutes(60));
        TimeInterval second = new TimeInterval(BASE.plusMinutes(60), BASE.plusMinutes(120));

        assertFalse(first.overlaps(second));
    }
}