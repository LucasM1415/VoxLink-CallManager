package org.example.voxlink_backend.Controllers;

import org.example.voxlink_backend.Service.RamalService;
import org.example.voxlink_backend.Service.JwtService;
import org.example.voxlink_backend.Model.Ramal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/ramais")
public class RamalController {

    @Autowired
    private RamalService ramalService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/criar")
    public ResponseEntity<String> criarRamal(@RequestBody Ramal ramal) {
        // Chama o serviço para criar o ramal
        String resultado = ramalService.criarRamal(ramal);
        if (resultado.equals("Ramal criado com sucesso.")) {
            return ResponseEntity.status(201).body(resultado);  // 201 Created
        } else {
            return ResponseEntity.status(400).body(resultado);  // 400 Bad Request
        }
    }

    // Mostrar ramais disponíveis (ativos e não associados a nenhum usuário)
    @GetMapping("/disponiveis")
    public ResponseEntity<List<Ramal>> mostrarRamaisDisponiveis() {
        List<Ramal> ramais = ramalService.buscarRamaisDisponiveis();
        return ResponseEntity.ok(ramais);
    }

    // Mostrar ramais indisponíveis (ativos e associados a um usuário)
    @GetMapping("/indisponiveis")
    public ResponseEntity<List<Ramal>> mostrarRamaisIndisponiveis() {
        List<Ramal> ramais = ramalService.buscarRamaisIndisponiveis();
        return ResponseEntity.ok(ramais);
    }

    // Mostrar todos os ramais (somente ativos)
    @GetMapping("/todos")
    public ResponseEntity<List<Ramal>> mostrarTodosOsRamais() {
        List<Ramal> ramais = ramalService.buscarTodosOsRamais();
        return ResponseEntity.ok(ramais);
    }

    // Buscar ramal por número (somente se ativo)
    @GetMapping("/numero/{numero}")
    public ResponseEntity<Ramal> buscarRamalPorNumero(@PathVariable Integer numero) {
        Optional<Ramal> ramal = ramalService.buscarPorNumero(numero);
        return ramal.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(404).body(null));
    }

    // Mostrar todos os ramais inativos (somente para o gerente)
    @GetMapping("/inativos")
    public ResponseEntity<List<Ramal>> mostrarRamaisInativos(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String codigoLogin = jwtService.extrairCodigoLogin(token);

        // Verificar se o usuário é gerente (isso deve ser feito com base no papel do usuário)
        if (!jwtService.ehGerente(codigoLogin)) {
            return ResponseEntity.status(403).body(null);  // 403 Forbidden se não for gerente
        }

        List<Ramal> ramaisInativos = ramalService.buscarRamaisInativos();
        return ResponseEntity.ok(ramaisInativos);
    }

    // Reativar um ramal (somente para o gerente)
    @PostMapping("/reativar/{ramalId}")
    public ResponseEntity<String> reativarRamal(@PathVariable Long ramalId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String codigoLogin = jwtService.extrairCodigoLogin(token);

        // Verificar se o usuário é gerente
        if (!jwtService.ehGerente(codigoLogin)) {
            return ResponseEntity.status(403).body("Apenas gerentes podem reativar ramais.");
        }

        String resultado = ramalService.reativarRamal(ramalId);
        return ResponseEntity.ok(resultado);
    }

    // Desativar um ramal (somente para o gerente)
    @PostMapping("/desativar")
    public ResponseEntity<String> desativarRamal(@RequestBody Map<String, Integer> body,
                                                 @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String codigoLogin = jwtService.extrairCodigoLogin(token);

        if (!jwtService.ehGerente(codigoLogin)) {
            return ResponseEntity.status(403).body("Apenas gerentes podem desativar ramais.");
        }

        Integer numeroRamal = body.get("numeroRamal");

        try {
            String resultado = ramalService.desativarRamalPorNumero(numeroRamal);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }


    @PostMapping("/associar/{numero}")
    public ResponseEntity<String> associarUsuarioARamal(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer numero) {

        String token = authHeader.replace("Bearer ", "");
        String codigoLogin = jwtService.extrairCodigoLogin(token);

        String resultado = ramalService.associarUsuarioARamal(codigoLogin, numero);
        return ResponseEntity.ok(resultado);
    }


    // Desassociar um ramal a um usuário
    @PostMapping("/desassociar/{numero}")
    public ResponseEntity<String> desassociarUsuarioDoRamal(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer numero) {

        String token = authHeader.replace("Bearer ", "");
        String codigoLogin = jwtService.extrairCodigoLogin(token);

        String resultado = ramalService.desassociarUsuarioDoRamal(codigoLogin, numero);
        return ResponseEntity.ok(resultado);
    }








}
