package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.dtos.UsuarioCadastroDTO;
import com.RuanPablo2.mercadoapi.dtos.UsuarioDTO;
import com.RuanPablo2.mercadoapi.entities.Usuario;
import com.RuanPablo2.mercadoapi.entities.enums.Role;
import com.RuanPablo2.mercadoapi.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioDTO save(UsuarioCadastroDTO dto, boolean isAdminCreating) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        Role role;
        if (isAdminCreating) {
            role = Role.ADMIN;
        } else {
            role = Role.CLIENTE;
        }

        dto.setSenha(passwordEncoder.encode(dto.getSenha()));

        Usuario usuario = new Usuario(dto);
        usuario.setRole(role);

        usuario = usuarioRepository.save(usuario);

        return new UsuarioDTO(usuario);
    }

    public List<UsuarioDTO> findAll(){
        List<Usuario> result = usuarioRepository.findAll();
        return result.stream().map(x -> new UsuarioDTO(x)).toList();
    }

    public UsuarioDTO findById(Long id){
        Usuario result = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario não encontrado"));;
        return new UsuarioDTO(result);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional
    public UsuarioDTO update(Long id, UsuarioCadastroDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.atualizarUsuario(dto);
        usuario = usuarioRepository.save(usuario);
        return new UsuarioDTO(usuario);
    }

    @Transactional
    public void delete(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuarioRepository.delete(usuario);
    }
}