package com.example.connect.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * RF07 – Histórico de lançamentos de pontuação.
 *
 * Cada linha representa um CREDITO (reciclagem validada)
 * ou DEBITO (resgate aprovado) gerado pela trigger do banco.
 * O Java apenas lê — nunca insere diretamente nesta tabela.
 *
 * Mapeamento: tabela historico_pontuacao.
 */
@Entity
@Table(name = "historico_pontuacao")
public class HistoricoPontuacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historico")
    private Integer idHistorico;

    /** Usuário dono do lançamento. */
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    /**
     * 'CREDITO' ou 'DEBITO' — restrição CHECK no banco.
     * Guardado como String para não precisar de um enum separado.
     */
    @Column(name = "tipo_operacao", nullable = false, length = 10)
    private String tipoOperacao;

    /** Quantidade de pontos movimentados (sempre positivo). */
    @Column(nullable = false)
    private Integer pontos;

    /** Saldo antes da operação. */
    @Column(name = "saldo_anterior", nullable = false)
    private Integer saldoAnterior;

    /** Saldo depois da operação. */
    @Column(name = "saldo_posterior", nullable = false)
    private Integer saldoPosterior;

    /**
     * ID de origem: id_registro (crédito) ou id_resgate (débito).
     * Nullable — pode ser nulo em lançamentos manuais.
     */
    @Column(name = "id_origem")
    private Integer idOrigem;

    @Column(length = 300)
    private String descricao;

    @Column(name = "data_operacao", nullable = false)
    private LocalDateTime dataOperacao;

    // -------------------------------------------------------------------------
    // Getters e Setters
    // -------------------------------------------------------------------------

    public Integer getIdHistorico() { return idHistorico; }
    public void setIdHistorico(Integer idHistorico) { this.idHistorico = idHistorico; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getTipoOperacao() { return tipoOperacao; }
    public void setTipoOperacao(String tipoOperacao) { this.tipoOperacao = tipoOperacao; }

    public Integer getPontos() { return pontos; }
    public void setPontos(Integer pontos) { this.pontos = pontos; }

    public Integer getSaldoAnterior() { return saldoAnterior; }
    public void setSaldoAnterior(Integer saldoAnterior) { this.saldoAnterior = saldoAnterior; }

    public Integer getSaldoPosterior() { return saldoPosterior; }
    public void setSaldoPosterior(Integer saldoPosterior) { this.saldoPosterior = saldoPosterior; }

    public Integer getIdOrigem() { return idOrigem; }
    public void setIdOrigem(Integer idOrigem) { this.idOrigem = idOrigem; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getDataOperacao() { return dataOperacao; }
    public void setDataOperacao(LocalDateTime dataOperacao) { this.dataOperacao = dataOperacao; }
}
