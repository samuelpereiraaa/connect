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

    // ── RF03 Regra 1 – configurações de bloqueio ──────────────────────────────
    /** Máximo de tentativas antes do bloqueio. */
    private static final int MAX_TENTATIVAS = 5;

    /** Duração do bloqueio temporário em minutos. */
    private static final int MINUTOS_BLOQUEIO = 15;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    // ── RF03 – resultado da tentativa de login ────────────────────────────────
    public enum ResultadoLogin {
        SUCESSO,
        EMAIL_NAO_ENCONTRADO,
        SENHA_INCORRETA,
        USUARIO_INATIVO,
        USUARIO_SUSPENSO,
        /** RF03 Regra 1: conta temporariamente bloqueada por excesso de tentativas. */
        CONTA_BLOQUEADA
    }

    /**
     * RF03 – Autentica o usuário com proteção contra força bruta.
     *
     * Regra 1: bloqueia temporariamente após {@value MAX_TENTATIVAS} falhas.
     * Regra 2: compara a senha usando BCrypt (hash).
     *
     * @param email e-mail informado no formulário
     * @param senha senha em texto puro informada no formulário
     * @return ResultadoLogin indicando o desfecho
     */
    public ResultadoLogin autenticar(String email, String senha) {

        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            return ResultadoLogin.EMAIL_NAO_ENCONTRADO;
        }

        // RF03 Regra 1 – verifica bloqueio ativo
        if (usuario.estaBloqueado()) {
            return ResultadoLogin.CONTA_BLOQUEADA;
        }

        // Verifica status da conta antes de checar senha
        if (usuario.getStatus() == StatusUsuario.INATIVO) {
            return ResultadoLogin.USUARIO_INATIVO;
        }
        if (usuario.getStatus() == StatusUsuario.SUSPENSO) {
            return ResultadoLogin.USUARIO_SUSPENSO;
        }

        // RF03 Regra 2 – valida senha com BCrypt
        if (!encoder.matches(senha, usuario.getSenha())) {
            registrarFalha(usuario);
            return ResultadoLogin.SENHA_INCORRETA;
        }

        // Login bem-sucedido: zera contador de tentativas
        resetarContador(usuario);
        return ResultadoLogin.SUCESSO;
    }

    /**
     * Incrementa o contador de tentativas falhas.
     * Se atingir o limite, aplica bloqueio temporário de {@value MINUTOS_BLOQUEIO} min.
     */
    private void registrarFalha(Usuario usuario) {
        int novasTentativas = usuario.getTentativasLogin() + 1;
        usuario.setTentativasLogin(novasTentativas);

        if (novasTentativas >= MAX_TENTATIVAS) {
            usuario.setBloqueadoAte(LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEIO));
            usuario.setTentativasLogin(0); // reseta para o próximo ciclo
        }

        usuarioRepository.save(usuario);
    }

    /** Zera o contador de tentativas e remove o bloqueio após login bem-sucedido. */
    private void resetarContador(Usuario usuario) {
        if (usuario.getTentativasLogin() > 0 || usuario.getBloqueadoAte() != null) {
            usuario.setTentativasLogin(0);
            usuario.setBloqueadoAte(null);
            usuarioRepository.save(usuario);
        }
    }

    /** Mantido para compatibilidade com código legado. */
    public boolean login(String email, String senha) {
        return autenticar(email, senha) == ResultadoLogin.SUCESSO;
    }

    // ── Cadastro ──────────────────────────────────────────────────────────────

    /**
     * RF03 Regra 3 – valida força mínima da senha antes de salvar.
     *
     * Requisitos:
     *   - Mínimo 8 caracteres
     *   - Pelo menos 1 letra maiúscula
     *   - Pelo menos 1 letra minúscula
     *   - Pelo menos 1 número
     *   - Pelo menos 1 caractere especial (!@#$%^&*...)
     */
    public void cadastrar(Usuario usuario, Endereco endereco) {

        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new RuntimeException("CPF já cadastrado.");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado.");
        }

        // RF03 Regra 3 – força mínima
        validarForcaSenha(usuario.getSenha());

        // RF03 Regra 2 – armazena com BCrypt
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        usuario.setStatus(StatusUsuario.ATIVO);
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setTentativasLogin(0);

        Usuario salvo = usuarioRepository.save(usuario);

        endereco.setUsuario(salvo);
        enderecoRepository.save(endereco);
    }

    /**
     * Valida a força da senha conforme RF03 Regra 3.
     * Lança RuntimeException com mensagem descritiva se algum critério não for atendido.
     */
    public void validarForcaSenha(String senha) {
        if (senha == null || senha.length() < 8) {
            throw new RuntimeException("A senha deve ter pelo menos 8 caracteres.");
        }
        if (!senha.matches(".*[A-Z].*")) {
            throw new RuntimeException("A senha deve conter pelo menos uma letra maiúscula.");
        }
        if (!senha.matches(".*[a-z].*")) {
            throw new RuntimeException("A senha deve conter pelo menos uma letra minúscula.");
        }
        if (!senha.matches(".*[0-9].*")) {
            throw new RuntimeException("A senha deve conter pelo menos um número.");
        }
        if (!senha.matches(".*[!@#$%^&*()\\-_=+\\[\\]{};:'\",.<>/?\\\\|`~].*")) {
            throw new RuntimeException("A senha deve conter pelo menos um caractere especial (!@#$%...).");
        }
    }

    // ── Outros ───────────────────────────────────────────────────────────────

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
