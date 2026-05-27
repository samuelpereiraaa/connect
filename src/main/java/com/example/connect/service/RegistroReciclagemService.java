package com.example.connect.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.connect.model.RegistroReciclagem;
import com.example.connect.model.StatusReciclagem;
import com.example.connect.model.Usuario;
import com.example.connect.repository.RegistroReciclagemRepository;

@Service
public class RegistroReciclagemService {

    @Autowired
    private RegistroReciclagemRepository repository;

    /**
     * RF05 - Regras:
     * 1. Vinculado a usuário, ponto e catador.
     * 2. QR Code deve ser único.
     * 3. Status indica situação da reciclagem (inicia como PENDENTE).
     */
    public RegistroReciclagem registrar(RegistroReciclagem registro) {

        // RF05 - Regra 1: todos os vínculos obrigatórios
        if (registro.getUsuario() == null || registro.getUsuario().getIdUsuario() == null) {
            throw new RuntimeException("Usuário é obrigatório para o registro de reciclagem.");
        }
        if (registro.getPonto() == null || registro.getPonto().getIdPonto() == null) {
            throw new RuntimeException("Ponto de coleta é obrigatório para o registro de reciclagem.");
        }
        if (registro.getCatador() == null || registro.getCatador().getIdUsuario() == null) {
            throw new RuntimeException("Catador é obrigatório para o registro de reciclagem.");
        }
        if (registro.getTipoMaterial() == null) {
            throw new RuntimeException("Tipo de material é obrigatório.");
        }
        if (registro.getQuantidade() == null || registro.getQuantidade() <= 0) {
            throw new RuntimeException("A quantidade deve ser maior que zero.");
        }

        // RF05 - Regra 2: gerar QR Code único
        String qr;
        do {
            qr = UUID.randomUUID().toString();
        } while (repository.existsByQrCode(qr));

        registro.setQrCode(qr);
        registro.setDataRegistro(LocalDateTime.now());

        // RF05 - Regra 3: status inicial
        registro.setStatus(StatusReciclagem.PENDENTE);

        return repository.save(registro);
    }

    /**
     * RF05 - Regra 3: atualiza o status do registro para VALIDADO ou RECUSADO.
     * Apenas catadores e administradores podem validar.
     * Registros já finalizados (VALIDADO/RECUSADO) não podem ser alterados novamente.
     *
     * @param idRegistro          ID do registro a ser validado
     * @param novoStatus          VALIDADO ou RECUSADO
     * @param observacao          Observação opcional do validador
     * @param catadorValidador    Usuário (catador ou admin) que está validando
     */
    public RegistroReciclagem validar(Integer idRegistro,
                                      StatusReciclagem novoStatus,
                                      String observacao,
                                      Usuario catadorValidador) {

        if (novoStatus == StatusReciclagem.PENDENTE) {
            throw new RuntimeException("Não é permitido retornar o status para PENDENTE.");
        }

        RegistroReciclagem registro = repository.findById(idRegistro)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado: #" + idRegistro));

        // RF05 - Regra 3: impede re-validação de registros já finalizados
        if (registro.getStatus() != StatusReciclagem.PENDENTE) {
            throw new RuntimeException(
                "Este registro já foi " + registro.getStatus().name().toLowerCase() +
                " e não pode ser alterado novamente."
            );
        }

        registro.setStatus(novoStatus);
        registro.setDataValidacao(LocalDateTime.now());
        registro.setCatadorValidador(catadorValidador);
        registro.setObservacaoValidacao(observacao);

        return repository.save(registro);
    }

    public List<RegistroReciclagem> listarPorUsuario(Integer idUsuario) {
        return repository.findByUsuarioIdUsuarioOrderByDataRegistroDesc(idUsuario);
    }

    public List<RegistroReciclagem> listarTodos() {
        return repository.findAll();
    }

    public List<RegistroReciclagem> listarPendentes() {
        return repository.findByStatus(StatusReciclagem.PENDENTE);
    }

    public RegistroReciclagem buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado."));
    }
}