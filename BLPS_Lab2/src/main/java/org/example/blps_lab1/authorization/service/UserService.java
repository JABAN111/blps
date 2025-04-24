package org.example.blps_lab1.authorization.service;

import java.util.List;
import java.util.UUID;

import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.courseSignUp.models.Course;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    User add(User user);
    List<User> addAll(List<User> users);

    User getUserByEmail(String username);

    User updateUser(User user);

    boolean isExist(String username);


    void enrollUser(User user, Course course);
    void enrollUser(User user, UUID courseUUID);



    UserDetailsService getUserDetailsService();
}
