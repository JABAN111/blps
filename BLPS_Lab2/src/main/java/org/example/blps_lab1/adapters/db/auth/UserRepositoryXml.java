package org.example.blps_lab1.adapters.db.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.core.ports.db.UserDatabase;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("dev")
@Slf4j
public class UserRepositoryXml implements UserDatabase {

    //TODO сделать через PostConstruct
    private static final String XML_FILE = "users.xml";
    private final XmlMapper xmlMapper = new XmlMapper();
    private final File file;

    private static final AtomicLong idCounter = new AtomicLong(1);

    public UserRepositoryXml() {
        try {
            file = new File(XML_FILE);
            boolean init = false;
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }
                init = true;
            } else if (file.length() == 0) {
                init = true;
            }
            if (init) {
                xmlMapper.writeValue(file, new UsersWrapper());
            } else {
                UsersWrapper wrapper = readWrapper();
                long maxId = wrapper.users.stream()
                        .mapToLong(u -> u.getId() != null ? u.getId() : 0L)
                        .max()
                        .orElse(0L);
                idCounter.set(maxId + 1);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize XML storage file: " + XML_FILE, e);
        }
    }

    @Override
    public synchronized User save(User user) {
        try {
            UsersWrapper wrapper = readWrapper();
            if (user.getId() == null) {
                user.setId(idCounter.getAndIncrement());
            }
            wrapper.users.removeIf(u -> u.getEmail().equals(user.getEmail()) || u.getId().equals(user.getId()));
            wrapper.users.add(user);
            xmlMapper.writeValue(file, wrapper);
            return user;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save user to XML", e);
        }
    }

    @Override
    public synchronized Optional<User> findByEmail(String email) {
        try {
            UsersWrapper wrapper = readWrapper();
            var foundUser = wrapper.users.stream()
                    .filter(u -> u.getEmail().equals(email))
                    .findFirst();
            log.debug("found user with email: {}, user data = {}", email, foundUser);
            return foundUser;
        } catch (IOException e) {
            log.error("fail to find user with email {}", email);
            throw new RuntimeException("Failed to read users from XML", e);
        }
    }

    @Override
    public synchronized Optional<User> findById(Long userId) {
        try {
            UsersWrapper wrapper = readWrapper();
            return wrapper.users.stream()
                    .filter(u -> u.getId().equals(userId))
                    .findFirst();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read users from XML", e);
        }
    }


    private UsersWrapper readWrapper() throws IOException {
        try {
            return xmlMapper.readValue(file, UsersWrapper.class);
        } catch (JsonProcessingException e) {
            UsersWrapper empty = new UsersWrapper();
            xmlMapper.writeValue(file, empty);
            return empty;
        }
    }

    @Setter
    @Getter
    @JacksonXmlRootElement(localName = "users")
    public static class UsersWrapper {
        @JacksonXmlElementWrapper(localName = "users")
        @JacksonXmlProperty(localName = "user")
        private List<User> users = new ArrayList<>();

        public UsersWrapper() { }

    }

}
