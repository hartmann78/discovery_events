package com.practice.events_service.generators;

import com.practice.events_service.dto.newDTO.NewUserRequest;
import com.practice.events_service.model.User;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Component;

@Component
public class UserGenerator {
    public User generateUser() {
        return User.builder()
                .name(generateName())
                .email(generateEmail())
                .build();
    }

    public NewUserRequest generateNewUserRequest() {
        return NewUserRequest.builder()
                .name(generateName())
                .email(generateEmail())
                .build();
    }

    private String generateName() {
        return RandomString.make(8);
    }

    private String generateEmail() {
        return RandomString.make(10) + "@mail.com";
    }
}
