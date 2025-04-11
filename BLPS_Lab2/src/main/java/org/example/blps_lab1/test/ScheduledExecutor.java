package org.example.blps_lab1.test;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Component
// spizheno from https://github.com/snowdrop/narayana-spring-boot/tree/main
public class ScheduledExecutor {

    private final SchedUserServ schedUserServ;
    private final RandomNameService randomNameService;
    private final TransactionTemplate transactionTemplate;

    public ScheduledExecutor(SchedUserServ schedUserServ,
                             RandomNameService randomNameService,
                             PlatformTransactionManager transactionManager) {
        this.schedUserServ = schedUserServ;
        this.randomNameService = randomNameService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * Периодически пытается создать нового пользователя.
     * Программное управление транзакциями: в случае исключения транзакция откатывается.
     */
//    @Scheduled(fixedRate = 5000)
//    public void createNewUser() {
//        String name = randomNameService.getRandomName();
//        System.out.println("Executor ---> Attempting to create a user named " + name);
//        try {
//            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
//                @Override
//                protected void doInTransactionWithoutResult(TransactionStatus status) {
//                    schedUserServ.create(name);
//                }
//            });
//        } catch (Throwable t) {
//            System.out.println("Executor ---> Failed to create a user named " + name + ". Transaction will rollback");
//            throw t;
//        }
//    }

//    @Scheduled(fixedRate = 5000)
//    public void createNewUser() {
//        String name = randomNameService.getRandomName();
//        System.out.println("Executor ---> Attempting to create a user named " + name);
//        try {
//            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
//                @Override
//                protected void doInTransactionWithoutResult(TransactionStatus status) {
//                    schedUserServ.create(name);
//                }
//            });
//        } catch (Throwable t) {
//            System.out.println("Executor ---> Failed to create a user named " + name + ". Transaction will rollback");
//            throw t;
//        }
//    }

    /**
     * Периодически выводит список пользователей, сохранённых в базе данных.
     */
    @Scheduled(fixedRate = 10000)
    public void listUsers() {
        schedUserServ.create("nigga?");
//        List<String> names = schedUserServ.getAll();
//        if (names.isEmpty()) {
//            System.out.println("Executor ---> Users database is still empty");
//        } else {
//            System.out.println("Executor ---> Current users: " + String.join(", ", names));
//        }
    }
}
