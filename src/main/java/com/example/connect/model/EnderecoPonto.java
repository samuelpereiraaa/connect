package com.example.connect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "endereco")
public class EnderecoPonto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endereco")
    private Integer idEndereco;

    @OneToOne
    @JoinColumn(name = "id_ponto", nullable = false)
    private PontoColeta pontoColeta;

    @Column(nullable = false, length = 8)
    private String cep;

    @Column(nullable = false)
    private String logradouro;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String municipio;

    @Column(nullable = false, length = 2)
    private String estado;

    private String complemento;

    public Integer getIdEndereco() { return idEndereco; }
    public void setIdEndereco(Integer idEndereco) { this.idEndereco = idEndereco; }

    public PontoColeta getPontoColeta() { return pontoColeta; }
    public void setPontoColeta(PontoColeta pontoColeta) { this.pontoColeta = pontoColeta; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
}
