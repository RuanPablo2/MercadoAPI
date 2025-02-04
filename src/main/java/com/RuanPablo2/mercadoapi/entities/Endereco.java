package com.RuanPablo2.mercadoapi.entities;

import com.RuanPablo2.mercadoapi.dtos.EnderecoDTO;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_endereco")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;

    @OneToOne(mappedBy = "endereco")
    private Usuario usuario;

    public Endereco(String rua, String numero, String complemento, String bairro, String cidade, String estado, String cep, Usuario usuario) {
        this.rua = rua;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.usuario = usuario;
    }

    public Endereco(EnderecoDTO dto) {
        this.rua = dto.getRua();
        this.numero = dto.getNumero();
        this.complemento = dto.getComplemento();
        this.bairro = dto.getBairro();
        this.cidade = dto.getCidade();
        this.estado = dto.getEstado();
        this.cep = dto.getCep();
    }
}