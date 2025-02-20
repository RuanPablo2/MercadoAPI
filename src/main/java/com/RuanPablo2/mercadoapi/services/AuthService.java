package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.dtos.request.LoginRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.response.LoginResponseDTO;
import com.RuanPablo2.mercadoapi.dtos.response.UserDTO;
import com.RuanPablo2.mercadoapi.dtos.request.UserRegistrationDTO;
import com.RuanPablo2.mercadoapi.exception.BusinessException;
import com.RuanPablo2.mercadoapi.security.CustomUserDetails;
import com.RuanPablo2.mercadoapi.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String jwt = jwtUtil.generateToken(userDetails.getEmail(), userDetails.getRole().toString());

            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(jwt);
            response.setUserId(userDetails.getId());
            response.setName(userDetails.getName());
            response.setEmail(userDetails.getEmail());

            return response;
        } catch (AuthenticationException e) {
            throw new RuntimeException("Usuário ou senha inválidos", e);
        }
    }

    public UserDTO register(UserRegistrationDTO userRegistrationDTO, boolean isAdminCreating) {
        if (userService.findByEmail(userRegistrationDTO.getEmail()).isPresent()) {
            throw new BusinessException("Email already registered", "USR-001");
        }

        return userService.save(userRegistrationDTO, isAdminCreating);
    }
}