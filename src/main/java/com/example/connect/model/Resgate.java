package com.example.connect.model;

import com.example.connect.model.enums.StatusResgate;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(name = "resgate")
public class Resgate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resgate")
    private Integer idResgate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recompensa", nullable = false)
    private Recompensa recompensa;

    @Column(name = "pontos_utilizados", nullable = false)
    private Integer pontosUtilizados;

    @Column(name = "data_resgate")
    private LocalDateTime dataResgate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private StatusResgate status = StatusResgate.SOLICITADO;

    // ── Getters ──────────────────────────────────────────────────────────────

    public Integer getIdResgate() { return idResgate; }
    public Usuario getUsuario() { return usuario; }
    public Recompensa getRecompensa() { return recompensa; }
    public Integer getPontosUtilizados() { return pontosUtilizados; }
    public LocalDateTime getDataResgate() { return dataResgate; }
    public StatusResgate getStatus() { return status; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setIdResgate(Integer idResgate) { this.idResgate = idResgate; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setRecompensa(Recompensa recompensa) { this.recompensa = recompensa; }
    public void setPontosUtilizados(Integer pontosUtilizados) { this.pontosUtilizados = pontosUtilizados; }
    public void setDataResgate(LocalDateTime dataResgate) { this.dataResgate = dataResgate; }
    public void setStatus(StatusResgate status) { this.status = status; }
}