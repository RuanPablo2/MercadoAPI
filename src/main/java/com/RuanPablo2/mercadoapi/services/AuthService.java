package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.dtos.LoginRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.LoginResponseDTO;
import com.RuanPablo2.mercadoapi.dtos.UsuarioCadastroDTO;
import com.RuanPablo2.mercadoapi.dtos.UsuarioDTO;
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
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String jwt = jwtUtil.generateToken(userDetails.getEmail(), userDetails.getRole().toString());

            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(jwt);
            response.setUsuarioId(userDetails.getId());
            response.setNome(userDetails.getNome());
            response.setEmail(userDetails.getEmail());

            return response;
        } catch (AuthenticationException e) {
            throw new RuntimeException("Usuário ou senha inválidos", e);
        }
    }

    public UsuarioDTO register(UsuarioCadastroDTO usuarioCadastroDTO, boolean isAdminCreating) {
        if (usuarioService.findByEmail(usuarioCadastroDTO.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        return usuarioService.save(usuarioCadastroDTO, isAdminCreating);
    }
}