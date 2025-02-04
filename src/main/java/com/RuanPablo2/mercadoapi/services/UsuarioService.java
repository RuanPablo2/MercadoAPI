package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.dtos.UsuarioCadastroDTO;
import com.RuanPablo2.mercadoapi.dtos.UsuarioDTO;
import com.RuanPablo2.mercadoapi.entities.Usuario;
import com.RuanPablo2.mercadoapi.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Transactional
    public UsuarioDTO save(UsuarioCadastroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado");
        }
        Usuario usuario = new Usuario(dto);
        usuario = usuarioRepository.save(usuario);
        return new UsuarioDTO(usuario);
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