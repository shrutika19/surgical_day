package com.hospital.surgicalday.scheduling;

import java.time.LocalDateTime;
import java.util.Objects;

public record TimeInterval(LocalDateTime start, LocalDateTime end) {

    public TimeInterval {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Interval end must be after interval start");
        }
    }

    public boolean overlaps(TimeInterval other) {
        Objects.requireNonNull(other, "other");
        return start.isBefore(other.end) && end.isAfter(other.start);
    }
}
