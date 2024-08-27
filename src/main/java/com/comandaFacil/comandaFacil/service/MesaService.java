package com.comandaFacil.comandaFacil.service;

import com.comandaFacil.comandaFacil.model.Mesa;
import com.comandaFacil.comandaFacil.model.Empresa;
import com.comandaFacil.comandaFacil.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    public List<Mesa> findByEmpresa(Empresa empresa) {
        return mesaRepository.findByEmpresa(empresa);
    }

    public Mesa saveMesa(Mesa mesa) {
        return mesaRepository.save(mesa);
    }

    public void deleteMesa(Long mesaId) {
        mesaRepository.deleteById(mesaId);
    }

    public Mesa updateMesa(Long mesaId, Mesa mesaDetails) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada: " + mesaId));

        mesa.setNumero(mesaDetails.getNumero());
        mesa.setCapacidade(mesaDetails.getCapacidade());
        mesa.setArea(mesaDetails.getArea());

        return mesaRepository.save(mesa);
    }

    public Optional<Mesa> findMesaById(Long mesaId) {
        return mesaRepository.findById(mesaId);
    }

    // Método para alternar a ocupação manual da mesa
    public Mesa toggleOcupacaoMesa(Long mesaId) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada: " + mesaId));
        mesa.setOcupadaManualmente(!mesa.isOcupadaManualmente());
        return mesaRepository.save(mesa);
    }
}
