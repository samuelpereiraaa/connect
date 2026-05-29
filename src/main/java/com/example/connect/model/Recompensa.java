package com.example.connect.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "recompensa")
public class Recompensa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recompensa")
    private Integer idRecompensa;

    @Column(name = "nome", nullable = false, length = 150, unique = true)
    private String nome;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "pontos_necessarios", nullable = false)
    private Integer pontosNecessarios;

    @Column(name = "tipo", length = 100)
    private String tipo;

    @Column(name = "estoque")
    private Integer estoque;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private StatusRecompensa status = StatusRecompensa.ATIVO;

    // ── Getters ──────────────────────────────────────────────────────────────

    public Integer getIdRecompensa() { return idRecompensa; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public Integer getPontosNecessarios() { return pontosNecessarios; }
    public String getTipo() { return tipo; }
    public Integer getEstoque() { return estoque; }
    public StatusRecompensa getStatus() { return status; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setIdRecompensa(Integer idRecompensa) { this.idRecompensa = idRecompensa; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setPontosNecessarios(Integer pontosNecessarios) { this.pontosNecessarios = pontosNecessarios; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }
    public void setStatus(StatusRecompensa status) { this.status = status; }
}