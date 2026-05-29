package com.example.connect.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.connect.model.HistoricoPontuacao;

/**
 * RF07 – Repositório do histórico de pontuação (escrita via trigger do banco).
 * RF08 – Consulta histórica com filtros por período e tipo de operação.
 */
@Repository
public interface HistoricoPontuacaoRepository extends JpaRepository<HistoricoPontuacao, Integer> {

    /**
     * RF07 / RF08 – Regra 2: extrato completo, do mais recente ao mais antigo.
     */
    List<HistoricoPontuacao> findByUsuarioIdUsuarioOrderByDataOperacaoDesc(Integer idUsuario);

    /**
     * RF08 – Regra 2: consulta histórica com filtros opcionais.
     *
     * Todos os parâmetros são opcionais:
     *   - dataInicio / dataFim : filtro por período
     *   - tipoOperacao         : 'CREDITO', 'DEBITO' ou null para ambos
     *
     * JPQL usa IS NULL OR para ignorar o parâmetro quando não informado,
     * sem precisar de Specification ou query dinâmica.
     */
    @Query("""
        SELECT h FROM HistoricoPontuacao h
        WHERE h.usuario.idUsuario = :idUsuario
          AND (:dataInicio IS NULL OR h.dataOperacao >= :dataInicio)
          AND (:dataFim    IS NULL OR h.dataOperacao <= :dataFim)
          AND (:tipo       IS NULL OR h.tipoOperacao = :tipo)
        ORDER BY h.dataOperacao DESC
    """)
    List<HistoricoPontuacao> filtrarHistorico(
            @Param("idUsuario")  Integer idUsuario,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim")    LocalDateTime dataFim,
            @Param("tipo")       String tipo
    );
}