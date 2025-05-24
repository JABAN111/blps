package org.example.blps_lab1.core.ports.auth;

import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.core.domain.auth.UserXml;
import org.example.blps_lab1.core.domain.course.nw.NewCourse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserXml add(UserXml user);

    UserXml getUserByEmail(String username);

    UserXml updateUser(UserXml user);

    boolean isExist(String username);

    void enrollUser(UserXml user, NewCourse course);

    UserDetailsService getUserDetailsService();
}
