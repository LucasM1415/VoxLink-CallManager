package org.example.voxlink_backend.Model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ramal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numero;

    private boolean ativo;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
