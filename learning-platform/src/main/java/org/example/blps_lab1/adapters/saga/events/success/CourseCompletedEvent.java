package org.example.blps_lab1.adapters.saga.events.success;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.blps_lab1.core.domain.auth.UserXml;
import org.example.blps_lab1.core.domain.course.nw.NewCourse;

import java.util.UUID;

@AllArgsConstructor
@Data
public class CourseCompletedEvent {
    private UserXml user;
    private NewCourse course;
}
