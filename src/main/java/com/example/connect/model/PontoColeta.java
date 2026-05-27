package com.example.connect.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ponto_coleta")
public class PontoColeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ponto")
    private Integer idPonto;

    @Column(nullable = false)
    private String nome;



    @Column(name = "horario_funcionamento")
    private String horarioFuncionamento;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "status_ponto_coleta")
    private StatusPontoColeta status;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    // Relação com materiais aceitos (tabela ponto_coleta_material)
    @ManyToMany
    @JoinTable(
        name = "ponto_coleta_material",
        joinColumns = @JoinColumn(name = "id_ponto"),
        inverseJoinColumns = @JoinColumn(name = "id_tipo_material")
    )
    private List<TipoMaterial> materiais;

    // Relacionamento com endereço (para acesso no template)
    @OneToOne(mappedBy = "pontoColeta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EnderecoPonto endereco;

    public PontoColeta() {}

    public Integer getIdPonto() { return idPonto; }
    public void setIdPonto(Integer idPonto) { this.idPonto = idPonto; }

    // Compatibilidade com código legado que usa getId()
    public Integer getId() { return idPonto; }
    public void setId(Integer id) { this.idPonto = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

   

    public String getHorarioFuncionamento() { return horarioFuncionamento; }
    public void setHorarioFuncionamento(String horarioFuncionamento) { this.horarioFuncionamento = horarioFuncionamento; }

    public StatusPontoColeta getStatus() { return status; }
    public void setStatus(StatusPontoColeta status) { this.status = status; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public List<TipoMaterial> getMateriais() { return materiais; }
    public void setMateriais(List<TipoMaterial> materiais) { this.materiais = materiais; }

    public EnderecoPonto getEndereco() { return endereco; }
    public void setEndereco(EnderecoPonto endereco) { this.endereco = endereco; }
}
