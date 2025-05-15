package org.example.voxlink_backend.Repository;

import org.example.voxlink_backend.Model.Ramal;
import org.example.voxlink_backend.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RamalRepository extends JpaRepository<Ramal, Long> {

    List<Ramal> findByAtivoAndUsuarioIsNull(boolean ativo); // Ramais disponíveis (ativos e não associados a nenhum usuário)

    List<Ramal> findByAtivoAndUsuarioIsNotNull(boolean ativo); // Ramais indisponíveis (ativos e associados a um usuário)

    List<Ramal> findByAtivoTrue();  // Buscando por ramais que estão ativos (ativo = true)

    Optional<Ramal> findByNumeroAndAtivo(Integer numero, boolean ativo); // Buscar ramal pelo número e ativo

    List<Ramal> findByAtivoFalse();  // Buscando por ramais que estão inativos (ativo = false)

    Optional<Ramal> findByUsuario_CodigoLogin(String codigoLogin);// Buscando pelo usuario associado

    Optional<Ramal> findByNumeroAndUsuario(Integer numero, Usuario usuario);

    List<Ramal> findByUsuario(Usuario usuario);
















}
