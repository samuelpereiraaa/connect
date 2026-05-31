package com.example.connect.model;

import jakarta.persistence.*;

/**
 * RF11 – Entidade Administrador.
 *
 * Vinculada a um Usuario existente (regra: administrador deve estar vinculado
 * a um usuário). O nivel_acesso controla o grau de permissão (1 a 5).
 */
@Entity
@Table(name = "administrador")
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")
    private Integer idAdmin;

    /** FK para usuario – único (cada usuário pode ser admin no máximo uma vez) */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    /**
     * Nível de acesso: 1 (operacional) a 5 (superadmin).
     * Regra RF11-3: controle de acesso conforme nível.
     */
    @Column(name = "nivel_acesso", nullable = false)
    private Integer nivelAcesso;

    // ── Construtores ──────────────────────────────────────────────────────────

    public Administrador() {}

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Integer getIdAdmin() { return idAdmin; }
    public void setIdAdmin(Integer idAdmin) { this.idAdmin = idAdmin; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Integer getNivelAcesso() { return nivelAcesso; }
    public void setNivelAcesso(Integer nivelAcesso) { this.nivelAcesso = nivelAcesso; }
}
