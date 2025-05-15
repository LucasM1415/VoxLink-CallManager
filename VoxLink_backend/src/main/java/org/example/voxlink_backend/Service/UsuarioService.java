package org.example.voxlink_backend.Service;

import lombok.RequiredArgsConstructor;
import org.example.voxlink_backend.Model.Usuario;
import org.example.voxlink_backend.Repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String codigoLogin) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCodigoLogin(codigoLogin)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return User.builder()
                .username(usuario.getCodigoLogin())
                .password(usuario.getSenha())
                .roles(usuario.getRole())
                .build();
    }

    // Buscar por código de login
    public Optional<Usuario> buscarPorCodigo(String codigoLogin) {
        return usuarioRepository.findByCodigoLogin(codigoLogin);
    }

    // Salvar usuário (com senha codificada)
    public Usuario salvar(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        Usuario salvo = usuarioRepository.save(usuario);



        return salvo;
    }

    // Buscar todos os usuários
    public Iterable<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

    // Buscar usuários por nome (parcial)
    public List<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome);
    }

    // Excluir usuário por ID (apenas se for GERENTE)
    public void excluirPorId(Long id) {
        usuarioRepository.deleteById(id);
    }

    // Verificar se o usuário existe por ID
    public boolean existePorId(Long id) {
        return usuarioRepository.existsById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public void atualizarSenha(Long id, String novaSenha) {
        var usuario = usuarioRepository.findById(id).orElseThrow();
        String senhaHash = passwordEncoder.encode(novaSenha);  // aqui você faz o hash
        usuario.setSenha(senhaHash);
        usuarioRepository.save(usuario);
    }
    public String gerarSenhaProvisoria() {
        return "SenhaNova" + (int)(Math.random() * 10000);  // ex: SenhaNova3456
    }




}