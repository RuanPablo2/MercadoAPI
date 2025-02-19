package com.RuanPablo2.mercadoapi.controllers;


import com.RuanPablo2.mercadoapi.dtos.LoginRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.LoginResponseDTO;
import com.RuanPablo2.mercadoapi.dtos.UserRegistrationDTO;
import com.RuanPablo2.mercadoapi.dtos.UserDTO;
import com.RuanPablo2.mercadoapi.services.AuthService;
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
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid UserRegistrationDTO userRegistrationDTO) {
            UserDTO savedUser = authService.register(userRegistrationDTO, false);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}