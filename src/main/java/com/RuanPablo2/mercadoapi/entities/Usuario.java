package com.RuanPablo2.mercadoapi.entities;

import com.RuanPablo2.mercadoapi.entities.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;
    private String endereco;
    private String telefone;

    @Enumerated(EnumType.STRING)
    private Role role;

    public Usuario(String nome, String email, String senha, String endereco, String telefone, Role role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.endereco = endereco;
        this.telefone = telefone;
        this.role = role;
    }
}