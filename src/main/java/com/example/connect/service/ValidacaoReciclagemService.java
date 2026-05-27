package com.example.connect.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.connect.model.RegistroReciclagem;
import com.example.connect.model.StatusReciclagem;
import com.example.connect.model.TipoUsuario;
import com.example.connect.model.Usuario;
import com.example.connect.repository.RegistroReciclagemRepository;

/**
 * RF06 - Validação de Reciclagem
 *
 * Regra 1: O catador deve validar ou recusar o registro.
 * Regra 2: O status deve ser atualizado após a validação.
 * Regra 3: O sistema deve registrar data e responsável pela validação.
 */
@Service
public class ValidacaoReciclagemService {

    @Autowired
    private RegistroReciclagemRepository repository;

    // -------------------------------------------------------------------------
    // RF06 - Regra 1: listar registros pendentes de validação
    // -------------------------------------------------------------------------

    /**
     * Retorna os registros PENDENTES visíveis para o usuário logado.
     * - CATADOR: vê apenas os registros em que ele é o catador vinculado.
     * - ADMINISTRADOR: vê todos os registros pendentes.
     */
    public List<RegistroReciclagem> listarPendentes(Usuario responsavel) {
        if (responsavel.getTipo() == TipoUsuario.ADMINISTRADOR) {
            return repository.findByStatusOrderByDataRegistroAsc(StatusReciclagem.PENDENTE);
        }
        return repository.findByCatadorIdUsuarioAndStatusOrderByDataRegistroAsc(
                responsavel.getIdUsuario(), StatusReciclagem.PENDENTE);
    }

    // -------------------------------------------------------------------------
    // RF06 - Regras 1, 2 e 3: executar a validação
    // -------------------------------------------------------------------------

    /**
     * Valida ou recusa um registro de reciclagem.
     *
     * Regra 1: apenas CATADOR ou ADMINISTRADOR podem executar.
     *          O catador só pode validar registros em que é o catador vinculado,
     *          salvo se for administrador.
     * Regra 2: atualiza o status para VALIDADO ou RECUSADO.
     * Regra 3: registra dataValidacao, catadorValidador e observacaoValidacao.
     *
     * @param idRegistro       ID do registro a ser avaliado
     * @param novoStatus       VALIDADO ou RECUSADO
     * @param observacao       Observação opcional do validador
     * @param responsavel      Usuário catador ou admin que está executando a ação
     */
    public RegistroReciclagem validar(Integer idRegistro,
                                      StatusReciclagem novoStatus,
                                      String observacao,
                                      Usuario responsavel) {

        // RF06 - Regra 1: somente catador ou administrador
        if (responsavel.getTipo() != TipoUsuario.CATADOR
                && responsavel.getTipo() != TipoUsuario.ADMINISTRADOR) {
            throw new RuntimeException(
                "Apenas catadores ou administradores podem validar registros de reciclagem.");
        }

        // RF06 - Regra 2: novoStatus deve ser VALIDADO ou RECUSADO
        if (novoStatus == StatusReciclagem.PENDENTE) {
            throw new RuntimeException(
                "Não é permitido definir o status como PENDENTE durante a validação.");
        }

        RegistroReciclagem registro = repository.findById(idRegistro)
                .orElseThrow(() -> new RuntimeException(
                    "Registro não encontrado: #" + idRegistro));

        // RF06 - Regra 2: só registros ainda PENDENTES podem ser avaliados
        if (registro.getStatus() != StatusReciclagem.PENDENTE) {
            throw new RuntimeException(
                "Este registro já foi " + registro.getStatus().name().toLowerCase() +
                " e não pode ser avaliado novamente.");
        }

        // RF06 - Regra 1: catador só avalia registros vinculados a ele
        boolean ehCatadorDoRegistro = registro.getCatador() != null
                && registro.getCatador().getIdUsuario().equals(responsavel.getIdUsuario());
        boolean ehAdmin = responsavel.getTipo() == TipoUsuario.ADMINISTRADOR;

        if (!ehAdmin && !ehCatadorDoRegistro) {
            throw new RuntimeException(
                "Este registro não está vinculado ao seu usuário. " +
                "Você só pode validar reciclagens em que é o catador responsável.");
        }

        // RF06 - Regra 2: atualiza o status
        registro.setStatus(novoStatus);

        // RF06 - Regra 3: registra data e responsável pela validação
        registro.setDataValidacao(LocalDateTime.now());
        registro.setCatadorValidador(responsavel);
        registro.setObservacaoValidacao(
            (observacao != null && !observacao.isBlank()) ? observacao.trim() : null);

        return repository.save(registro);
    }

    // -------------------------------------------------------------------------
    // RF06 - Regra 3: histórico de validações realizadas pelo responsável
    // -------------------------------------------------------------------------

    /**
     * Retorna o histórico de registros já validados ou recusados pelo catador.
     * Administradores veem todos; catadores veem apenas os seus.
     */
    public List<RegistroReciclagem> listarHistoricoValidacoes(Usuario responsavel) {
        if (responsavel.getTipo() == TipoUsuario.ADMINISTRADOR) {
            return repository.findAll().stream()
                    .filter(r -> r.getStatus() != StatusReciclagem.PENDENTE)
                    .sorted((a, b) -> b.getDataValidacao().compareTo(a.getDataValidacao()))
                    .toList();
        }
        return repository.findValidacoesPorCatador(responsavel.getIdUsuario());
    }

    /** Busca um registro pelo ID para exibição de detalhes. */
    public RegistroReciclagem buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado: #" + id));
    }
}
