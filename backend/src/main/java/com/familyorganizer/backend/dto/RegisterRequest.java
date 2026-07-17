package com.familyorganizer.backend.dto;

import com.familyorganizer.backend.model.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private Role role;
    
    // Si la familia no existe, se crea con este nombre
    private String familyName;
    
    // Este código sirve para unirse a una familia existente o crear una nueva
    private String secretCode;
}
