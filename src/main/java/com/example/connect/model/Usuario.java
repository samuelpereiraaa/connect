package com.example.connect.model;

import java.time.LocalDate;
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
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    private String nome;

    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(unique = true, nullable = false)
    private String email;

    private String senha;

    private String telefone;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private StatusUsuario status;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TipoUsuario tipo;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @Column(name = "data_atualizacao")
    private LocalDate dataAtualizacao;

    // ── RF03 – Bloqueio por tentativas inválidas (RF03 Regra 1) ──────────────

    /** Contador de tentativas de login com senha errada. */
    @Column(name = "tentativas_login", nullable = false)
    private Integer tentativasLogin = 0;

    /**
     * Data/hora até quando o acesso está temporariamente bloqueado.
     * Nulo = não bloqueado.
     */
    @Column(name = "bloqueado_ate")
    private LocalDateTime bloqueadoAte;

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public StatusUsuario getStatus() { return status; }
    public void setStatus(StatusUsuario status) { this.status = status; }

    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public LocalDate getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDate dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public Integer getTentativasLogin() { return tentativasLogin; }
    public void setTentativasLogin(Integer tentativasLogin) { this.tentativasLogin = tentativasLogin; }

    public LocalDateTime getBloqueadoAte() { return bloqueadoAte; }
    public void setBloqueadoAte(LocalDateTime bloqueadoAte) { this.bloqueadoAte = bloqueadoAte; }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Retorna true se o usuário está temporariamente bloqueado agora. */
    public boolean estaBloqueado() {
        return bloqueadoAte != null && LocalDateTime.now().isBefore(bloqueadoAte);
    }
}
