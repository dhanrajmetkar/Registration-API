package com.example.Register.Register.services;

import com.example.Register.Register.entity.PasswordResetToken;
import com.example.Register.Register.entity.User;
import com.example.Register.Register.entity.VerificationToken;
import com.example.Register.Register.model.UserModel;
import com.example.Register.Register.repository.PasswordResetTokenRepository;
import com.example.Register.Register.repository.UserRepository;
import com.example.Register.Register.repository.VerificationTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;
@Slf4j
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;
    
    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;


    @Override
    public User registerUser(UserModel userModel) {
        User user=User
                .builder()
                .email(userModel.getEmail())
                .firstname(userModel.getFirstname())
                .lastname(userModel.getLastname())
                .password(passwordEncoder.encode(userModel.getPassword()))
                .role("USER")
                .build();
        userRepository.save(user);
        return user;

    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        VerificationToken verificationToken=new VerificationToken(token,user);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken=verificationTokenRepository.findByToken(token);
        if(verificationToken==null)
        {
            return "invalid";
        }
        User user=verificationToken.getUser();
        Calendar calendar=Calendar.getInstance();
        if(verificationToken.getExpirationTime().getTime()-calendar.getTime().getTime()<=0 )
        {
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }
        user.setEnable(true);
        userRepository.save(user);
        return "valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String token) {
        VerificationToken verificationToken =verificationTokenRepository.findByToken(token);
        if(verificationToken==null)
        {
            return null;
        }
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public User findUserByEmail(String email) {
        User user=userRepository.findByEmail(email);
        return user;
    }

    @Override
    public PasswordResetToken createPasswordResetTokenforUser(User user, String token) {

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUser(user).orElse(null);
        if (passwordResetToken != null) {
            passwordResetToken=new PasswordResetToken(token);
        } else {
            passwordResetToken = new PasswordResetToken(token,user);
        }
        passwordResetTokenRepository.save(passwordResetToken);

        return passwordResetToken;
    }
    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken=passwordResetTokenRepository.findByToken(token);
        if(passwordResetToken==null)
        {
            return "invalid";
        }
        User user=passwordResetToken.getUser();
        Calendar calendar=Calendar.getInstance();
        if(passwordResetToken.getExpirationTime().getTime()-calendar.getTime().getTime()<=0 )
        {
            passwordResetTokenRepository.delete(passwordResetToken);
            return "expired";
        }
        return "valid";

    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean checkOldPasswordValid(User user, String oldPassword) {
       return passwordEncoder.matches(user.getPassword(), oldPassword);
    }

}
