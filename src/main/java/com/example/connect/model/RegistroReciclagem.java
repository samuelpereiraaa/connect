package com.example.connect.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * RF05 - Registro de Reciclagem
 * Regras: vinculado a usuário, ponto e catador; QR Code único; status indica situação.
 */
@Entity
@Table(name = "registro_reciclagem")
public class RegistroReciclagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro")
    private Integer id;

    /** RF05 - Regra 1: vinculado a usuário */
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    /** RF05 - Regra 1: vinculado a ponto */
    @ManyToOne
    @JoinColumn(name = "id_ponto", nullable = false)
    private PontoColeta ponto;

    /** RF05 - Regra 1: vinculado a catador */
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

    /** RF05 - Regra 2: QR Code deve ser único */
    @Column(unique = true, nullable = false)
    private String qrCode;

    /** RF05 - Regra 3: status indica situação da reciclagem */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "status_reciclagem")
    private StatusReciclagem status;

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
}
