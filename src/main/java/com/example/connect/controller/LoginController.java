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
 * Regra 1: bloqueia conta após 5 tentativas inválidas por 15 minutos.
 * Regra 2: senha validada com BCrypt.
 * Regra 3: mensagens de erro descritivas.
 */
@Controller
public class LoginController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String telaLogin(
            @RequestParam(required = false) String erro,
            @RequestParam(required = false) String mensagem,
            HttpSession session,
            Model model) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado != null) {
            return redirecionarPorTipo(logado.getTipo());
        }

        if (erro != null) {
            String textoErro = switch (erro) {
                case "email_nao_encontrado" -> "E-mail não encontrado. Verifique e tente novamente.";
                case "senha_incorreta"      -> "Senha incorreta. Verifique e tente novamente.";
                case "usuario_inativo"      -> "Sua conta está inativa. Entre em contato com o suporte.";
                case "usuario_suspenso"     -> "Sua conta está suspensa. Entre em contato com o suporte.";
                // RF03 Regra 1 – mensagem de bloqueio temporário
                case "conta_bloqueada"      -> "Conta bloqueada temporariamente por excesso de tentativas. Tente novamente em 15 minutos.";
                default                    -> "Credenciais inválidas. Tente novamente.";
            };
            model.addAttribute("erro", textoErro);
        }

        if ("logout_sucesso".equals(mensagem)) {
            model.addAttribute("mensagem", "Você saiu com sucesso.");
        }

        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String senha,
            HttpSession session) {

        ResultadoLogin resultado = service.autenticar(email, senha);

        if (resultado != ResultadoLogin.SUCESSO) {
            String codigoErro = switch (resultado) {
                case EMAIL_NAO_ENCONTRADO -> "email_nao_encontrado";
                case SENHA_INCORRETA      -> "senha_incorreta";
                case USUARIO_INATIVO      -> "usuario_inativo";
                case USUARIO_SUSPENSO     -> "usuario_suspenso";
                // RF03 Regra 1
                case CONTA_BLOQUEADA      -> "conta_bloqueada";
                default                  -> "credenciais_invalidas";
            };
            return "redirect:/login?erro=" + codigoErro;
        }

        Usuario usuario = usuarioRepository.findByEmail(email);
        session.setAttribute("usuarioLogado", usuario);

        return redirecionarPorTipo(usuario.getTipo());
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?mensagem=logout_sucesso";
    }

    private String redirecionarPorTipo(TipoUsuario tipo) {
        return switch (tipo) {
            case ADMINISTRADOR -> "redirect:/admin/home";
            case CATADOR       -> "redirect:/home";
            case USUARIO       -> "redirect:/home";
        };
    }
}
