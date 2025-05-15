package org.example.voxlink_backend.DTO.UserDTO;

import lombok.Data;

@Data
public class UsuarioCadastroDTO {
    private String nome;
    private String senha;
    private String email;
    private String cargo;
}

