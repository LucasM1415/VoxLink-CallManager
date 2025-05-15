package org.example.voxlink_backend.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.voxlink_backend.Service.Email.EmailService;
import org.example.voxlink_backend.Service.JwtService;
import org.example.voxlink_backend.Service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final EmailService emailService;



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.codigoLogin(),
                            request.senha()
                    )
            );

            String token = jwtService.gerarToken(request.codigoLogin());

            var usuario = usuarioService.buscarPorCodigo(request.codigoLogin())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado após autenticação"));

            System.out.println("Login bem-sucedido:");
            System.out.println("Código: " + usuario.getCodigoLogin());
            System.out.println("Role: " + usuario.getRole());
            System.out.println("Token: " + token);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "codigoLogin", usuario.getCodigoLogin()
            ));

        } catch (AuthenticationException e) {
            System.err.println("Falha na autenticação: " + e.getMessage());
            return ResponseEntity.status(401).body(Map.of(
                    "mensagem", "Credenciais inválidas",
                    "erro", e.getMessage()
            ));
        }
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<?> recuperarSenha(@RequestBody RecuperarSenhaRequest request) {
        var usuarioOpt = usuarioService.buscarPorEmail(request.email());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                    "mensagem", "Usuário com esse email não foi encontrado."
            ));
        }

        var usuario = usuarioOpt.get();
        String novaSenha = usuarioService.gerarSenhaProvisoria();
        usuarioService.atualizarSenha(usuario.getId(), novaSenha);

        // ✅ Enviar email
        String subject = "Recuperação de Senha - VoxLink";
        String body = String.format(
                "Olá %s,\n\nSeu código de login é: %s\nSua nova senha provisória é: %s\n\nPor favor, altere sua senha após o login.",
                usuario.getNome(),
                usuario.getCodigoLogin(),
                novaSenha
        );

        emailService.sendEmail(
                usuario.getEmail(),  // para quem enviar
                subject,
                body
        );

        return ResponseEntity.ok(Map.of(
                "mensagem", "Email enviado com sucesso para: " + usuario.getEmail()
        ));
    }


    public record LoginRequest(String codigoLogin, String senha) {}
    public record RecuperarSenhaRequest(String email) {}
}
