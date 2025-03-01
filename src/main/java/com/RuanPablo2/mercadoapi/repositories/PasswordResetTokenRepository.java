package com.RuanPablo2.mercadoapi.repositories;

import com.RuanPablo2.mercadoapi.entities.PasswordResetToken;
import com.RuanPablo2.mercadoapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);
    void deleteByUser(User user);
}