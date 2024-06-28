package com.example.Register.Register.repository;

import com.example.Register.Register.entity.PasswordResetToken;
import com.example.Register.Register.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
    PasswordResetToken findByToken(String token);

    boolean existsByToken(String token);

    PasswordResetToken existsByUser(User user);

    Optional<PasswordResetToken> findByUser(User user);
}
