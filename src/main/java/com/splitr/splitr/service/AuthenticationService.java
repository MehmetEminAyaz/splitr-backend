package com.splitr.splitr.service;

import com.splitr.splitr.dto.AuthResponse;
import com.splitr.splitr.dto.LoginRequest;
import com.splitr.splitr.dto.RegisterRequest;
import com.splitr.splitr.entity.User;
import com.splitr.splitr.repository.UserRepository;
import com.splitr.splitr.util.UserCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Benzersiz 5 haneli userCode oluştur
        String userCode = UserCodeGenerator.generateUniqueCode(userRepository);

        User user = User.builder()
                .userCode(userCode)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(); // Olmaması lazım çünkü authenticate oldu

        String jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }
}