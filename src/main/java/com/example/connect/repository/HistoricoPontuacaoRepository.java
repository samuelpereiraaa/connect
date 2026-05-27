package com.example.connect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.connect.model.HistoricoPontuacao;

/**
 * RF07 – Repositório do histórico de pontuação.
 * Apenas leitura — a trigger fn_atualizar_pontuacao() é responsável pela escrita.
 */
@Repository
public interface HistoricoPontuacaoRepository extends JpaRepository<HistoricoPontuacao, Integer> {

    /** RF07 – Extrato do usuário, do mais recente ao mais antigo. */
    List<HistoricoPontuacao> findByUsuarioIdUsuarioOrderByDataOperacaoDesc(Integer idUsuario);
}
