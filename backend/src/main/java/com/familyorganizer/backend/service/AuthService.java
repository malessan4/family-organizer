package com.familyorganizer.backend.service;

import com.familyorganizer.backend.dto.AuthRequest;
import com.familyorganizer.backend.dto.AuthResponse;
import com.familyorganizer.backend.dto.RegisterRequest;
import com.familyorganizer.backend.model.Family;
import com.familyorganizer.backend.model.User;
import com.familyorganizer.backend.repository.FamilyRepository;
import com.familyorganizer.backend.repository.UserRepository;
import com.familyorganizer.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        
        // Verificar si la familia ya existe mediante el código secreto
        Optional<Family> familyOpt = familyRepository.findBySecretCode(request.getSecretCode());
        Family family;
        
        if (familyOpt.isPresent()) {
            family = familyOpt.get();
        } else {
            // Si no existe, creamos una nueva
            family = Family.builder()
                    .name(request.getFamilyName() != null ? request.getFamilyName() : "Familia")
                    .secretCode(request.getSecretCode())
                    .build();
            family = familyRepository.save(family);
        }

        // Crear el usuario
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .family(family)
                .build();
                
        userRepository.save(user);

        // Generar JWT
        String jwtToken = jwtUtil.generateToken(user.getUsername());
        
        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .familyName(family.getName())
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
                
        String jwtToken = jwtUtil.generateToken(user.getUsername());
        
        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .familyName(user.getFamily().getName())
                .build();
    }
}
