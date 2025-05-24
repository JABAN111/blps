package org.example.blps_lab1.adapters.course.dto.nw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blps_lab1.core.domain.course.Topic;
import org.example.blps_lab1.core.domain.course.nw.NewModule;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewCourseDto {
    private UUID uuid;

    private String name;

    private String description;

    private BigDecimal price;

    private Topic topic;

    private LocalDateTime creationTime;

    private List<NewModuleDto> newModuleList = new ArrayList<>();

    private List<NewCourseDto> additionalCourseList = new ArrayList<>();
}
