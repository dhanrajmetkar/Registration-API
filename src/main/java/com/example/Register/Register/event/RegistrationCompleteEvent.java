package com.example.Register.Register.event;

import com.example.Register.Register.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;
@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private User user;
    private String applicationUrl;

    public RegistrationCompleteEvent(User user,String applicationUrl) {
        super(user);
        this.user=user;
        this.applicationUrl=applicationUrl;
    }
}
