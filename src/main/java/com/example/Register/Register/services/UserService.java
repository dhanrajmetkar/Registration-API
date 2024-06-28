package com.example.Register.Register.services;

import com.example.Register.Register.entity.PasswordResetToken;
import com.example.Register.Register.entity.User;
import com.example.Register.Register.entity.VerificationToken;
import com.example.Register.Register.model.UserModel;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String token);

    User findUserByEmail(String email);

    PasswordResetToken createPasswordResetTokenforUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    boolean checkOldPasswordValid(User user, String oldPassword);
}
