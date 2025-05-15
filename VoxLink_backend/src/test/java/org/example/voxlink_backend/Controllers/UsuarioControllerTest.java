package org.example.voxlink_backend.Controllers;


import org.example.voxlink_backend.DTO.UserDTO.UsuarioCadastroDTO;
import org.example.voxlink_backend.DTO.UserDTO.UsuarioRespostaDTO;
import org.example.voxlink_backend.Model.Usuario;
import org.example.voxlink_backend.Service.JwtService;
import org.example.voxlink_backend.Service.UsuarioService;
import org.example.voxlink_backend.Mappers.UsuarioMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioController usuarioController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
    }

    @Test
    void testarCadastroUsuario() throws Exception {
        UsuarioCadastroDTO dto = new UsuarioCadastroDTO();
        dto.setNome("usuario");
        dto.setSenha("senha");
        dto.setEmail("email@example.com");

        Usuario usuario = new Usuario();
        usuario.setNome("usuario");
        usuario.setSenha("senha");
        usuario.setEmail("email@example.com");

        UsuarioRespostaDTO respostaDTO = new UsuarioRespostaDTO();
        respostaDTO.setNome("usuario");
        respostaDTO.setEmail("email@example.com");

        when(usuarioMapper.toEntity(dto)).thenReturn(usuario);
        when(usuarioService.salvar(usuario)).thenReturn(usuario);
        when(usuarioMapper.toDTO(usuario)).thenReturn(respostaDTO);

        mockMvc.perform(post("/usuarios")
                        .contentType("application/json")
                        .content("{\"nome\": \"usuario\", \"senha\": \"senha\", \"email\": \"email@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("usuario"))
                .andExpect(jsonPath("$.email").value("email@example.com"));

        verify(usuarioService, times(1)).salvar(usuario);
    }

    @Test
    void testarBuscarUsuarioPorCodigo() throws Exception {
        String codigo = "usuario";

        // Criando o DTO e a entidade Usuario
        Usuario usuario = new Usuario();
        usuario.setNome("usuario");
        usuario.setSenha("senha");
        usuario.setEmail("email@example.com");
        usuario.setRole("USUARIO");

        UsuarioRespostaDTO respostaDTO = new UsuarioRespostaDTO();
        respostaDTO.setNome("usuario");
        respostaDTO.setEmail("email@example.com");
        respostaDTO.setCodigoLogin("usuario");
        respostaDTO.setRole("USUARIO");

        // Simulando o comportamento dos mocks
        when(usuarioService.buscarPorCodigo(codigo)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(respostaDTO);

        // Realizando a requisição GET e verificando a resposta
        mockMvc.perform(get("/usuarios/{codigo}", codigo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoLogin").value("usuario"))
                .andExpect(jsonPath("$.nome").value("usuario"))
                .andExpect(jsonPath("$.email").value("email@example.com"))
                .andExpect(jsonPath("$.role").value("USUARIO"));

        // Verificando se o serviço foi chamado uma vez
        verify(usuarioService, times(1)).buscarPorCodigo(codigo);
    }


    @Test
    void testarBuscarUsuarioPorCodigoNaoEncontrado() throws Exception {
        String codigo = "usuario";

        when(usuarioService.buscarPorCodigo(codigo)).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuarios/{codigo}", codigo))
                .andExpect(status().isNotFound());

        verify(usuarioService, times(1)).buscarPorCodigo(codigo);
    }

    @Test
    void testarExcluirUsuario() throws Exception {
        Long id = 1L;
        String authHeader = "Bearer validToken";

        // Criando o usuário autenticado (Gerente)
        Usuario usuarioAutenticado = new Usuario();
        usuarioAutenticado.setNome("gerente");
        usuarioAutenticado.setSenha("senha");
        usuarioAutenticado.setEmail("email@example.com");
        usuarioAutenticado.setRole("GERENTE");

        // Simulando o comportamento dos mocks
        when(usuarioService.buscarPorCodigo("gerente")).thenReturn(Optional.of(usuarioAutenticado));
        when(usuarioService.existePorId(id)).thenReturn(true);

        // Realizando a requisição DELETE e verificando a resposta
        mockMvc.perform(delete("/usuarios/{id}", id)
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuário excluído com sucesso."));

        // Verificando se o serviço foi chamado uma vez
        verify(usuarioService, times(1)).excluirPorId(id);
    }



    @Test
    void testarPerfilAutenticado() throws Exception {
        String codigoLogin = "usuario_logado";
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(codigoLogin, null));

        mockMvc.perform(get("/usuarios/perfil"))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuário logado: " + codigoLogin));
    }
}

