package org.example.voxlink_backend.Controllers;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.example.voxlink_backend.Utils.JwtValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TestarTokenController {

    private final JwtValidator jwtValidator;

    @PostMapping("/testar-token")
    public ResponseEntity<String> testarToken(
            @RequestHeader("Authorization") String authHeader) {

        // Verifica se o cabeçalho existe
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401)
                    .body("❌ Formato de token inválido. Use 'Bearer <token>'");
        }

        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            boolean valido = jwtValidator.validarToken(token);

            if (valido) {
                return ResponseEntity.ok("✅ Token válido!");
            } else {
                return ResponseEntity.status(401)
                        .body("❌ Token inválido ou expirado");
            }

        } catch (JwtException e) {
            return ResponseEntity.status(401)
                    .body("❌ Erro ao processar token: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("⚠️ Erro interno ao validar token");
        }
    }
}