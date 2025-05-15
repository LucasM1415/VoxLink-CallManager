package org.example.voxlink_backend.Controllers;

import org.example.voxlink_backend.Service.RamalService;
import org.example.voxlink_backend.Service.JwtService;
import org.example.voxlink_backend.Model.Ramal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RamalControllerTest {

    @Mock
    private RamalService ramalService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private RamalController ramalController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ramalController).build();
    }

    @Test
    public void testCriarRamal_Sucesso() throws Exception {
        Ramal ramal = new Ramal();
        ramal.setNumero(101);

        when(ramalService.criarRamal(any(Ramal.class))).thenReturn("Ramal criado com sucesso.");

        mockMvc.perform(post("/ramais/criar")
                        .contentType("application/json")
                        .content("{\"numero\": 101}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Ramal criado com sucesso."));
    }

    @Test
    public void testCriarRamal_Falha() throws Exception {
        Ramal ramal = new Ramal();
        ramal.setNumero(101);

        when(ramalService.criarRamal(any(Ramal.class))).thenReturn("Erro ao criar ramal.");

        mockMvc.perform(post("/ramais/criar")
                        .contentType("application/json")
                        .content("{\"numero\": 101}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro ao criar ramal."));
    }

    @Test
    public void testMostrarRamaisDisponiveis() throws Exception {
        Ramal ramal1 = new Ramal();
        ramal1.setNumero(101);
        Ramal ramal2 = new Ramal();
        ramal2.setNumero(102);

        List<Ramal> ramaisDisponiveis = Arrays.asList(ramal1, ramal2);

        when(ramalService.buscarRamaisDisponiveis()).thenReturn(ramaisDisponiveis);

        mockMvc.perform(get("/ramais/disponiveis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numero").value(101))
                .andExpect(jsonPath("$[1].numero").value(102));
    }

    @Test
    public void testBuscarRamalPorNumero_Existe() throws Exception {
        Ramal ramal = new Ramal();
        ramal.setNumero(101);

        when(ramalService.buscarPorNumero(101)).thenReturn(Optional.of(ramal));

        mockMvc.perform(get("/ramais/numero/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(101));
    }

    @Test
    public void testBuscarRamalPorNumero_NaoExiste() throws Exception {
        when(ramalService.buscarPorNumero(101)).thenReturn(Optional.empty());

        mockMvc.perform(get("/ramais/numero/101"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testReativarRamal_Gerente() throws Exception {
        String authHeader = "Bearer valid-token";
        String resultado = "Ramal reativado com sucesso.";

        when(jwtService.extrairCodigoLogin(authHeader.replace("Bearer ", ""))).thenReturn("codigoValido");
        when(jwtService.ehGerente("codigoValido")).thenReturn(true);
        when(ramalService.reativarRamal(1L)).thenReturn(resultado);

        mockMvc.perform(post("/ramais/reativar/1")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(content().string("Ramal reativado com sucesso."));
    }

    @Test
    public void testReativarRamal_NaoGerente() throws Exception {
        String authHeader = "Bearer valid-token";

        when(jwtService.extrairCodigoLogin(authHeader.replace("Bearer ", ""))).thenReturn("codigoValido");
        when(jwtService.ehGerente("codigoValido")).thenReturn(false);

        mockMvc.perform(post("/ramais/reativar/1")
                        .header("Authorization", authHeader))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Apenas gerentes podem reativar ramais."));
    }
}
