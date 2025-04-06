package app.scheduler;

import app.user.model.User;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
public class UserRenewalScheduler {
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public UserRenewalScheduler(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedDelay = 20000)
    public void renewUserStatus() {
        List<User> users = userService.getAllUsersReadyForRenewal();
        if (users.isEmpty()) {
            log.info("No User found for renewal");
            return;
        }
        for (User user : users) {
            if(user.getRole().name().equals("ADMIN")){
                return;
            }
            LocalDateTime updatedOn = user.getUpdatedOn();
            long days = ChronoUnit.DAYS.between(updatedOn, LocalDateTime.now());
            if(days >= 1L){
                user.setActive(false);
                userRepository.save(user);
            }
        }
    }
}


