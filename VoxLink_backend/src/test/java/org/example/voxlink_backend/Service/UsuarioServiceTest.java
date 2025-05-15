package org.example.voxlink_backend.Service;


import org.example.voxlink_backend.Model.Usuario;
import org.example.voxlink_backend.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock


    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCodigoLogin("ADM001");
        usuario.setSenha("senha123");
        usuario.setEmail("teste@email.com");
        usuario.setRole("GERENTE");
        usuario.setNome("Admin");
    }

    @Test
    void loadUserByUsername_DeveRetornarUserDetails_QuandoUsuarioExistir() {
        when(usuarioRepository.findByCodigoLogin("ADM001")).thenReturn(Optional.of(usuario));

        var userDetails = usuarioService.loadUserByUsername("ADM001");

        assertEquals(usuario.getCodigoLogin(), userDetails.getUsername());
        assertEquals(usuario.getSenha(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + usuario.getRole())));
    }

    @Test
    void loadUserByUsername_DeveLancarException_QuandoUsuarioNaoExistir() {
        when(usuarioRepository.findByCodigoLogin("NAO_EXISTE")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> usuarioService.loadUserByUsername("NAO_EXISTE"));
    }

    @Test
    void salvar_DeveCodificarSenha_EEnviarEmail() {
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCodificada");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario salvo = usuarioService.salvar(usuario);

        assertEquals("senhaCodificada", salvo.getSenha());
    }

    @Test
    void buscarPorCodigo_DeveRetornarUsuario() {
        when(usuarioRepository.findByCodigoLogin("ADM001")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.buscarPorCodigo("ADM001");

        assertTrue(resultado.isPresent());
        assertEquals(usuario, resultado.get());
    }

    @Test
    void buscarTodos_DeveChamarFindAll() {
        usuarioService.buscarTodos();
        verify(usuarioRepository).findAll();
    }

    @Test
    void buscarPorNome_DeveChamarFindByNomeContainingIgnoreCase() {
        usuarioService.buscarPorNome("Admin");
        verify(usuarioRepository).findByNomeContainingIgnoreCase("Admin");
    }

    @Test
    void excluirPorId_DeveChamarDeleteById() {
        usuarioService.excluirPorId(1L);
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void existePorId_DeveRetornarTrue_SeExistir() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        boolean existe = usuarioService.existePorId(1L);

        assertTrue(existe);
    }

    @Test
    void existePorId_DeveRetornarFalse_SeNaoExistir() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        boolean existe = usuarioService.existePorId(1L);

        assertFalse(existe);
    }
}
