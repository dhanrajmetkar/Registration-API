package com.example.Register.Register.controller;

import com.example.Register.Register.entity.PasswordModel;
import com.example.Register.Register.entity.PasswordResetToken;
import com.example.Register.Register.entity.User;
import com.example.Register.Register.entity.VerificationToken;
import com.example.Register.Register.event.RegistrationCompleteEvent;
import com.example.Register.Register.event.listner.RegistrationCompleteEventListner;
import com.example.Register.Register.model.UserModel;
import com.example.Register.Register.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.SaslServer;
import javax.xml.transform.sax.SAXResult;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class RegistrationController {

    @Autowired
    UserService userService;

    @Autowired
    ApplicationEventPublisher publisher;

    @Autowired
    private JavaMailSender mailSender;

    public RegistrationController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping("/register")
        public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
            User user = userService.registerUser(userModel);

            publisher.publishEvent(new RegistrationCompleteEvent(
                    user,
                    applicationUrl(request)));
            return "success";
        }

    @GetMapping("/verifyRegistration")
        public String verifyRegistration(@RequestParam("token") String token)
        {
            String result =userService.validateVerificationToken(token);
            if(result.equalsIgnoreCase("valid")){
                return "User Verified Successfully";
            }
            else {
                return "badUser";
            }
        }
    @GetMapping("/resendVerificationToken")
        public String  resendVerificationToken(@RequestParam("token") String token,HttpServletRequest request)
        {
            VerificationToken verificationToken= userService.generateNewVerificationToken(token);
            User user=verificationToken.getUser();
            resendVerificationTokenMail(user,applicationUrl(request),verificationToken.getToken());
            return  "Resend link Send :";
        }
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel,HttpServletRequest request)
    {
        User user =userService.findUserByEmail(passwordModel.getEmail());
        if(user!=null)
        {
            String token = UUID.randomUUID().toString();
            PasswordResetToken passwordResetToken=userService.createPasswordResetTokenforUser(user,token);
            sendResetPasswordMail(user,applicationUrl(request),passwordResetToken.getToken());
            return  "Reset link Send On Your Email  ::";
        }
        return "USER not found :";
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token ,@RequestBody PasswordModel passwordModel)
    {
        String result =userService.validatePasswordResetToken(token);
        if(!result.equalsIgnoreCase("valid")){
            return "Invalid Token";
        }

        Optional<User> user=userService.getUserByPasswordResetToken(token);
        if(user.isPresent())
        {
           userService.changePassword(user.get(),passwordModel.getNewPassword());
           return "passwordChange";
        }
        else{
            return "invalid";
        }

    }
    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel)
    {
        User user=userService.findUserByEmail(passwordModel.getEmail());
        if(!userService.checkOldPasswordValid(user,passwordModel.getOldPassword()))
        {
            return "Invalid old Password ";
        }

        userService.changePassword(user,passwordModel.getNewPassword());

        return "Password changed successfully";
    }


        private void sendResetPasswordMail(User user, String url1, String token) {
            String url =url1+"/savePassword?token="+token;
            try {
                String msg="Click the below link to RESET your PASSWORD :";
                sendMail(url,msg);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            log.info("Reset mail send successfully : {}", url);
        }

        private void resendVerificationTokenMail(User user, String url1, String token) {
                String url =url1+"/verifyRegistration?token="+token;
                try {
                    String msg="click the below link to verity your account : ";
                  sendMail(url,msg);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                log.info("mail send successfully : "+url);
            }

        private void sendMail(String url,String sub) throws MessagingException, UnsupportedEncodingException {
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

            helper.setSubject(sub);
            helper.setText(url, true);

            mailSender.send(message);
        }


    private String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath();
    }





}
