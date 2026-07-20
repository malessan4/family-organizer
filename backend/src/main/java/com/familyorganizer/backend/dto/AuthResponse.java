package com.familyorganizer.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String displayName;
    private String familyName;
    private String familyCode; // Para que el usuario pueda compartirlo con su familia
}
