package com.familyorganizer.backend.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;

    // Nombre visible en la app (ej: "Mamá", "Alessandro", "Sofi")
    private String displayName;

    // Si la familia no existe, se crea con este nombre
    private String familyName;

    // Código para unirse a una familia existente o crear una nueva
    private String secretCode;

    // El rol se asigna automáticamente como MEMBER en el servidor
}
