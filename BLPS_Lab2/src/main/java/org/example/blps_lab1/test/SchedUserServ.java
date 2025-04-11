package org.example.blps_lab1.test;

import org.example.blps_lab1.authorization.models.Role;
import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.authorization.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

@Component
public class SchedUserServ {

    private final JdbcTemplate jdbcTemplate;
    private final UserService userService;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public SchedUserServ(JdbcTemplate jdbcTemplate,
                         UserService userService,
                         PlatformTransactionManager transactionManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.userService = userService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    // Программное управление транзакциями
    public void create(String name) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                System.out.printf("jms killed, creating user: %s%n", name);
                var user = new User();
                user.setPassword("somequitegoodpwd");
                user.setEmail("jaba@jaba.jaba");
                user.setFirstName(UUID.randomUUID().toString());
                user.setLastName(UUID.randomUUID().toString());
                user.setPhoneNumber("89991875292");
                user.setRole(Role.ROLE_ADMIN);
                 userService.add(user);

            }
        });
    }

    public List<String> getAll() {
        return this.jdbcTemplate.queryForList("SELECT NAME FROM UNI", String.class);
    }
}
