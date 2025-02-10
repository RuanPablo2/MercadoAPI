package com.RuanPablo2.mercadoapi.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDTO {

    private String token;
    private Long usuarioId;
    private String nome;
    private String email;
}