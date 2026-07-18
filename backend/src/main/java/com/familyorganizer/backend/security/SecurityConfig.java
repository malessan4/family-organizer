package com.familyorganizer.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    // Lista de orígenes permitidos (viene de application.properties / variable de entorno)
    @Value("${allowed.origins:http://localhost:3000}")
    private String allowedOriginsRaw;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF: deshabilitado porque usamos JWT stateless (no hay sesiones ni cookies de sesión)
            .csrf(csrf -> csrf.disable())

            // CORS: configurado con orígenes específicos (NO wildcard en prod)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Reglas de autorización
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos: login y registro
                .requestMatchers("/api/auth/**").permitAll()
                // WebSockets: necesitan acceso sin token inicial para el handshake
                .requestMatchers("/ws/**").permitAll()
                // Preflight CORS: los navegadores envían OPTIONS antes de cada petición cross-origin.
                // Permitirlos NO es un riesgo: no llevan datos ni modifican nada.
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Todo lo demás requiere autenticación JWT válida
                .anyRequest().authenticated()
            )

            // Sin sesiones en servidor: cada request lleva su JWT
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // SEGURIDAD: orígenes explícitos desde variable de entorno.
        // En dev: "http://localhost:3000"
        // En prod: "https://tu-dominio.vercel.app"
        // NUNCA usar "*" con allowCredentials=true (los navegadores lo rechazan)
        List<String> origins = Arrays.asList(allowedOriginsRaw.split(","));
        configuration.setAllowedOrigins(origins);

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Solo los headers necesarios (no wildcard en prod)
        configuration.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With"
        ));

        // Exponer Authorization para que el cliente pueda leer el JWT desde la respuesta
        configuration.setExposedHeaders(List.of("Authorization"));

        // Permite enviar cookies y el header Authorization
        configuration.setAllowCredentials(true);

        // Cache del preflight por 1 hora (reduce peticiones OPTIONS innecesarias)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt con strength 12: más seguro que el default (10). Aumentar a 13+ en hardware moderno de prod.
        return new BCryptPasswordEncoder(12);
    }
}
