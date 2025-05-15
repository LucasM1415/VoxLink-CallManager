package org.example.voxlink_backend.Controllers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.example.voxlink_backend.Service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class TokenDebugController {

    private final JwtService jwtService;

    @PostMapping("/analisar-token")
    public ResponseEntity<String> analisarToken(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest()
                    .body("❌ Cabeçalho 'Authorization' inválido. Deve começar com 'Bearer '");
        }

        String token = authHeader.substring(7);

        try {
            boolean valido = jwtService.validarToken(token);
            String codigoLogin = jwtService.extrairCodigoLogin(token);
            Claims claims = jwtService.extrairTodosClaims(token);

            String resposta = "✅ Token válido!\n"
                    + "Código Login: " + codigoLogin + "\n"
                    + "Emitido em: " + claims.getIssuedAt() + "\n"
                    + "Expira em: " + claims.getExpiration() + "\n"
                    + "Claims completos: " + claims.toString();

            return ResponseEntity.ok(resposta);

        } catch (JwtException e) {
            return ResponseEntity.status(401)
                    .body("❌ Token inválido ou corrompido: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("⚠️ Erro interno: " + e.getMessage());
        }
    }
}
