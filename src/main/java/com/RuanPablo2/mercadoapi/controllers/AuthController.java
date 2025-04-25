package com.RuanPablo2.mercadoapi.controllers;


import com.RuanPablo2.mercadoapi.dtos.request.ForgotPasswordRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.request.LoginRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.request.ResetPasswordRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.response.LoginResponseDTO;
import com.RuanPablo2.mercadoapi.dtos.request.UserRegistrationDTO;
import com.RuanPablo2.mercadoapi.dtos.response.UserDTO;
import com.RuanPablo2.mercadoapi.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO loginRequest, HttpServletResponse response) {
        try {
            LoginResponseDTO loginResponse = authService.login(loginRequest);

            Cookie jwtCookie = new Cookie("jwt", loginResponse.getToken());
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60);

            response.addCookie(jwtCookie);

            loginResponse.setToken(null);
            return ResponseEntity.ok(loginResponse);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid UserRegistrationDTO userRegistrationDTO) {
            UserDTO savedUser = authService.register(userRegistrationDTO, false);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDTO requestDTO) {
        authService.requestPasswordReset(requestDTO);
        return ResponseEntity.ok("If the email exists, a password reset link has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO requestDTO) {
        authService.resetPassword(requestDTO);
        return ResponseEntity.ok("Password has been reset successfully.");
    }
}