package com.example.connect.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * RF11 – Entidade de Relatório.
 *
 * Representa um relatório gerado por período pelo administrador,
 * consolidando o total de material reciclado no intervalo informado.
 */
@Entity
@Table(name = "relatorio")
public class Relatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_relatorio")
    private Integer idRelatorio;

    /** Tipo do relatório: ex. "RECICLAGEM", "PONTUACAO", "USUARIOS" */
    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    /** Soma total em kg (ou unidades) de material reciclado no período */
    @Column(name = "total_reciclado", nullable = false)
    private Double totalReciclado;

    /** Administrador que gerou o relatório */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin", nullable = false)
    private Administrador administrador;

    // ── Construtores ──────────────────────────────────────────────────────────

    public Relatorio() {}

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Integer getIdRelatorio() { return idRelatorio; }
    public void setIdRelatorio(Integer idRelatorio) { this.idRelatorio = idRelatorio; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public Double getTotalReciclado() { return totalReciclado; }
    public void setTotalReciclado(Double totalReciclado) { this.totalReciclado = totalReciclado; }

    public Administrador getAdministrador() { return administrador; }
    public void setAdministrador(Administrador administrador) { this.administrador = administrador; }
}
