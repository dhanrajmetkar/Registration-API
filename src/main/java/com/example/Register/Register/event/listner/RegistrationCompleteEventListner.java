package com.example.Register.Register.event.listner;

import com.example.Register.Register.entity.User;
import com.example.Register.Register.event.RegistrationCompleteEvent;
import com.example.Register.Register.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
@Slf4j
@Component
public class RegistrationCompleteEventListner implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // crate verification token
         User user=event.getUser();
         String token = UUID.randomUUID().toString();
         userService.saveVerificationTokenForUser(token,user);

         //send mail to user
        String url =event.getApplicationUrl()+"/verifyRegistration?token="+token;
        try {
            sendMail(url);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        log.info("mail send successfully : "+url);


    }

        private void sendMail(String url) throws MessagingException, UnsupportedEncodingException
        {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom("your-email@example.com", "Your Name");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        helper.setTo("dhanrajmetkar3660@gmail.com");

        helper.setSubject("click the below link to verity your account : ");
        helper.setText(url, true);

        mailSender.send(message);
    }
}
