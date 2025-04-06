package app;

import app.user.model.User;
import app.user.model.UserRole;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class TestBuilder {
    public static User aRandomUser(){
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("A")
                .password("1234")
                .email("a@b")
                .role(UserRole.GUEST)
                .active(false)
                .createdAt(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        return user;
    }
}
