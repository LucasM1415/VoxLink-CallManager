package org.example.voxlink_backend.Repository;

import org.example.voxlink_backend.Model.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    Optional<Usuario> findByCodigoLogin(String codigoLogin);

    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    Optional<Usuario> findByEmail(String email);



}
