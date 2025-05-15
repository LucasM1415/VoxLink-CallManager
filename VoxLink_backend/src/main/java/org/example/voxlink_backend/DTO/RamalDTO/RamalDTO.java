package org.example.voxlink_backend.DTO.RamalDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RamalDTO {
    private Long id;
    private Integer numero;
    private boolean ativo;
    private String codigoLoginUsuario;
}
