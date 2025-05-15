package org.example.voxlink_backend.Service;

import org.example.voxlink_backend.Model.Ramal;
import org.example.voxlink_backend.Model.Usuario;
import org.example.voxlink_backend.Repository.RamalRepository;
import org.example.voxlink_backend.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RamalServiceTest {

    @InjectMocks
    private RamalService ramalService;

    @Mock
    private RamalRepository ramalRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private Ramal ramal;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inicializando objetos para os testes
        ramal = new Ramal();
        ramal.setId(1L);
        ramal.setNumero(101);
        ramal.setAtivo(true);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCodigoLogin("USER001");
    }

    @Test
    void criarRamal_Success() {
        // Simulando a resposta do repositório
        when(ramalRepository.findByNumeroAndAtivo(101, true)).thenReturn(Optional.empty());
        when(ramalRepository.save(any(Ramal.class))).thenReturn(ramal);

        // Chamando o método
        String resultado = ramalService.criarRamal(ramal);

        // Verificando se o resultado é o esperado
        assertEquals("Ramal criado com sucesso.", resultado);

        // Verificando as interações com o repositório
        verify(ramalRepository, times(1)).save(ramal);
    }

    @Test
    void associarUsuarioARamal_Success() {
        // Simulando a resposta dos repositórios
        when(ramalRepository.findByNumeroAndAtivo(101, true)).thenReturn(Optional.of(ramal));
        when(usuarioRepository.findByCodigoLogin("USER001")).thenReturn(Optional.of(usuario));

        // Chamando o método
        String resultado = ramalService.associarUsuarioARamal("USER001", 101);

        // Verificando se o resultado é o esperado
        assertEquals("Usuário associado ao ramal com sucesso.", resultado);

        // Verificando se o ramal foi salvo com o usuário associado
        verify(ramalRepository, times(1)).save(ramal);
    }

    @Test
    void associarUsuarioARamal_RamalOcupado() {
        ramal.setUsuario(usuario); // Simulando ramal já ocupado

        when(ramalRepository.findByNumeroAndAtivo(101, true)).thenReturn(Optional.of(ramal));

        // Chamando o método
        String resultado = ramalService.associarUsuarioARamal("USER001", 101);

        // Verificando a resposta para ramal já ocupado
        assertEquals("Ramal já está ocupado.", resultado);
    }

    @Test
    void desassociarUsuarioDoRamal_Success() {
        // Simulando a resposta do repositório
        when(ramalRepository.findByUsuario_CodigoLogin("USER001")).thenReturn(Optional.of(ramal));

        // Verificando a interação com o repositório
        verify(ramalRepository, times(1)).save(ramal);
    }

}
