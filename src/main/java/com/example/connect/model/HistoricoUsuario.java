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

@Entity
@Table(name = "historico_usuario")
public class HistoricoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historico")
    private Integer idHistorico;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "campo_alterado", nullable = false, length = 100)
    private String campoAlterado;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(name = "valor_novo", columnDefinition = "TEXT")
    private String valorNovo;

    @ManyToOne
    @JoinColumn(name = "id_responsavel", nullable = false)
    private Usuario responsavel;

    @Column(name = "data_acao", nullable = false)
    private LocalDateTime dataAcao;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    public Integer getIdHistorico() { return idHistorico; }
    public void setIdHistorico(Integer idHistorico) { this.idHistorico = idHistorico; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getCampoAlterado() { return campoAlterado; }
    public void setCampoAlterado(String campoAlterado) { this.campoAlterado = campoAlterado; }

    public String getValorAnterior() { return valorAnterior; }
    public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }

    public String getValorNovo() { return valorNovo; }
    public void setValorNovo(String valorNovo) { this.valorNovo = valorNovo; }

    public Usuario getResponsavel() { return responsavel; }
    public void setResponsavel(Usuario responsavel) { this.responsavel = responsavel; }

    public LocalDateTime getDataAcao() { return dataAcao; }
    public void setDataAcao(LocalDateTime dataAcao) { this.dataAcao = dataAcao; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
