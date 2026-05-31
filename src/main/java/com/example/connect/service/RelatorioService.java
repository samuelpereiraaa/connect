package com.example.connect.service;

import com.example.connect.model.Administrador;
import com.example.connect.model.Relatorio;
import com.example.connect.model.StatusReciclagem;
import com.example.connect.model.TipoUsuario;
import com.example.connect.model.Usuario;
import com.example.connect.repository.AdministradorRepository;
import com.example.connect.repository.RegistroReciclagemRepository;
import com.example.connect.repository.RelatorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * RF11 – Serviço de Relatório e Controle Administrativo.
 *
 * Regras implementadas:
 *   RF11-1: geração de relatórios por período.
 *   RF11-2: administrador deve estar vinculado a um usuário.
 *   RF11-3: controle de acesso conforme nível.
 */
@Service
public class RelatorioService {

    @Autowired
    private RelatorioRepository relatorioRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private RegistroReciclagemRepository registroRepository;

    // ── Administrador ─────────────────────────────────────────────────────────

    /**
     * RF11-2: obtém o Administrador vinculado ao usuário logado.
     * Lança exceção se o usuário não for ADMINISTRADOR ou não tiver registro
     * na tabela administrador.
     */
    public Administrador obterAdmin(Usuario usuario) {
        if (usuario == null || usuario.getTipo() != TipoUsuario.ADMINISTRADOR) {
            throw new SecurityException("Acesso restrito a administradores.");
        }
        return administradorRepository
                .findByUsuarioIdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new IllegalStateException(
                        "Usuário administrador não possui cadastro na tabela administrador."));
    }

    /**
     * RF11-3: verifica se o administrador possui nível de acesso mínimo exigido.
     */
    public void verificarNivel(Administrador admin, int nivelMinimo) {
        if (admin.getNivelAcesso() < nivelMinimo) {
            throw new SecurityException(
                    "Nível de acesso insuficiente. Necessário: " + nivelMinimo
                    + " | Seu nível: " + admin.getNivelAcesso());
        }
    }

    // ── Relatório ─────────────────────────────────────────────────────────────

    /**
     * RF11-1: gera e persiste um relatório de reciclagem para o período informado.
     *
     * O total_reciclado é calculado somando a quantidade de todos os registros
     * com status VALIDADO cujo data_registro esteja dentro do intervalo.
     */
    @Transactional
    public Relatorio gerarRelatorio(String tipo,
                                    LocalDate dataInicio,
                                    LocalDate dataFim,
                                    Administrador administrador) {

        // RF11-1: validar período
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("Data de início e fim são obrigatórias.");
        }
        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("A data fim não pode ser anterior à data início.");
        }

        // Calcula o total reciclado no período via consulta no repositório de registros
        double totalReciclado = registroRepository
                .findAll()
                .stream()
                .filter(r -> r.getStatus() == StatusReciclagem.VALIDADO)
                .filter(r -> {
                    LocalDate data = r.getDataRegistro().toLocalDate();
                    return !data.isBefore(dataInicio) && !data.isAfter(dataFim);
                })
                .mapToDouble(r -> r.getQuantidade() != null ? r.getQuantidade().doubleValue() : 0)
                .sum();

        Relatorio relatorio = new Relatorio();
        relatorio.setTipo(tipo != null ? tipo.toUpperCase() : "RECICLAGEM");
        relatorio.setDataInicio(dataInicio);
        relatorio.setDataFim(dataFim);
        relatorio.setTotalReciclado(totalReciclado);
        relatorio.setAdministrador(administrador);

        return relatorioRepository.save(relatorio);
    }

    /**
     * RF11-1: lista relatórios filtrando por tipo e/ou período.
     * Parâmetros nulos são ignorados (sem filtro para aquele campo).
     */
    public List<Relatorio> listar(String tipo, LocalDate dataInicio, LocalDate dataFim) {
        // Se nenhum filtro informado, retorna todos
        if (tipo == null && dataInicio == null && dataFim == null) {
            return relatorioRepository.findAllByOrderByDataInicioDesc();
        }
        // Valores padrão para o filtro de período quando apenas um lado é informado
        LocalDate inicio = dataInicio != null ? dataInicio : LocalDate.of(2000, 1, 1);
        LocalDate fim    = dataFim    != null ? dataFim    : LocalDate.now();
        String tipoFiltro = (tipo != null && !tipo.isBlank()) ? tipo.toUpperCase() : null;

        return relatorioRepository.findByTipoAndPeriodo(tipoFiltro, inicio, fim);
    }

    /** Retorna um relatório pelo id, ou lança exceção se não encontrado. */
    public Relatorio buscarPorId(Integer id) {
        return relatorioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relatório #" + id + " não encontrado."));
    }

    /** RF11-3 (nível 3+): exclui um relatório. */
    @Transactional
    public void excluir(Integer id, Administrador admin) {
        verificarNivel(admin, 3);
        Relatorio relatorio = buscarPorId(id);
        relatorioRepository.delete(relatorio);
    }
}
