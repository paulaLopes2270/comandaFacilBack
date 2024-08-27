package com.comandaFacil.comandaFacil.controller;

import com.comandaFacil.comandaFacil.config.JwtTokenProvider;
import com.comandaFacil.comandaFacil.response.JwtAuthenticationResponse;
import com.comandaFacil.comandaFacil.service.UsuarioService;
import com.comandaFacil.comandaFacil.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<Usuario> registerUsuario(@RequestBody Usuario usuario) {
        Usuario novoUsuario = usuarioService.registerUsuario(usuario);
        return ResponseEntity.ok(novoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUsuario(@RequestBody Usuario loginRequest) {
        try {
            System.out.println("Tentando autenticar usuário: " + loginRequest.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            System.out.println("Autenticação bem-sucedida para usuário: " + loginRequest.getUsername());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);

            System.out.println("Token JWT gerado: " + jwt);

            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));

        } catch (Exception e) {
            System.err.println("Erro durante a autenticação: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro de autenticação: " + e.getMessage());
        }
    }
}
