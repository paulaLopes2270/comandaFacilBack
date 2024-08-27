package com.comandaFacil.comandaFacil.controller;

import com.comandaFacil.comandaFacil.model.Empresa;
import com.comandaFacil.comandaFacil.model.Menu;
import com.comandaFacil.comandaFacil.service.EmpresaService;
import com.comandaFacil.comandaFacil.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/menus")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private EmpresaService empresaService;

    @PostMapping("/empresa/{empresaId}")
    public ResponseEntity<Menu> createMenuForEmpresa(
            @PathVariable Long empresaId,
            @RequestBody Menu menu) {
        Empresa empresa = empresaService.findEmpresaById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada: " + empresaId));

        if (menu.getImagemBase64() != null && !menu.getImagemBase64().isEmpty()) {
            try {
                menu.setImagem(Base64.getDecoder().decode(menu.getImagemBase64()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null);
            }
        }
        menu.setEmpresa(empresa);
        Menu novoMenu = menuService.saveMenu(menu);
        return ResponseEntity.ok(novoMenu);
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<Menu>> getMenusByEmpresa(@PathVariable Long empresaId) {
        System.out.println("GET request to /menus/empresa/" + empresaId);
        List<Menu> menus = menuService.findByEmpresaId(empresaId);
        if (menus.isEmpty()) {
            System.out.println("No menus found for empresaId: " + empresaId);
            return ResponseEntity.noContent().build();
        }

        // Convertendo as imagens para base64 antes de retornar ao frontend
        for (Menu menu : menus) {
            if (menu.getImagem() != null) {
                menu.setImagemBase64(Base64.getEncoder().encodeToString(menu.getImagem()));
            }
        }

        System.out.println("Menus found: " + menus.size());
        return ResponseEntity.ok(menus);
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId) {
        // Busca o menu pelo ID
        Menu menu = menuService.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu não encontrado: " + menuId));

        // Deleta o menu pelo ID
        menuService.deleteMenu(menuId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{menuId}")
    public ResponseEntity<Menu> updateMenu(
            @PathVariable Long menuId,
            @RequestBody Menu menuDetails) {

        Menu menu = menuService.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu não encontrado: " + menuId));

        // Atualiza os detalhes do menu
        menu.setNome(menuDetails.getNome());
        menu.setDescricao(menuDetails.getDescricao());
        menu.setPreco(menuDetails.getPreco());
        menu.setCategoria(menuDetails.getCategoria());

        if (menuDetails.getImagemBase64() != null && !menuDetails.getImagemBase64().isEmpty()) {
            try {
                menu.setImagem(Base64.getDecoder().decode(menuDetails.getImagemBase64()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null);
            }
        }

        Menu updatedMenu = menuService.saveMenu(menu);
        return ResponseEntity.ok(updatedMenu);
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<Menu> getMenuById(@PathVariable Long menuId) {
        Menu menu = menuService.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + menuId));

        // Convertendo a imagem para Base64 antes de enviar ao frontend
        if (menu.getImagem() != null) {
            menu.setImagemBase64(Base64.getEncoder().encodeToString(menu.getImagem()));
        }

        return ResponseEntity.ok(menu);
    }
}
