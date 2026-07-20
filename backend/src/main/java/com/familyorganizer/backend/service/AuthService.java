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

        // Buscar familia por código secreto
        Optional<Family> familyOpt = familyRepository.findBySecretCode(request.getSecretCode());
        Family family;

        if (familyOpt.isPresent()) {
            // El código ya existe: el usuario se une a esa familia
            family = familyOpt.get();
        } else {
            // El código no existe: se crea una familia nueva
            family = Family.builder()
                    .name(request.getFamilyName() != null && !request.getFamilyName().isBlank()
                            ? request.getFamilyName()
                            : "Mi Familia")
                    .secretCode(request.getSecretCode())
                    .build();
            family = familyRepository.save(family);
        }

        // El displayName por defecto es el username si no se especificó
        String displayName = (request.getDisplayName() != null && !request.getDisplayName().isBlank())
                ? request.getDisplayName()
                : request.getUsername();

        // Crear el usuario (rol MEMBER por defecto, asignado en la entidad)
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(displayName)
                .family(family)
                .build();

        userRepository.save(user);

        String jwtToken = jwtUtil.generateToken(user.getUsername());

        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .familyName(family.getName())
                .familyCode(family.getSecretCode())
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
                .displayName(user.getDisplayName())
                .familyName(user.getFamily().getName())
                .familyCode(user.getFamily().getSecretCode())
                .build();
    }
}
