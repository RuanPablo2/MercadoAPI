package com.RuanPablo2.mercadoapi.controllers;


import com.RuanPablo2.mercadoapi.dtos.LoginRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.LoginResponseDTO;
import com.RuanPablo2.mercadoapi.dtos.UsuarioCadastroDTO;
import com.RuanPablo2.mercadoapi.dtos.UsuarioDTO;
import com.RuanPablo2.mercadoapi.entities.Endereco;
import com.RuanPablo2.mercadoapi.entities.Usuario;
import com.RuanPablo2.mercadoapi.entities.enums.Role;
import com.RuanPablo2.mercadoapi.security.CustomUserDetails;
import com.RuanPablo2.mercadoapi.security.CustomUserDetailsService;
import com.RuanPablo2.mercadoapi.security.JwtUtil;
import com.RuanPablo2.mercadoapi.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<UsuarioDTO> register(@RequestBody @Valid UsuarioCadastroDTO usuarioCadastroDTO) {
        try {
            UsuarioDTO savedUser = authService.register(usuarioCadastroDTO, false);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}