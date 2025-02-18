package com.RuanPablo2.mercadoapi.entities;

import com.RuanPablo2.mercadoapi.dtos.UserRegistrationDTO;
import com.RuanPablo2.mercadoapi.entities.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Entity
@Table(name = "tb_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String cpf;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    public User(String name, String email, String password, Address address, String phoneNumber, String cpf, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.cpf = cpf;
        this.role = role;
    }

    public void updateUser(UserRegistrationDTO dto) {
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.password = dto.getPassword();
        this.cpf = dto.getCpf();
        this.address = new Address(dto.getAddress());
    }

    public User(UserRegistrationDTO dto) {
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.password = dto.getPassword();
        this.address = new Address(dto.getAddress());
    }
}