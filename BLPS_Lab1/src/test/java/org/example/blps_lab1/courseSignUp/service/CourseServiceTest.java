// package org.example.blps_lab1.courseSignUp.service;

// import jakarta.transaction.Transactional;
// import org.example.blps_lab1.courseSignUp.models.Course;
// import org.example.blps_lab1.courseSignUp.models.Topic;
// import org.example.blps_lab1.courseSignUp.repository.CourseRepository;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import java.math.BigDecimal;

// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// @SpringBootTest
// @Transactional
// class CourseServiceTest {

//     @Autowired
//     private CourseRepository courseRepository;

//     @Autowired
//     private CourseService courseService;

//     private static Course course1;
//     private static Course course2;

//     @BeforeAll
//     public static void initialiseSomeEntities(){
//         course1 = Course.builder()
//                 .courseName("MyCourse")
//                 .coursePrice(BigDecimal.valueOf(99.99))
//                 .topicName(Topic.PROGRAMMING)
//                 .courseDescription("Very useful course")
//                 .courseDuration(30)
//                 .withJobOffer(false)
//                 .build();

//         course2 = Course.builder()
//                 .courseName("YourCourse")
//                 .coursePrice(BigDecimal.valueOf(100))
//                 .topicName(Topic.DESIGN)
//                 .courseDescription("Not useful course")
//                 .courseDuration(50)
//                 .withJobOffer(true)
//                 .build();
//     }

//     @Test
//     void createCourse(){
//         courseService.createCourse(course1);
//         Course savedCourse1 = courseRepository.findByCourseName("MyCourse");
//         assertNotNull(savedCourse1.getCourseId());
//         assertTrue(courseRepository.existsById(savedCourse1.getCourseId()));

//         courseService.createCourse(course2);
//         Course savedCourse2 = courseRepository.findByCourseName("YourCourse");
//         assertNotNull(savedCourse2.getCourseId());
//         assertTrue(courseRepository.existsById(savedCourse2.getCourseId()));
//     }

// }