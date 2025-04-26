package com.RuanPablo2.mercadoapi.dtos.response;

import com.RuanPablo2.mercadoapi.entities.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
}