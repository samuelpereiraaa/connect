package com.example.connect.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * RF07 – Gerenciamento de Pontuação.
 *
 * Regra 1: a pontuação é calculada automaticamente pela trigger
 *          fn_atualizar_pontuacao() no banco ao validar um registro.
 * Regra 2: atualizada conforme registros validados; o Java apenas lê/exibe.
 *
 * Mapeamento: tabela pontuacao — um registro por usuário (UNIQUE id_usuario).
 */
@Entity
@Table(name = "pontuacao")
public class Pontuacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pontuacao")
    private Integer idPontuacao;

    /** RF07 – FK para usuario; relação 1:1 (UNIQUE no banco). */
    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    /** RF07 – Total de pontos acumulados; nunca negativo (CHECK no banco). */
    @Column(name = "pontos_total", nullable = false)
    private Integer pontosTotal;

    /** RF07 – Nível calculado pela trigger conforme tabela nivel_pontuacao. */
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "nivel_usuario", nullable = false)
    private NivelUsuario nivel;

    /** RF07 – Atualizado pela trigger a cada crédito ou débito de pontos. */
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    // -------------------------------------------------------------------------
    // Getters e Setters
    // -------------------------------------------------------------------------

    public Integer getIdPontuacao() { return idPontuacao; }
    public void setIdPontuacao(Integer idPontuacao) { this.idPontuacao = idPontuacao; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Integer getPontosTotal() { return pontosTotal; }
    public void setPontosTotal(Integer pontosTotal) { this.pontosTotal = pontosTotal; }

    public NivelUsuario getNivel() { return nivel; }
    public void setNivel(NivelUsuario nivel) { this.nivel = nivel; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}