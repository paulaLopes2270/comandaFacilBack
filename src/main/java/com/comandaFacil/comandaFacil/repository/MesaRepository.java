package com.comandaFacil.comandaFacil.repository;

import com.comandaFacil.comandaFacil.model.Mesa;
import com.comandaFacil.comandaFacil.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
    List<Mesa> findByEmpresa(Empresa empresa);
}
