package com.example.connect.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.connect.model.Endereco;
import com.example.connect.model.StatusUsuario;
import com.example.connect.model.TipoUsuario;
import com.example.connect.model.Usuario;
import com.example.connect.repository.EnderecoRepository;
import com.example.connect.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    // ─── RF03 – resultado da tentativa de login ─────────────────────────────────
    public enum ResultadoLogin {
        SUCESSO,
        EMAIL_NAO_ENCONTRADO,
        SENHA_INCORRETA,
        USUARIO_INATIVO,
        USUARIO_SUSPENSO
    }

    /**
     * RF03 – Autentica o usuário pelo e-mail e senha.
     * Valida: existência do e-mail, senha correta e status da conta.
     *
     * @param email  e-mail informado no formulário
     * @param senha  senha em texto puro informada no formulário
     * @return ResultadoLogin indicando o desfecho da tentativa
     */
    public ResultadoLogin autenticar(String email, String senha) {

        // Regra 1 – valida e-mail
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            return ResultadoLogin.EMAIL_NAO_ENCONTRADO;
        }

        // Regra 1 – valida senha
        if (!encoder.matches(senha, usuario.getSenha())) {
            return ResultadoLogin.SENHA_INCORRETA;
        }

        // Regra 2 – verifica status (apenas ATIVO pode entrar)
        if (usuario.getStatus() == StatusUsuario.INATIVO) {
            return ResultadoLogin.USUARIO_INATIVO;
        }
        if (usuario.getStatus() == StatusUsuario.SUSPENSO) {
            return ResultadoLogin.USUARIO_SUSPENSO;
        }

        return ResultadoLogin.SUCESSO;
    }

    /** Mantido para compatibilidade com código legado. */
    public boolean login(String email, String senha) {
        return autenticar(email, senha) == ResultadoLogin.SUCESSO;
    }

    // ─── Cadastro ────────────────────────────────────────────────────────────────
    public void cadastrar(Usuario usuario, Endereco endereco) {

        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        if (usuario.getSenha().length() < 8) {
            throw new RuntimeException("Senha extremamente fraca");
        }

        usuario.setSenha(encoder.encode(usuario.getSenha()));
        usuario.setStatus(StatusUsuario.ATIVO);
        usuario.setDataCadastro(LocalDateTime.now());

        Usuario salvo = usuarioRepository.save(usuario);

        endereco.setUsuario(salvo);
        enderecoRepository.save(endereco);
    }

    // ─── Outros ─────────────────────────────────────────────────────────────────
    public void inativar(Integer id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        usuario.setStatus(StatusUsuario.INATIVO);
        usuarioRepository.save(usuario);
    }

    public List<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContaining(nome);
    }

    public List<Usuario> buscarPorTipo(TipoUsuario tipo) {
        return usuarioRepository.findByTipo(tipo);
    }

    public List<Usuario> buscarPorStatus(StatusUsuario status) {
        return usuarioRepository.findByStatus(status);
    }
    
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}


