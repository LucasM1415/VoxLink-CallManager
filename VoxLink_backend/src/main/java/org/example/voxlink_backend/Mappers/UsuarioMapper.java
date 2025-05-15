package org.example.voxlink_backend.Mappers;

import org.example.voxlink_backend.DTO.UserDTO.UsuarioCadastroDTO;
import org.example.voxlink_backend.DTO.UserDTO.UsuarioRespostaDTO;
import org.example.voxlink_backend.Model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioCadastroDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setCargo(dto.getCargo());
        usuario.setCodigoLogin(gerarCodigoLogin(dto.getNome()));
        return usuario;
    }

    public UsuarioRespostaDTO toDTO(Usuario usuario) {
        UsuarioRespostaDTO dto = new UsuarioRespostaDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setCodigoLogin(usuario.getCodigoLogin());
        dto.setRole(usuario.getRole());
        return dto;
    }

    private String gerarCodigoLogin(String nome) {
        return nome.toLowerCase().replaceAll("\\s+", "") + System.currentTimeMillis();
    }
}
