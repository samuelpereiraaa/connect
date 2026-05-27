package com.example.connect.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;

@Entity
@Table(name = "registro_reciclagem")
public class RegistroReciclagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_ponto", nullable = false)
    private PontoColeta ponto;

    @ManyToOne
    @JoinColumn(name = "id_catador", nullable = false)
    private Usuario catador;

    @ManyToOne
    @JoinColumn(name = "id_tipo_material", nullable = false)
    private TipoMaterial tipoMaterial;

    @Column(nullable = false)
    private Double quantidade;

    @Column(name = "data_registro")
    private LocalDateTime dataRegistro;

    @Column(unique = true, nullable = false)
    private String qrCode;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "status_reciclagem")
    private StatusReciclagem status;

    @Column(name = "data_validacao")
    private LocalDateTime dataValidacao;

    @ManyToOne
    @JoinColumn(name = "id_catador_validador")
    private Usuario catadorValidador;

    @Column(name = "observacao_validacao", length = 500)
    private String observacaoValidacao;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public PontoColeta getPonto() { return ponto; }
    public void setPonto(PontoColeta ponto) { this.ponto = ponto; }

    public Usuario getCatador() { return catador; }
    public void setCatador(Usuario catador) { this.catador = catador; }

    public TipoMaterial getTipoMaterial() { return tipoMaterial; }
    public void setTipoMaterial(TipoMaterial tipoMaterial) { this.tipoMaterial = tipoMaterial; }

    public Double getQuantidade() { return quantidade; }
    public void setQuantidade(Double quantidade) { this.quantidade = quantidade; }

    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public StatusReciclagem getStatus() { return status; }
    public void setStatus(StatusReciclagem status) { this.status = status; }

    public LocalDateTime getDataValidacao() { return dataValidacao; }
    public void setDataValidacao(LocalDateTime dataValidacao) { this.dataValidacao = dataValidacao; }

    public Usuario getCatadorValidador() { return catadorValidador; }
    public void setCatadorValidador(Usuario catadorValidador) { this.catadorValidador = catadorValidador; }

    public String getObservacaoValidacao() { return observacaoValidacao; }
    public void setObservacaoValidacao(String observacaoValidacao) { this.observacaoValidacao = observacaoValidacao; }
}