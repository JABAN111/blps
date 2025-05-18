package org.example.blps_lab1.core.ports.course;

import java.util.UUID;

public interface CourseProgressService {
    void addPoints(Long userId, Long courseUUID, int points);

}
