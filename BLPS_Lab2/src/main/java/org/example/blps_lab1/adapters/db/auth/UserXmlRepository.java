package org.example.blps_lab1.adapters.db.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.blps_lab1.core.domain.auth.UserXml;
import org.example.blps_lab1.core.ports.db.UserDatabase;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
@Primary
public class UserXmlRepository implements UserDatabase {

    private final Path xmlPath = Path.of("users.xml");
    private final XmlMapper xmlMapper = new XmlMapper();

    private static final AtomicLong idCounter = new AtomicLong(1);

    private final static TypeReference<UsersXmlWrapper> TYPE_REFERENCE = new TypeReference<>() {
    };

    @PostConstruct
    public void init() throws IOException {
//        Files.deleteIfExists(xmlPath);
//        Files.createFile(xmlPath);
    }


    @Override
    public UserXml save(UserXml userXml) {
        userXml.setId(idCounter.getAndIncrement());
        final UsersXmlWrapper userXmlWrapper = getUserXmlWrapper();
        userXmlWrapper.getUsers().add(userXml);
        try {
            xmlMapper.writeValue(xmlPath.toFile(), userXmlWrapper);
        } catch (IOException e) {
            throw new RuntimeException("Не получилось сохранить пользователя: ", e);
        }
        return userXml;
    }

    @Override
    public synchronized Optional<UserXml> findByEmail(String email) {
        final List<UserXml> users = getUserXmlWrapper().getUsers();
        return users.stream()
                .filter(user -> email.equals(user.getUsername()))
                .findFirst();
    }

    @Override
    public Optional<UserXml> findById(Long userId) {
        final List<UserXml> users = getUserXmlWrapper().getUsers();
        return users.stream()
                .filter(user -> userId.equals(user.getId()))
                .findFirst();
    }

    private UsersXmlWrapper getUserXmlWrapper() {
        UsersXmlWrapper usersXmlWrapper;
        try {
            usersXmlWrapper = xmlMapper.readValue(xmlPath.toFile(), TYPE_REFERENCE);
        } catch (IOException e) {
            usersXmlWrapper = new UsersXmlWrapper();
        }
        return usersXmlWrapper;
    }


}
