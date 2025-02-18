package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.dtos.UserRegistrationDTO;
import com.RuanPablo2.mercadoapi.dtos.UserDTO;
import com.RuanPablo2.mercadoapi.entities.User;
import com.RuanPablo2.mercadoapi.entities.enums.Role;
import com.RuanPablo2.mercadoapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO save(UserRegistrationDTO dto, boolean isAdminCreating) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        Role role;
        if (isAdminCreating) {
            role = Role.ROLE_ADMIN;
        } else {
            role = Role.ROLE_CLIENT;
        }

        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        User user = new User(dto);
        user.setRole(role);

        user = userRepository.save(user);

        return new UserDTO(user);
    }

    public List<UserDTO> findAll(){
        List<User> result = userRepository.findAll();
        return result.stream().map(x -> new UserDTO(x)).toList();
    }

    public UserDTO findById(Long id){
        User result = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));;
        return new UserDTO(result);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public UserDTO update(Long id, UserRegistrationDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        user.updateUser(dto);
        user = userRepository.save(user);
        return new UserDTO(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        userRepository.delete(user);
    }
}