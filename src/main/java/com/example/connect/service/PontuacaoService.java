package com.example.connect.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.connect.model.HistoricoPontuacao;
import com.example.connect.model.Pontuacao;
import com.example.connect.repository.HistoricoPontuacaoRepository;
import com.example.connect.repository.PontuacaoRepository;

/**
 * RF07 – Serviço de Gerenciamento de Pontuação.
 *
 * Regra 1: a pontuação é calculada automaticamente pela trigger
 *          fn_atualizar_pontuacao() no banco sempre que um registro
 *          de reciclagem é atualizado para VALIDADO.
 *
 * Regra 2: o saldo e o nível são atualizados conforme registros validados;
 *          este serviço expõe métodos de leitura para exibição na interface.
 *
 * Observação: nenhum método deste serviço insere ou altera pontos diretamente.
 *             Toda a lógica de cálculo, crédito e histórico é delegada ao banco.
 */
@Service
public class PontuacaoService {

    @Autowired
    private PontuacaoRepository pontuacaoRepository;

    @Autowired
    private HistoricoPontuacaoRepository historicoRepository;

    // -------------------------------------------------------------------------
    // RF07 – Regra 1 e 2: consultar pontuação atual do usuário
    // -------------------------------------------------------------------------

    /**
     * Retorna a pontuação do usuário, ou um objeto vazio com INICIANTE/0
     * caso ainda não exista registro (usuário sem nenhuma reciclagem validada).
     */
    public Pontuacao buscarPorUsuario(Integer idUsuario) {
        Optional<Pontuacao> opt = pontuacaoRepository.findByUsuarioIdUsuario(idUsuario);

        if (opt.isPresent()) {
            return opt.get();
        }

        // Antes da primeira validação o banco ainda não criou o registro;
        // retornamos um objeto transiente para evitar null na view.
        Pontuacao vazia = new Pontuacao();
        vazia.setPontosTotal(0);
        vazia.setNivel(com.example.connect.model.NivelUsuario.INICIANTE);
        return vazia;
    }

    // -------------------------------------------------------------------------
    // RF07 – Regra 2: histórico de lançamentos (extrato)
    // -------------------------------------------------------------------------

    /**
     * Retorna os lançamentos de pontuação do usuário, do mais recente ao mais antigo.
     * Cada linha representa um CREDITO (reciclagem validada) ou DEBITO (resgate aprovado).
     */
    public List<HistoricoPontuacao> buscarHistorico(Integer idUsuario) {
        return historicoRepository.findByUsuarioIdUsuarioOrderByDataOperacaoDesc(idUsuario);
    }
}
