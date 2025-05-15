package org.example.voxlink_backend.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.example.voxlink_backend.Model.Usuario;
import org.example.voxlink_backend.Repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final UsuarioRepository usuarioRepository;

    // Chave secreta - considere mover para application.properties
    private static final String SECRET_KEY_STRING = "segredo_do_seu_sistema_super_secreto_que_deve_ter_mais_de_32_bytes";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRATION_TIME = 3600000; // 1 hora em ms

    public String gerarToken(String codigoLogin) {
        // Buscar o usuário no banco de dados
        Usuario usuario = usuarioRepository.findByCodigoLogin(codigoLogin)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Gerar o token com informações do usuário, incluindo o role, nome, email e cargo
        return Jwts.builder()
                .subject(usuario.getCodigoLogin()) // Código de login como subject
                .claim("role", usuario.getRole())  // Role do usuário
                .claim("nome", usuario.getNome())  // Nome do usuário
                .claim("email", usuario.getEmail()) // Email do usuário
                .claim("cargo", usuario.getCargo()) // Cargo do usuário
                .issuedAt(new Date()) // Data de emissão
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expiração
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // Assinatura com chave secreta
                .compact(); // Compactação do token
    }

    // Função para validar o token
    public boolean validarToken(String token) {
        try {
            getClaims(token); // Se não lançar exceção, token é válido
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Token inválido: " + e.getMessage());
            return false;
        }
    }

    // Função para extrair o código de login do token
    public String extrairCodigoLogin(String token) {
        return getClaims(token).getSubject();
    }

    // Função para extrair todos os claims do token
    public Claims extrairTodosClaims(String token) {
        return getClaims(token);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()          // <-- Usando a API nova (JJWT 0.12.x)
                .verifyWith(SECRET_KEY)   // <-- Substitui setSigningKey()
                .build()
                .parseSignedClaims(token) // <-- Substitui parseClaimsJws()
                .getPayload();            // <-- Substitui getBody()
    }

    public boolean ehGerente(String codigoLogin) {
        return usuarioRepository.findByCodigoLogin(codigoLogin)
                .map(usuario -> "GERENTE".equalsIgnoreCase(usuario.getRole()))
                .orElse(false);
    }
}
