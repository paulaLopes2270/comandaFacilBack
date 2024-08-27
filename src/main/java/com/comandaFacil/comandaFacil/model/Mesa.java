package com.comandaFacil.comandaFacil.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mesas")
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero", nullable = false)
    private int numero;

    @Column(name = "capacidade", nullable = false)
    private int capacidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "area", nullable = false)
    private Area area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    // Novos campos
    @Column(name = "ocupada_manual", nullable = false)
    private boolean ocupadaManualmente = false;

    @Column(name = "data_ocupacao")
    private LocalDateTime dataOcupacao;
}

enum Area {
    INTERNA,
    EXTERNA
}
