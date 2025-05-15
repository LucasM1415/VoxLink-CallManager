package org.example.voxlink_backend.Controllers;

import org.example.voxlink_backend.DTO.UserDTO.UsuarioCadastroDTO;
import org.example.voxlink_backend.DTO.UserDTO.UsuarioRespostaDTO;
import org.example.voxlink_backend.Mappers.UsuarioMapper;
import org.example.voxlink_backend.Model.Ramal;
import org.example.voxlink_backend.Model.Usuario;
import org.example.voxlink_backend.Repository.RamalRepository;
import org.example.voxlink_backend.Service.Email.EmailService;
import org.example.voxlink_backend.Service.JwtService;
import org.example.voxlink_backend.Service.RamalService;
import org.example.voxlink_backend.Service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final UsuarioMapper usuarioMapper;
    private final EmailService emailService;
    private final RamalService ramalService;

    public UsuarioController(
            UsuarioService usuarioService,
            JwtService jwtService,
            UsuarioMapper usuarioMapper,
            EmailService emailService,
            RamalService ramalService
    ) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.usuarioMapper = usuarioMapper;
        this.emailService = emailService;
        this.ramalService = ramalService;
    }



    // Cadastrar novo usuário
    @PostMapping
    public ResponseEntity<UsuarioRespostaDTO> cadastrar(@RequestBody UsuarioCadastroDTO dto) {
        // 1. Converter DTO para entidade e salvar o novo usuário
        Usuario novoUsuario = usuarioMapper.toEntity(dto);
        Usuario salvo = usuarioService.salvar(novoUsuario);

        // 2. Montar e-mail
        String subject = "Cadastro realizado com sucesso!";
        String body = String.format(
                "Olá %s,\n\nSeu cadastro foi realizado com sucesso!\n\n" +
                        "Seu código de login é: %s\n" +
                        "Por favor, guarde este código para futuras logins.\n\n" +
                        "Se tiver algum problema, entre em contato com o suporte.",
                salvo.getNome(), // Assumindo que você tem o nome do usuário
                salvo.getCodigoLogin()
        );

        // 3. Enviar e-mail
        emailService.sendEmail(
                salvo.getEmail(),  // Para quem enviar (email do usuário cadastrado)
                subject,
                body
        );

        // 4. Retornar a resposta
        return ResponseEntity.ok(usuarioMapper.toDTO(salvo));
    }


    // Buscar usuário por código de login
    @GetMapping("/{codigo}")
    public ResponseEntity<UsuarioRespostaDTO> buscarPorCodigo(@PathVariable String codigo) {
        Optional<Usuario> usuario = usuarioService.buscarPorCodigo(codigo);
        return usuario.map(u -> ResponseEntity.ok(usuarioMapper.toDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar usuários por nome
    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioRespostaDTO>> buscarPorNome(@RequestParam String nome) {
        List<Usuario> usuarios = usuarioService.buscarPorNome(nome);
        List<UsuarioRespostaDTO> dtos = usuarios.stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Excluir usuário (somente se for GERENTE)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String codigoLogin = jwtService.extrairCodigoLogin(token);
        Optional<Usuario> autenticado = usuarioService.buscarPorCodigo(codigoLogin);

        if (autenticado.isEmpty()) {
            return ResponseEntity.status(401).body("Usuário não autenticado.");
        }

        if (!"GERENTE".equalsIgnoreCase(autenticado.get().getRole())) {
            return ResponseEntity.status(403).body("Você não tem permissão para excluir usuários.");
        }

        if (!usuarioService.existePorId(id)) {
            return ResponseEntity.notFound().build();
        }

        usuarioService.excluirPorId(id);
        return ResponseEntity.ok("Usuário excluído com sucesso.");
    }

    @GetMapping("/perfil")
    public ResponseEntity<String> perfilAutenticado() {
        String codigoLogin = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok("Usuário logado: " + codigoLogin);
    }

    @RequestMapping("/gerente")
    public class GerenteController {

        @PreAuthorize("hasRole('GERENTE')")
        @GetMapping("/dados")
        public ResponseEntity<String> dadosGerente() {
            return ResponseEntity.ok("Somente gerentes podem ver isso!");
        }
    }

    @GetMapping("/ramais-atuais")
    public ResponseEntity<?> ramaisAtuais(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String codigoLogin = jwtService.extrairCodigoLogin(token);

        Optional<Usuario> usuarioOpt = usuarioService.buscarPorCodigo(codigoLogin);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Usuário não autenticado.");
        }

        Usuario usuario = usuarioOpt.get();

        // Aqui buscamos TODOS os ramais do usuário
        List<Ramal> ramais = ramalService.buscarTodosPorUsuario(usuario);

        // Alteração aqui: retorna 200 com lista vazia
        return ResponseEntity.ok(ramais);
    }











}
