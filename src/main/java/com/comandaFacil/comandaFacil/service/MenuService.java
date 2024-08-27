package com.comandaFacil.comandaFacil.service;

import com.comandaFacil.comandaFacil.model.Menu;
import com.comandaFacil.comandaFacil.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    public List<Menu> findAllMenus() {
        return menuRepository.findAll();
    }

    public Optional<Menu> findMenuById(Long id) {
        return menuRepository.findById(id);
    }

    public Menu saveMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    public Menu updateMenu(Long id, Menu menuDetails) {
        Menu menu = menuRepository.findById(id).orElseThrow(() -> new RuntimeException("Menu não encontrado: " + id));
        menu.setNome(menuDetails.getNome());
        menu.setDescricao(menuDetails.getDescricao());
        menu.setPreco(menuDetails.getPreco());
        menu.setImagem(menuDetails.getImagem());
        return menuRepository.save(menu);
    }

    public void deleteMenu(Long id) {
        Menu menu = menuRepository.findById(id).orElseThrow(() -> new RuntimeException("Menu não encontrado: " + id));
        menuRepository.delete(menu);
    }

    public List<Menu> findByEmpresaId(Long empresaId) {
        return menuRepository.findByEmpresaId(empresaId);
    }

    public Optional<Menu> findById(Long id) {
        return menuRepository.findById(id);
    }
}
