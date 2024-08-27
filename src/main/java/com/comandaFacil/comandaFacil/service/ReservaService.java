package com.comandaFacil.comandaFacil.service;

import com.comandaFacil.comandaFacil.model.Mesa;
import com.comandaFacil.comandaFacil.model.Empresa;
import com.comandaFacil.comandaFacil.model.Reserva;
import com.comandaFacil.comandaFacil.model.Usuario;
import com.comandaFacil.comandaFacil.repository.ReservaRepository;
import com.comandaFacil.comandaFacil.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private MesaRepository mesaRepository;

    public List<Reserva> findByEmpresa(Empresa empresa) {
        return reservaRepository.findByEmpresa(empresa);
    }

    public List<Reserva> findByEmpresaAndDataHoraBetween(Empresa empresa, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return reservaRepository.findByEmpresaAndDataHoraBetween(empresa, startDateTime, endDateTime);
    }

    public List<Reserva> findByUsuario(Usuario usuario) {
        return reservaRepository.findByUsuario(usuario);
    }

    public Reserva saveReserva(Reserva reserva) {
        Mesa mesa = reserva.getMesa();

        System.out.println("Tentativa de reserva para a mesa: " + mesa.getNumero());
        System.out.println("Data da reserva: " + reserva.getDataHora());
        System.out.println("Mesa está ocupada manualmente: " + mesa.isOcupadaManualmente());

        if (mesa.isOcupadaManualmente() && mesa.getDataOcupacao() != null) {
            LocalDate dataOcupacao = mesa.getDataOcupacao().toLocalDate();
            LocalDate dataReserva = reserva.getDataHora().toLocalDate();

            if (dataOcupacao.isEqual(dataReserva)) {
                System.out.println("Reserva bloqueada: Mesa ocupada manualmente para a data da reserva.");
                throw new RuntimeException("A mesa está marcada como ocupada para o resto do dia.");
            }
        }

        List<Reserva> reservasExistentes = findReservasByMesaAndHorario(
                mesa,
                reserva.getDataHora().withHour(0).withMinute(0).withSecond(0),
                reserva.getDataHora().withHour(23).withMinute(59).withSecond(59)
        );

        if (!reservasExistentes.isEmpty()) {
            System.out.println("Reserva bloqueada: Já existe uma reserva para este horário.");
            throw new RuntimeException("Já existe uma reserva para esta mesa no horário solicitado.");
        }

        System.out.println("Reserva permitida e sendo salva.");
        return reservaRepository.save(reserva);
    }

    public void deleteReserva(Long reservaId) {
        reservaRepository.deleteById(reservaId);
    }

    public Optional<Reserva> findReservaById(Long reservaId) {
        return reservaRepository.findById(reservaId);
    }

    public void liberarMesasOcupadas() {
        List<Mesa> mesas = mesaRepository.findAll();
        LocalDateTime agora = LocalDateTime.now();
        for (Mesa mesa : mesas) {
            if (mesa.isOcupadaManualmente() && mesa.getDataOcupacao().toLocalDate().isBefore(agora.toLocalDate())) {
                mesa.setOcupadaManualmente(false);
                mesa.setDataOcupacao(null);
                mesaRepository.save(mesa);
            }
        }
    }

    public List<Reserva> findReservasByMesaAndHorario(Mesa mesa, LocalDateTime inicio, LocalDateTime fim) {
        return reservaRepository.findReservasByMesaAndHorario(mesa, inicio, fim);
    }

    public List<LocalDateTime> getUnavailableSlots(Mesa mesa) {
        List<Reserva> reservas = reservaRepository.findByMesa(mesa);
        List<LocalDateTime> unavailableSlots = new ArrayList<>();
        for (Reserva reserva : reservas) {
            unavailableSlots.add(reserva.getDataHora());
            unavailableSlots.add(reserva.getDataHora().plusHours(2));
        }
        return unavailableSlots;
    }
}
