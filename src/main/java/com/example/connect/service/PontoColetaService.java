package com.example.connect.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.connect.model.EnderecoPonto;
import com.example.connect.model.PontoColeta;
import com.example.connect.model.StatusPontoColeta;
import com.example.connect.repository.EnderecoPontoRepository;
import com.example.connect.repository.PontoColetaRepository;

@Service
public class PontoColetaService {

    @Autowired
    private PontoColetaRepository pontoRepository;

    @Autowired
    private EnderecoPontoRepository enderecoRepository;

    /**
     * RF04 - Regra 1: o ponto deve possuir endereço vinculado.
     * RF04 - Regra 2: deve permitir cadastro.
     */
    @Transactional
    public PontoColeta cadastrar(PontoColeta ponto, EnderecoPonto endereco) {

        if (ponto.getNome() == null || ponto.getNome().isBlank()) {
            throw new RuntimeException("O nome do ponto é obrigatório.");
        }

        // RF04 - Regra 1: endereço é obrigatório
        if (endereco == null || endereco.getCep() == null || endereco.getCep().isBlank()) {
            throw new RuntimeException("O endereço do ponto de coleta é obrigatório.");
        }

        ponto.setStatus(StatusPontoColeta.ATIVO);
        ponto.setDataCadastro(LocalDateTime.now());

        PontoColeta salvo = pontoRepository.save(ponto);

        endereco.setPontoColeta(salvo);
        enderecoRepository.save(endereco);

        return salvo;
    }

    /**
     * RF04 - Regra 2: deve permitir alteração.
     */
    @Transactional
    public void alterar(Integer id, PontoColeta dadosNovos, EnderecoPonto enderecoNovo) {

        PontoColeta ponto = pontoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ponto não encontrado."));

        ponto.setNome(dadosNovos.getNome());
     
        ponto.setHorarioFuncionamento(dadosNovos.getHorarioFuncionamento());

        if (dadosNovos.getStatus() != null) {
            ponto.setStatus(dadosNovos.getStatus());
        }

        pontoRepository.save(ponto);

        // Atualiza ou cria endereço vinculado (RF04 - Regra 1)
        EnderecoPonto endereco = enderecoRepository
                .findByPontoColetaIdPonto(id)
                .orElse(new EnderecoPonto());

        endereco.setPontoColeta(ponto);
        endereco.setCep(enderecoNovo.getCep());
        endereco.setLogradouro(enderecoNovo.getLogradouro());
        endereco.setNumero(enderecoNovo.getNumero());
        endereco.setBairro(enderecoNovo.getBairro());
        endereco.setMunicipio(enderecoNovo.getMunicipio());
        endereco.setEstado(enderecoNovo.getEstado());
        endereco.setComplemento(enderecoNovo.getComplemento());

        enderecoRepository.save(endereco);
    }

    public List<PontoColeta> listarTodos() {
        return pontoRepository.findAll();
    }

    public List<PontoColeta> listarAtivos() {
        return pontoRepository.findByStatus(StatusPontoColeta.ATIVO);
    }

    public List<PontoColeta> buscarPorNome(String nome) {
        return pontoRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * RF04 - Regra 2: deve permitir consulta.
     */
    public PontoColeta buscarPorId(Integer id) {
        return pontoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ponto não encontrado."));
    }

    public EnderecoPonto buscarEnderecoPorPonto(Integer idPonto) {
        return enderecoRepository.findByPontoColetaIdPonto(idPonto)
                .orElse(new EnderecoPonto());
    }

    public void alterarStatus(Integer id, String novoStatus) {

        PontoColeta ponto = pontoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ponto não encontrado."));

        ponto.setStatus(StatusPontoColeta.valueOf(novoStatus));
        pontoRepository.save(ponto);
    }
}
