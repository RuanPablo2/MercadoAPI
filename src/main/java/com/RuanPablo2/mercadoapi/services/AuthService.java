package com.RuanPablo2.mercadoapi.services;

import com.RuanPablo2.mercadoapi.dtos.request.ForgotPasswordRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.request.LoginRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.request.ResetPasswordRequestDTO;
import com.RuanPablo2.mercadoapi.dtos.response.LoginResponseDTO;
import com.RuanPablo2.mercadoapi.dtos.response.UserDTO;
import com.RuanPablo2.mercadoapi.dtos.request.UserRegistrationDTO;
import com.RuanPablo2.mercadoapi.dtos.response.UserSessionDTO;
import com.RuanPablo2.mercadoapi.entities.PasswordResetToken;
import com.RuanPablo2.mercadoapi.entities.User;
import com.RuanPablo2.mercadoapi.exception.BusinessException;
import com.RuanPablo2.mercadoapi.exception.ResourceNotFoundException;
import com.RuanPablo2.mercadoapi.exception.UnauthorizedException;
import com.RuanPablo2.mercadoapi.repositories.PasswordResetTokenRepository;
import com.RuanPablo2.mercadoapi.repositories.UserRepository;
import com.RuanPablo2.mercadoapi.security.CustomUserDetails;
import com.RuanPablo2.mercadoapi.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${app.reset-password-base-url}")
    private String resetPasswordBaseUrl;

    @Autowired
    private EmailService emailService;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String jwt = jwtUtil.generateToken(userDetails.getEmail(), userDetails.getRole().toString());

            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(jwt);
            response.setUserId(userDetails.getId());
            response.setName(userDetails.getName());
            response.setEmail(userDetails.getEmail());

            return response;
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Invalid username or password", "AUTH-001", e);
        }
    }

    public void logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
    }

    public UserDTO register(UserRegistrationDTO userRegistrationDTO, boolean isAdminCreating) {
        if (userService.findByEmail(userRegistrationDTO.getEmail()).isPresent()) {
            throw new BusinessException("Email already registered", "USR-001");
        }

        return userService.save(userRegistrationDTO, isAdminCreating);
    }

    @Transactional
    public void requestPasswordReset(ForgotPasswordRequestDTO requestDTO) {
        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "USR-404"));

        // Remove token antigo, se existir
        passwordResetTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        LocalDateTime expiration = LocalDateTime.now().plusHours(1);

        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiration);
        passwordResetTokenRepository.save(resetToken);

        String resetLink = resetPasswordBaseUrl + "?token=" + token;

        String htmlContent = "<html><body>"
                + "<h2>Password Reset Request</h2>"
                + "<p>Hello " + user.getName() + ",</p>"
                + "<p>You requested to reset your password. Click the link below to proceed:</p>"
                + "<a href='" + resetLink + "'>Reset Password</a>"
                + "<p>If you did not request a password reset, please ignore this email.</p>"
                + "</body></html>";

        emailService.sendHtmlEmail(user.getEmail(), "Password Reset Request", htmlContent);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO requestDTO) {
        if (!requestDTO.getNewPassword().equals(requestDTO.getConfirmPassword())) {
            throw new BusinessException("Passwords do not match", "RST-003");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(requestDTO.getToken())
                .orElseThrow(() -> new BusinessException("Invalid or expired token", "RST-001"));

        if (resetToken.isExpired()) {
            throw new BusinessException("Token expired", "RST-002");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }

    public UserSessionDTO getCurrentUser(CustomUserDetails userDetails) {
        return new UserSessionDTO(
                userDetails.getId(),
                userDetails.getName(),
                userDetails.getEmail(),
                userDetails.getRole()
        );
    }
}