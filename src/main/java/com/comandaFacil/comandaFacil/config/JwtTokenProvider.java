package com.comandaFacil.comandaFacil.config;

import com.comandaFacil.comandaFacil.model.Usuario;
import com.comandaFacil.comandaFacil.service.UsuarioService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Autowired
    private UsuarioService usuarioService;

    // Método para gerar o token JWT com o campo "role"
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // Busque o usuário pelo username
        Usuario usuario = usuarioService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000); // 1 hora de validade

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", usuario.getRole()) // Inclua o papel do usuário no token
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Método para obter a autenticação a partir do token
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Extraia o papel do usuário do token
        String role = claims.get("role", String.class);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                claims.getSubject(), "", new ArrayList<>());

        // Podemos usar o papel extraído conforme necessário aqui
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    // Método para validar o token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
