package com.example.connect.repository;

import com.example.connect.model.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * RF11 – Repositório de Relatório.
 */
@Repository
public interface RelatorioRepository extends JpaRepository<Relatorio, Integer> {

    /** Lista todos os relatórios do administrador, mais recentes primeiro. */
    List<Relatorio> findByAdministradorIdAdminOrderByDataInicioDesc(Integer idAdmin);

    /** Lista todos os relatórios ordenados por data de início. */
    List<Relatorio> findAllByOrderByDataInicioDesc();

    /**
     * RF11-1: busca relatórios filtrados por tipo e/ou sobreposição de período.
     * Retorna relatórios cujo intervalo [dataInicio, dataFim] sobrepõe o período informado.
     */
    @Query("""
        SELECT r FROM Relatorio r
        WHERE (:tipo IS NULL OR r.tipo = :tipo)
          AND r.dataFim   >= :dataInicio
          AND r.dataInicio <= :dataFim
        ORDER BY r.dataInicio DESC
    """)
    List<Relatorio> findByTipoAndPeriodo(
            @Param("tipo")       String tipo,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim")    LocalDate dataFim);
}
