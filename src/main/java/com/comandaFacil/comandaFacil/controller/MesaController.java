package com.comandaFacil.comandaFacil.controller;

import com.comandaFacil.comandaFacil.model.Mesa;
import com.comandaFacil.comandaFacil.model.Empresa;
import com.comandaFacil.comandaFacil.service.EmpresaService;
import com.comandaFacil.comandaFacil.service.MesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mesas")
public class MesaController {

    @Autowired
    private MesaService mesaService;

    @Autowired
    private EmpresaService empresaService;

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<Mesa>> getMesasByEmpresa(@PathVariable Long empresaId) {
        Empresa empresa = empresaService.findEmpresaById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada: " + empresaId));
        List<Mesa> mesas = mesaService.findByEmpresa(empresa);
        return ResponseEntity.ok(mesas);
    }

    @PostMapping("/empresa/{empresaId}")
    public ResponseEntity<Mesa> createMesaForEmpresa(
            @PathVariable Long empresaId,
            @RequestBody Mesa mesa) {
        Empresa empresa = empresaService.findEmpresaById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada: " + empresaId));

        mesa.setEmpresa(empresa);
        Mesa novaMesa = mesaService.saveMesa(mesa);
        return ResponseEntity.ok(novaMesa);
    }

    @DeleteMapping("/{mesaId}")
    public ResponseEntity<Void> deleteMesa(@PathVariable Long mesaId) {
        mesaService.deleteMesa(mesaId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{mesaId}")
    public ResponseEntity<Mesa> updateMesa(
            @PathVariable Long mesaId,
            @RequestBody Mesa mesaDetails) {

        Mesa updatedMesa = mesaService.updateMesa(mesaId, mesaDetails);
        return ResponseEntity.ok(updatedMesa);
    }

    // Novo endpoint para marcar a mesa como ocupada ou liberá-la manualmente
    @PutMapping("/{mesaId}/ocupar")
    public ResponseEntity<Mesa> toggleOcupacaoMesa(@PathVariable Long mesaId) {
        Mesa mesa = mesaService.toggleOcupacaoMesa(mesaId);
        return ResponseEntity.ok(mesa);
    }
}
