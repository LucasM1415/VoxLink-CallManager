package org.example.voxlink_backend.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String email;

    private String senha;

    private String codigoLogin;

    @Builder.Default
    private String role = "USUARIO"; // Role padrão

    private String cargo; // Pode ser nulo
}
