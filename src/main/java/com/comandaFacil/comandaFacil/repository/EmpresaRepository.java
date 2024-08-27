package com.comandaFacil.comandaFacil.repository;

import com.comandaFacil.comandaFacil.model.Empresa;
import com.comandaFacil.comandaFacil.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    List<Empresa> findAllByUsuario(Usuario usuario);
}
