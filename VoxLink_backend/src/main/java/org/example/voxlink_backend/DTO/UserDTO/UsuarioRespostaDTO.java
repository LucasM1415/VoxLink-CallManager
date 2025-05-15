package org.example.voxlink_backend.DTO.UserDTO;

import lombok.Data;

@Data
public class UsuarioRespostaDTO {
    private Long id;
    private String nome;
    private String email;
    private String codigoLogin;
    private String role;

}