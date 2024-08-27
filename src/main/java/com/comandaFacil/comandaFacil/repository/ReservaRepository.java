package com.comandaFacil.comandaFacil.repository;

import com.comandaFacil.comandaFacil.model.Reserva;
import com.comandaFacil.comandaFacil.model.Empresa;
import com.comandaFacil.comandaFacil.model.Mesa;
import com.comandaFacil.comandaFacil.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByEmpresaAndDataHoraBetween(Empresa empresa, LocalDateTime startDateTime, LocalDateTime endDateTime);
    List<Reserva> findByEmpresa(Empresa empresa);
    List<Reserva> findByUsuario(Usuario usuario);

    // Método para buscar reservas por uma mesa específica
    List<Reserva> findByMesa(Mesa mesa);

    // Método existente que busca reservas para uma mesa específica em um horário específico usando uma consulta JPQL
    @Query("SELECT r FROM Reserva r WHERE r.mesa = :mesa AND r.dataHora BETWEEN :inicio AND :fim")
    List<Reserva> findReservasByMesaAndHorario(@Param("mesa") Mesa mesa, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
