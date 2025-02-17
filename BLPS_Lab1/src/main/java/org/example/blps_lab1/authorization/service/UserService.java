package org.example.blps_lab1.authorization.service;

import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.authorization.dto.LoginRequest;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.repository.UserRepository;
import org.example.blps_lab1.courseSignUp.models.Course;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;


    public void createUser(final User user){
        try{
            User newUser = getIfUserExists(user.getId());
            log.info("Created user: {}", newUser);
        }catch (ObjectNotFoundException e){
            User newUser = new User();
            userRepository.save(newUser);

        }
    }

    public User getIfUserExists(Long userId){
        return userRepository.findById(userId).orElseThrow(() ->{
            log.info("User with id {} doesn't exists", userId);
            return new RuntimeException("Пользователь не найден");
        });
    }

    public User validateUser(LoginRequest loginInfo){
        if(loginInfo.getEmail().contains("@")){
            Optional<User> optionalUser = userRepository.findByEmail(loginInfo.getEmail());
            User user = optionalUser.orElseThrow(() -> new BadCredentialsException("Invalid login or password"));
            if(!passwordEncoder.matches(loginInfo.getPassword(), user.getPassword())){
                throw new BadCredentialsException("Invalid login or password");
            }
            return user;
        }else{
           throw new RuntimeException("Email must have @");
        }
    }
}
