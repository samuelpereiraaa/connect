package com.example.connect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.connect.model.TipoUsuario;
import com.example.connect.model.Usuario;
import com.example.connect.repository.UsuarioRepository;
import com.example.connect.service.UsuarioService;
import com.example.connect.service.UsuarioService.ResultadoLogin;

import jakarta.servlet.http.HttpSession;

/**
 * RF03 – Autenticação de usuários.
 * Regra 1: valida e-mail e senha.
 * Regra 2: redireciona conforme tipo de usuário.
 * Regra 3: em caso de erro apresenta mensagem.
 */
@Controller
public class LoginController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /** RF03 – Exibe a tela de login */
    @GetMapping("/login")
    public String telaLogin(
            @RequestParam(required = false) String erro,
            @RequestParam(required = false) String mensagem,
            HttpSession session,
            Model model) {

        // Se já está logado, redireciona direto
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado != null) {
            return redirecionarPorTipo(logado.getTipo());
        }

        // RF03 Regra 3 – traduz o código de erro em mensagem legível
        if (erro != null) {
            String textoErro = switch (erro) {
                case "email_nao_encontrado" -> "E-mail não encontrado. Verifique e tente novamente.";
                case "senha_incorreta"      -> "Senha incorreta. Verifique e tente novamente.";
                case "usuario_inativo"      -> "Sua conta está inativa. Entre em contato com o suporte.";
                case "usuario_suspenso"     -> "Sua conta está suspensa. Entre em contato com o suporte.";
                default                    -> "Credenciais inválidas. Tente novamente.";
            };
            model.addAttribute("erro", textoErro);
        }

        if ("logout_sucesso".equals(mensagem)) {
            model.addAttribute("mensagem", "Você saiu com sucesso.");
        }

        return "login";
    }

    /** RF03 – Processa o formulário de login */
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String senha,
            HttpSession session) {

        // RF03 Regra 1 – valida e-mail e senha
        ResultadoLogin resultado = service.autenticar(email, senha);

        // RF03 Regra 3 – em caso de erro redireciona com código
        if (resultado != ResultadoLogin.SUCESSO) {
            String codigoErro = switch (resultado) {
                case EMAIL_NAO_ENCONTRADO -> "email_nao_encontrado";
                case SENHA_INCORRETA      -> "senha_incorreta";
                case USUARIO_INATIVO      -> "usuario_inativo";
                case USUARIO_SUSPENSO     -> "usuario_suspenso";
                default                  -> "credenciais_invalidas";
            };
            return "redirect:/login?erro=" + codigoErro;
        }

        // RF03 Regra 2 – armazena usuário na sessão
        Usuario usuario = usuarioRepository.findByEmail(email);
        session.setAttribute("usuarioLogado", usuario);

        // RF03 Regra 2 – redireciona conforme tipo
        return redirecionarPorTipo(usuario.getTipo());
    }

    /** GET /logout – encerra a sessão */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?mensagem=logout_sucesso";
    }

    private String redirecionarPorTipo(TipoUsuario tipo) {
        return switch (tipo) {
            case ADMINISTRADOR -> "redirect:/admin/usuarios";
            case CATADOR       -> "redirect:/home";
            case USUARIO       -> "redirect:/home";
        };
    }
}
