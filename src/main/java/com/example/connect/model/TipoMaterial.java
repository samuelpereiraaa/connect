package com.example.connect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_material")
public class TipoMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_material")
    private Integer id;

    private String nome;

    private String descricao;

    @Column(name = "pontos_por_unidade")
    private Double pontosPorUnidade;

    private Boolean ativo;

    public TipoMaterial() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPontosPorUnidade() {
        return pontosPorUnidade;
    }

    public void setPontosPorUnidade(Double pontosPorUnidade) {
        this.pontosPorUnidade = pontosPorUnidade;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}