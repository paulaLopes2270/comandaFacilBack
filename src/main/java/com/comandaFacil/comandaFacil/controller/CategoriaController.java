package com.comandaFacil.comandaFacil.controller;

import com.comandaFacil.comandaFacil.model.Categoria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @GetMapping
    public Categoria[] getCategorias() {
        return Categoria.values();
    }
}
