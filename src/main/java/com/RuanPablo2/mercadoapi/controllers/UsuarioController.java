package com.RuanPablo2.mercadoapi.controllers;

import com.RuanPablo2.mercadoapi.dtos.UsuarioCadastroDTO;
import com.RuanPablo2.mercadoapi.dtos.UsuarioDTO;
import com.RuanPablo2.mercadoapi.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;


    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> findAll() {
        List<UsuarioDTO> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable Long id) {
        UsuarioDTO usuarioDTO = usuarioService.findById(id);
        return ResponseEntity.ok(usuarioDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody UsuarioCadastroDTO dto) {
        UsuarioDTO usuarioDTO = usuarioService.update(id, dto);
        return ResponseEntity.ok(usuarioDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}