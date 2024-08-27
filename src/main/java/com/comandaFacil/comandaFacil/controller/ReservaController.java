package com.comandaFacil.comandaFacil.controller;

import com.comandaFacil.comandaFacil.model.Reserva;
import com.comandaFacil.comandaFacil.model.Empresa;
import com.comandaFacil.comandaFacil.model.Mesa;
import com.comandaFacil.comandaFacil.model.Usuario;
import com.comandaFacil.comandaFacil.service.EmpresaService;
import com.comandaFacil.comandaFacil.service.MesaService;
import com.comandaFacil.comandaFacil.service.ReservaService;
import com.comandaFacil.comandaFacil.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;


@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private MesaService mesaService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/empresa/{empresaId}/mesa/{mesaId}")
    public ResponseEntity<?> createReserva(
            @PathVariable Long empresaId,
            @PathVariable Long mesaId,
            @RequestBody Reserva reserva,
            Authentication authentication) {

        Empresa empresa = empresaService.findEmpresaById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada: " + empresaId));

        Mesa mesa = mesaService.findMesaById(mesaId)
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada: " + mesaId));

        Usuario usuario = usuarioService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + authentication.getName()));

        LocalDate today = LocalDate.now();
        LocalDate dataOcupacao = mesa.getDataOcupacao() != null ? mesa.getDataOcupacao().toLocalDate() : null;
        LocalDate dataReserva = reserva.getDataHora().toLocalDate();

        // Verifica se a mesa está ocupada manualmente para o dia atual
        if (mesa.isOcupadaManualmente() && dataOcupacao != null && dataOcupacao.isEqual(today) && dataReserva.isEqual(today)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A mesa está marcada como ocupada para o resto do dia.");
        }


        // Verifica se já existe uma reserva formal para a mesa no horário solicitado
        LocalDateTime reservaInicio = reserva.getDataHora();
        LocalDateTime reservaFim = reservaInicio.plusHours(2);  // Exemplo de tempo de reserva

        List<Reserva> reservasExistentes = reservaService.findReservasByMesaAndHorario(mesa, reservaInicio, reservaFim);
        if (!reservasExistentes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Já existe uma reserva para esta mesa no horário solicitado.");
        }

        reserva.setEmpresa(empresa);
        reserva.setMesa(mesa);
        reserva.setUsuario(usuario); // Vincular a reserva ao usuário logado

        Reserva novaReserva = reservaService.saveReserva(reserva);
        return ResponseEntity.ok(novaReserva);
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<Reserva>> getReservasByEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(value = "date", required = false) String date) {

        Empresa empresa = empresaService.findEmpresaById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada: " + empresaId));

        List<Reserva> reservas;
        if (date != null && !date.isEmpty()) {
            LocalDateTime startDateTime = LocalDateTime.parse(date + "T00:00:00");
            LocalDateTime endDateTime = LocalDateTime.parse(date + "T23:59:59");
            reservas = reservaService.findByEmpresaAndDataHoraBetween(empresa, startDateTime, endDateTime);
        } else {
            reservas = reservaService.findByEmpresa(empresa);
        }
        return ResponseEntity.ok(reservas);
    }

    // Novo endpoint: Listar reservas do usuário logado
    @GetMapping("/minhas")
    public ResponseEntity<List<Reserva>> getReservasDoUsuario(Authentication authentication) {
        Usuario usuario = usuarioService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + authentication.getName()));

        List<Reserva> reservas = reservaService.findByUsuario(usuario);
        return ResponseEntity.ok(reservas);
    }

    // Novo endpoint: Cancelar reserva com verificação de 24 horas de antecedência
    @DeleteMapping("/cancelar/{reservaId}")
    public ResponseEntity<String> cancelarReserva(@PathVariable Long reservaId, Authentication authentication) {
        Reserva reserva = reservaService.findReservaById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada: " + reservaId));

        // Verifica se o usuário logado é o dono da reserva
        Usuario usuario = usuarioService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + authentication.getName()));

        if (!reserva.getUsuario().equals(usuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não pode cancelar esta reserva.");
        }

        // Verifica se a reserva é cancelável (com mais de 24 horas de antecedência)
        LocalDateTime agora = LocalDateTime.now();
        if (reserva.getDataHora().isBefore(agora.plusHours(24))) {
            return ResponseEntity.badRequest().body("Você só pode cancelar a reserva com mais de 24 horas de antecedência.");
        }

        reservaService.deleteReserva(reservaId);
        return ResponseEntity.ok("Reserva cancelada com sucesso.");
    }

    // Novo endpoint: Marcar mesa como ocupada ou livre
    @PutMapping("/mesa/{mesaId}/ocupar")
    public ResponseEntity<String> marcarOcupacao(@PathVariable Long mesaId, @RequestParam boolean ocupar) {
        Mesa mesa = mesaService.findMesaById(mesaId)
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada: " + mesaId));

        mesa.setOcupadaManualmente(ocupar);
        mesa.setDataOcupacao(ocupar ? LocalDateTime.now() : null);
        mesaService.saveMesa(mesa);

        return ResponseEntity.ok(ocupar ? "Mesa marcada como ocupada." : "Mesa liberada.");
    }

    // Novo endpoint: Listar horários indisponíveis de uma mesa
    @GetMapping("/empresa/{empresaId}/mesa/{mesaId}/unavailable-slots")
    public ResponseEntity<List<LocalDateTime>> getUnavailableSlots(
            @PathVariable Long empresaId,
            @PathVariable Long mesaId) {

        Empresa empresa = empresaService.findEmpresaById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada: " + empresaId));

        Mesa mesa = mesaService.findMesaById(mesaId)
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada: " + mesaId));

        List<LocalDateTime> unavailableSlots = reservaService.getUnavailableSlots(mesa);
        return ResponseEntity.ok(unavailableSlots);
    }

}
