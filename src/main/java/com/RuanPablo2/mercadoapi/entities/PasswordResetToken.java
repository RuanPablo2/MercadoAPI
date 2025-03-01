package com.RuanPablo2.mercadoapi.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Entity
@Table(name = "tb_password_reset_token")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    public PasswordResetToken(String token, User user, LocalDateTime expirationDate) {
        this.token = token;
        this.user = user;
        this.expirationDate = expirationDate;
    }

    public boolean isExpired() {
        return expirationDate.isBefore(LocalDateTime.now());
    }
}