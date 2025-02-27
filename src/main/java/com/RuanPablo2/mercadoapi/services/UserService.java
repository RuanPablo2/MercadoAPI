package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.dtos.request.UserRegistrationDTO;
import com.RuanPablo2.mercadoapi.dtos.response.UserDTO;
import com.RuanPablo2.mercadoapi.entities.User;
import com.RuanPablo2.mercadoapi.entities.enums.Role;
import com.RuanPablo2.mercadoapi.exception.BusinessException;
import com.RuanPablo2.mercadoapi.exception.ForbiddenException;
import com.RuanPablo2.mercadoapi.exception.ResourceNotFoundException;
import com.RuanPablo2.mercadoapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new BusinessException("Email already registered", "USR-001");
        }

        if (userRepository.existsByCpf(dto.getCpf())) {
            throw new BusinessException("CPF already registered", "USR-002");
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

    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserDTO::new);
    }

    public UserDTO findById(Long id){
        User result = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found", "USR-404"));
        return new UserDTO(result);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public UserDTO update(Long id, UserRegistrationDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "USR-404"));

        if (!user.getEmail().equals(dto.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new ForbiddenException("Email already in use", "USR-EMAIL");
            }
        }

        user.updateUser(dto);
        user = userRepository.save(user);
        return new UserDTO(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "USR-404"));

        userRepository.delete(user);
    }
}