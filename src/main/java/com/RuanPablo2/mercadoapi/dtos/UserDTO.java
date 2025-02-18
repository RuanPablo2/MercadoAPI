package com.RuanPablo2.mercadoapi.dtos;

import com.RuanPablo2.mercadoapi.entities.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private AddressDTO address;

    public UserDTO(User entity) {
        id = entity.getId();
        name = entity.getName();
        email = entity.getEmail();
        if (entity.getAddress() != null) {
            address = new AddressDTO(entity.getAddress());
        }
    }
}