package org.example.blps_lab1.courseSignUp.repository;

import org.example.blps_lab1.courseSignUp.models.Course;
import org.example.blps_lab1.courseSignUp.models.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByCourseAndOrderNumber(Course course, Integer orderNumber);
    List<Module> findByCourseOrderByOrderNumberAsc(Course course);
}
