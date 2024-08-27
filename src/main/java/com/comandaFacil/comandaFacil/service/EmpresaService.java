package com.comandaFacil.comandaFacil.service;

import com.comandaFacil.comandaFacil.model.Empresa;
import com.comandaFacil.comandaFacil.model.Usuario;
import com.comandaFacil.comandaFacil.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    public List<Empresa> findAllEmpresas() {
        return empresaRepository.findAll();
    }

    public Optional<Empresa> findEmpresaById(Long id) {
        return empresaRepository.findById(id);
    }

    public Empresa saveEmpresa(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    public Empresa updateEmpresa(Long id, Empresa empresaDetails) {
        Empresa empresa = empresaRepository.findById(id).orElseThrow(() -> new RuntimeException("Empresa não encontrada: " + id));
        empresa.setNome(empresaDetails.getNome());
        empresa.setEndereco(empresaDetails.getEndereco());
        empresa.setTelefone(empresaDetails.getTelefone());
        empresa.setEmail(empresaDetails.getEmail());
        empresa.setImagem(empresaDetails.getImagem());
        return empresaRepository.save(empresa);
    }

    public void deleteEmpresa(Long id) {
        Empresa empresa = empresaRepository.findById(id).orElseThrow(() -> new RuntimeException("Empresa não encontrada: " + id));
        empresaRepository.delete(empresa);
    }

    public List<Empresa> findAllByUsuario(Usuario usuario) {
        return empresaRepository.findAllByUsuario(usuario);
    }
}
