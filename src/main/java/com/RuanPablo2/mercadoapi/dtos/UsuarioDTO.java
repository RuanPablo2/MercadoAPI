package com.RuanPablo2.mercadoapi.dtos;

import com.RuanPablo2.mercadoapi.entities.Usuario;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UsuarioDTO {

    private Long id;
    private String nome;
    private String email;
    private EnderecoDTO endereco;

    public UsuarioDTO(Usuario entity) {
        id = entity.getId();
        nome = entity.getNome();
        email = entity.getEmail();
        if (entity.getEndereco() != null) {
            endereco = new EnderecoDTO(entity.getEndereco());
        }
    }
}