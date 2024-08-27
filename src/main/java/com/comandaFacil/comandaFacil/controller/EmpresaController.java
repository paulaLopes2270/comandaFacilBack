package com.comandaFacil.comandaFacil.controller;

import com.comandaFacil.comandaFacil.model.Empresa;
import com.comandaFacil.comandaFacil.model.Usuario;
import com.comandaFacil.comandaFacil.service.EmpresaService;
import com.comandaFacil.comandaFacil.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Empresa> getAllEmpresas() {
        List<Empresa> empresas = empresaService.findAllEmpresas();
        for (Empresa empresa : empresas) {
            if (empresa.getImagem() != null) {
                empresa.setImagemBase64(Base64.getEncoder().encodeToString(empresa.getImagem()));
            }
        }
        return empresas;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> getEmpresaById(@PathVariable Long id) {
        Empresa empresa = empresaService.findEmpresaById(id)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada: " + id));
        if (empresa.getImagem() != null) {
            empresa.setImagemBase64(Base64.getEncoder().encodeToString(empresa.getImagem()));
        }
        return ResponseEntity.ok().body(empresa);
    }

    @PostMapping
    public ResponseEntity<Empresa> createEmpresa(@RequestBody Empresa empresa, Authentication authentication) {
        try {
            if (empresa.getImagemBase64() != null && !empresa.getImagemBase64().isEmpty()) {
                empresa.setImagem(Base64.getDecoder().decode(empresa.getImagemBase64()));
            }

            String username = authentication.getName();
            Optional<Usuario> usuarioOptional = usuarioService.findByUsername(username);

            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();
                empresa.setUsuario(usuario);
                Empresa novaEmpresa = empresaService.saveEmpresa(empresa);
                return ResponseEntity.ok(novaEmpresa);
            } else {
                return ResponseEntity.status(404).body(null);
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empresa> updateEmpresa(@PathVariable Long id, @RequestBody Empresa empresaDetails) {
        try {
            if (empresaDetails.getImagemBase64() != null && !empresaDetails.getImagemBase64().isEmpty()) {
                empresaDetails.setImagem(Base64.getDecoder().decode(empresaDetails.getImagemBase64()));
            }
            Empresa updatedEmpresa = empresaService.updateEmpresa(id, empresaDetails);
            return ResponseEntity.ok(updatedEmpresa);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpresa(@PathVariable Long id) {
        try {
            empresaService.deleteEmpresa(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/minhas")
    public List<Empresa> getEmpresasDoUsuario(Authentication authentication) {
        String username = authentication.getName();
        Optional<Usuario> usuarioOptional = usuarioService.findByUsername(username);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            List<Empresa> empresas = empresaService.findAllByUsuario(usuario);
            for (Empresa empresa : empresas) {
                if (empresa.getImagem() != null) {
                    empresa.setImagemBase64(Base64.getEncoder().encodeToString(empresa.getImagem()));
                }
            }
            return empresas;
        } else {
            throw new RuntimeException("Usuário não encontrado");
        }
    }


}
