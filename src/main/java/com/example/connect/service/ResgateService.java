package com.example.connect.service;

import com.example.connect.model.Pontuacao;
import com.example.connect.model.Recompensa;
import com.example.connect.model.Resgate;
import com.example.connect.model.StatusRecompensa;
import com.example.connect.model.Usuario;
import com.example.connect.model.enums.StatusResgate;
import com.example.connect.repository.PontuacaoRepository;
import com.example.connect.repository.ResgateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * RF10 – Resgate de Recompensas.
 *
 * Regra 1: O usuário deve possuir pontos suficientes.
 * Regra 2: O resgate deve ser registrado.
 * Regra 3: Os pontos devem ser descontados (via trigger fn_debitar_pontos_resgate
 *           ao mudar status para APROVADO).
 */
@Service
public class ResgateService {

    private final ResgateRepository    resgateRepository;
    private final RecompensaService    recompensaService;
    private final PontuacaoRepository  pontuacaoRepository;

    public ResgateService(ResgateRepository resgateRepository,
                          RecompensaService recompensaService,
                          PontuacaoRepository pontuacaoRepository) {
        this.resgateRepository   = resgateRepository;
        this.recompensaService   = recompensaService;
        this.pontuacaoRepository = pontuacaoRepository;
    }

    // ─── RF10 – Regra 1 + 2: Solicitar resgate ───────────────────────────────

    /**
     * Solicita o resgate de uma recompensa pelo usuário logado.
     * <p>
     * Regra 1: verifica se o usuário possui pontos suficientes ANTES de registrar.
     * Regra 2: registra o resgate com status SOLICITADO.
     * Regra 3: o débito de pontos ocorre via trigger do banco quando o admin
     *           aprovar (status → APROVADO).
     */
    @Transactional
    public Resgate resgatar(Integer idRecompensa, Usuario usuario) {

        // RF10 – Regra 1a: busca e valida a recompensa
        Recompensa recompensa = recompensaService.buscarPorId(idRecompensa)
                .orElseThrow(() -> new RuntimeException("Recompensa não encontrada."));

        if (recompensa.getStatus() != StatusRecompensa.ATIVO) {
            throw new RuntimeException("Esta recompensa não está disponível para resgate.");
        }

        if (recompensa.getEstoque() != null && recompensa.getEstoque() <= 0) {
            throw new RuntimeException("Esta recompensa está esgotada.");
        }

        // RF10 – Regra 1b: verifica saldo de pontos do usuário
        int pontosNecessarios = recompensa.getPontosNecessarios();
        int saldoAtual = pontuacaoRepository
                .findByUsuarioIdUsuario(usuario.getIdUsuario())
                .map(Pontuacao::getPontosTotal)
                .orElse(0);

        if (saldoAtual < pontosNecessarios) {
            throw new RuntimeException(
                    "Pontos insuficientes. Você possui " + saldoAtual +
                    " pts, mas são necessários " + pontosNecessarios + " pts.");
        }

        // RF10 – Regra 2: registra o resgate com status SOLICITADO
        Resgate resgate = new Resgate();
        resgate.setUsuario(usuario);
        resgate.setRecompensa(recompensa);
        resgate.setPontosUtilizados(pontosNecessarios);
        resgate.setStatus(StatusResgate.SOLICITADO);

        return resgateRepository.save(resgate);
    }

    // ─── RF10 – Regra 3: Aprovar resgate (Admin) ─────────────────────────────

    /**
     * Aprova o resgate. A trigger fn_debitar_pontos_resgate no banco
     * desconta os pontos e registra o histórico automaticamente ao
     * detectar a mudança de status para APROVADO.
     *
     * RF10 – Regra 3: pontos descontados via trigger.
     */
    @Transactional
    public Resgate aprovar(Integer idResgate) {
        Resgate resgate = buscarPorId(idResgate);

        if (resgate.getStatus() != StatusResgate.SOLICITADO) {
            throw new RuntimeException("Apenas resgates com status SOLICITADO podem ser aprovados.");
        }

        // RF10 – Regra 1: re-valida pontos no momento da aprovação
        Recompensa recompensa = resgate.getRecompensa();
        int saldoAtual = pontuacaoRepository
                .findByUsuarioIdUsuario(resgate.getUsuario().getIdUsuario())
                .map(Pontuacao::getPontosTotal)
                .orElse(0);

        if (saldoAtual < recompensa.getPontosNecessarios()) {
            throw new RuntimeException(
                    "Saldo insuficiente para aprovação. Usuário possui " + saldoAtual +
                    " pts, necessários " + recompensa.getPontosNecessarios() + " pts.");
        }

        // RF10 – Regra 3: a trigger fn_debitar_pontos_resgate é acionada
        //                  quando o status muda para APROVADO.
        resgate.setStatus(StatusResgate.APROVADO);
        return resgateRepository.save(resgate);
    }

    // ─── Cancelar resgate ────────────────────────────────────────────────────

    @Transactional
    public Resgate cancelar(Integer idResgate) {
        Resgate resgate = buscarPorId(idResgate);

        if (resgate.getStatus() == StatusResgate.APROVADO) {
            throw new RuntimeException("Resgates já aprovados não podem ser cancelados.");
        }

        resgate.setStatus(StatusResgate.CANCELADO);
        return resgateRepository.save(resgate);
    }

    // ─── Consultas ───────────────────────────────────────────────────────────

    public List<Resgate> listarPorUsuario(Integer idUsuario) {
        return resgateRepository.findByUsuarioIdUsuarioOrderByDataResgateDesc(idUsuario);
    }

    public List<Resgate> listarTodos() {
        return resgateRepository.findAllByOrderByDataResgateDesc();
    }

    public List<Resgate> listarPorStatus(StatusResgate status) {
        return resgateRepository.findByStatusOrderByDataResgateDesc(status);
    }

    public Resgate buscarPorId(Integer idResgate) {
        return resgateRepository.findById(idResgate)
                .orElseThrow(() -> new RuntimeException("Resgate não encontrado."));
    }
}
