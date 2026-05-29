package com.example.connect.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.connect.model.HistoricoPontuacao;
import com.example.connect.model.NivelUsuario;
import com.example.connect.model.Pontuacao;
import com.example.connect.repository.HistoricoPontuacaoRepository;
import com.example.connect.repository.PontuacaoRepository;

/**
 * RF07 – Pontuação calculada automaticamente pela trigger do banco.
 * RF08 – Consulta de pontuação: visualização de pontos, nível e histórico
 *         com filtro por período e tipo de operação.
 */
@Service
public class PontuacaoService {

    @Autowired
    private PontuacaoRepository pontuacaoRepository;

    @Autowired
    private HistoricoPontuacaoRepository historicoRepository;

    // -------------------------------------------------------------------------
    // RF07 / RF08 – Regra 1: saldo e nível atuais do usuário
    // -------------------------------------------------------------------------

    /**
     * Retorna a pontuação do usuário.
     * Se ainda não existir registro (nenhuma reciclagem validada),
     * devolve um objeto transiente com INICIANTE/0 para evitar null na view.
     */
    public Pontuacao buscarPorUsuario(Integer idUsuario) {
        Optional<Pontuacao> opt = pontuacaoRepository.findByUsuarioIdUsuario(idUsuario);
        if (opt.isPresent()) {
            return opt.get();
        }

        Pontuacao vazia = new Pontuacao();
        vazia.setPontosTotal(0);
        vazia.setNivel(NivelUsuario.INICIANTE);
        return vazia;
    }

    // -------------------------------------------------------------------------
    // RF07 – Regra 2: histórico completo (sem filtro)
    // -------------------------------------------------------------------------

    /**
     * Extrato completo do usuário, do mais recente ao mais antigo.
     * Usado na carga inicial da página antes de qualquer filtro ser aplicado.
     */
    public List<HistoricoPontuacao> buscarHistorico(Integer idUsuario) {
        return historicoRepository.findByUsuarioIdUsuarioOrderByDataOperacaoDesc(idUsuario);
    }

    // -------------------------------------------------------------------------
    // RF08 – Regra 2: consulta histórica com filtros
    // -------------------------------------------------------------------------

    /**
     * Retorna o histórico filtrado por período e/ou tipo de operação.
     *
     * @param idUsuario   ID do usuário logado
     * @param dataInicio  Data inicial do filtro (inclusive); null = sem limite inferior
     * @param dataFim     Data final do filtro (inclusive, até 23:59:59); null = sem limite superior
     * @param tipo        "CREDITO", "DEBITO" ou null para ambos
     */
    public List<HistoricoPontuacao> consultarHistorico(
            Integer idUsuario,
            LocalDate dataInicio,
            LocalDate dataFim,
            String tipo) {

        // Converte LocalDate para LocalDateTime nos extremos do dia
        LocalDateTime inicio = (dataInicio != null) ? dataInicio.atStartOfDay()         : null;
        LocalDateTime fim    = (dataFim    != null) ? dataFim.atTime(23, 59, 59) : null;

        // Normaliza tipo: string vazia ou "TODOS" vira null → sem filtro
        String tipoFiltro = (tipo != null && !tipo.isBlank() && !"TODOS".equals(tipo))
                ? tipo.toUpperCase()
                : null;

        return historicoRepository.filtrarHistorico(idUsuario, inicio, fim, tipoFiltro);
    }
}