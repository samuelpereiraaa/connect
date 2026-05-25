package com.example.connect.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.connect.model.HistoricoUsuario;
import com.example.connect.model.StatusUsuario;
import com.example.connect.model.TipoUsuario;
import com.example.connect.model.Usuario;
import com.example.connect.repository.HistoricoUsuarioRepository;
import com.example.connect.repository.UsuarioRepository;

@Service
public class GerenciamentoUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HistoricoUsuarioRepository historicoRepository;

    // RF02 - Busca com múltiplos filtros
    public List<Usuario> buscar(String nome, String email, TipoUsuario tipo, StatusUsuario status) {
        List<Usuario> todos = usuarioRepository.findAll();

        return todos.stream()
            .filter(u -> nome   == null || nome.isBlank()            || u.getNome().toLowerCase().contains(nome.toLowerCase()))
            .filter(u -> email  == null || email.isBlank()           || u.getEmail().toLowerCase().contains(email.toLowerCase()))
            .filter(u -> tipo   == null                              || u.getTipo() == tipo)
            .filter(u -> status == null                              || u.getStatus() == status)
            .toList();
    }

    // RF02 - Alterar dados do usuário (somente admin)
    public void alterar(Integer idUsuario, Usuario dadosNovos, Usuario adminResponsavel) {

        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Registra cada campo alterado individualmente no histórico (RS003)
        if (!usuario.getNome().equals(dadosNovos.getNome())) {
            registrarHistorico(usuario, "nome", usuario.getNome(), dadosNovos.getNome(), adminResponsavel, "Alteração de nome");
            usuario.setNome(dadosNovos.getNome());
        }

        if (!usuario.getEmail().equals(dadosNovos.getEmail())) {
            if (usuarioRepository.existsByEmail(dadosNovos.getEmail())) {
                throw new RuntimeException("Email já cadastrado para outro usuário");
            }
            registrarHistorico(usuario, "email", usuario.getEmail(), dadosNovos.getEmail(), adminResponsavel, "Alteração de e-mail");
            usuario.setEmail(dadosNovos.getEmail());
        }

        if (dadosNovos.getTelefone() != null && !dadosNovos.getTelefone().equals(usuario.getTelefone())) {
            registrarHistorico(usuario, "telefone", usuario.getTelefone(), dadosNovos.getTelefone(), adminResponsavel, "Alteração de telefone");
            usuario.setTelefone(dadosNovos.getTelefone());
        }

        if (dadosNovos.getTipo() != null && dadosNovos.getTipo() != usuario.getTipo()) {
            registrarHistorico(usuario, "tipo", usuario.getTipo().name(), dadosNovos.getTipo().name(), adminResponsavel, "Alteração de perfil");
            usuario.setTipo(dadosNovos.getTipo());
        }

        usuarioRepository.save(usuario);
    }

    // RF02 - Alterar status (ativar, inativar, suspender)
    public void alterarStatus(Integer idUsuario, StatusUsuario novoStatus, Usuario adminResponsavel) {

        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String statusAnterior = usuario.getStatus().name();

        usuario.setStatus(novoStatus);
        usuarioRepository.save(usuario);

        registrarHistorico(usuario, "status", statusAnterior, novoStatus.name(), adminResponsavel,
                "Status alterado para " + novoStatus.name());
    }

    // RF02 - Buscar histórico de um usuário
    public List<HistoricoUsuario> buscarHistorico(Integer idUsuario) {
        return historicoRepository.findByUsuarioIdUsuarioOrderByDataAcaoDesc(idUsuario);
    }

    // Utilitário interno para registrar auditoria (RS003)
    private void registrarHistorico(Usuario usuario, String campo,
                                    String valorAnterior, String valorNovo,
                                    Usuario responsavel, String descricao) {
        HistoricoUsuario h = new HistoricoUsuario();
        h.setUsuario(usuario);
        h.setCampoAlterado(campo);
        h.setValorAnterior(valorAnterior);
        h.setValorNovo(valorNovo);
        h.setResponsavel(responsavel);
        h.setDataAcao(LocalDateTime.now());
        h.setDescricao(descricao);
        historicoRepository.save(h);
    }
}
