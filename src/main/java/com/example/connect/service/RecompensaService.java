package com.example.connect.service;

import com.example.connect.model.Recompensa;
import com.example.connect.model.StatusRecompensa;
import com.example.connect.repository.RecompensaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RecompensaService {

    private final RecompensaRepository recompensaRepository;

    public RecompensaService(RecompensaRepository recompensaRepository) {
        this.recompensaRepository = recompensaRepository;
    }

    /**
     * RF09 – Regra 1: cadastrar nova recompensa.
     * RF09 – Regra 2: apenas administradores (verificação feita no controller).
     */
    @Transactional
    public Recompensa cadastrar(Recompensa recompensa) {
        validarCampos(recompensa);
        recompensa.setStatus(StatusRecompensa.ATIVO);
        return recompensaRepository.save(recompensa);
    }

    /**
     * RF09 – Regra 1: alterar recompensa existente.
     */
    @Transactional
    public Recompensa alterar(Integer id, Recompensa dados) {
        Recompensa existente = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Recompensa não encontrada."));

        validarCampos(dados);

        existente.setNome(dados.getNome());
        existente.setDescricao(dados.getDescricao());
        existente.setPontosNecessarios(dados.getPontosNecessarios());
        existente.setTipo(dados.getTipo());
        existente.setEstoque(dados.getEstoque());

        if (dados.getStatus() != null) {
            existente.setStatus(dados.getStatus());
        }

        return recompensaRepository.save(existente);
    }

    /**
     * RF09 – Regra 1: listagem admin com filtros opcionais de nome e status.
     * Ambos os parâmetros são opcionais (null = sem filtro para aquele campo).
     */
    public List<Recompensa> listarComFiltros(String nome, StatusRecompensa status) {

        boolean temNome   = nome   != null && !nome.isBlank();
        boolean temStatus = status != null;

        if (temNome && temStatus) {
            return recompensaRepository
                    .findByNomeContainingIgnoreCaseAndStatusOrderByNomeAsc(nome, status);
        }
        if (temNome) {
            return recompensaRepository
                    .findByNomeContainingIgnoreCaseOrderByNomeAsc(nome);
        }
        if (temStatus) {
            return recompensaRepository
                    .findByStatusOrderByNomeAsc(status);
        }
        return recompensaRepository.findAllByOrderByNomeAsc();
    }

    /**
     * Listagem pública: apenas recompensas ATIVAS.
     */
    public List<Recompensa> listarAtivas() {
        return recompensaRepository.findByStatusOrderByNomeAsc(StatusRecompensa.ATIVO);
    }

    /**
     * RF09 – Regra 1: consultar por ID.
     */
    public Optional<Recompensa> buscarPorId(Integer id) {
        return recompensaRepository.findById(id);
    }

    /**
     * Altera somente o status da recompensa.
     */
    @Transactional
    public void alterarStatus(Integer id, String novoStatus) {
        Recompensa recompensa = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Recompensa não encontrada."));
        recompensa.setStatus(StatusRecompensa.valueOf(novoStatus));
        recompensaRepository.save(recompensa);
    }

    // ─── Validações internas ────────────────────────────────────────────────

    private void validarCampos(Recompensa r) {
        if (r.getNome() == null || r.getNome().isBlank()) {
            throw new RuntimeException("O nome da recompensa é obrigatório.");
        }
        if (r.getPontosNecessarios() == null || r.getPontosNecessarios() <= 0) {
            throw new RuntimeException("Os pontos necessários devem ser maiores que zero.");
        }
        if (r.getEstoque() != null && r.getEstoque() < 0) {
            throw new RuntimeException("O estoque não pode ser negativo.");
        }
    }
}