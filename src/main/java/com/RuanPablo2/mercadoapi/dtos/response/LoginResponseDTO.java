package com.RuanPablo2.mercadoapi.dtos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDTO {
    
    private Long userId;
    private String name;
    private String email;
}