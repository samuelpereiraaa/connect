package com.example.connect.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.connect.model.HistoricoUsuario;
import com.example.connect.model.StatusUsuario;
import com.example.connect.model.TipoUsuario;
import com.example.connect.model.Usuario;
import com.example.connect.repository.UsuarioRepository;
import com.example.connect.service.GerenciamentoUsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/usuarios")
public class GerenciamentoUsuarioController {

    @Autowired
    private GerenciamentoUsuarioService gerenciamentoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // RF02 - Lista de usuários com filtros
    @GetMapping
    public String listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) TipoUsuario tipo,
            @RequestParam(required = false) StatusUsuario status,
            Model model,
            HttpSession session) {

        if (!isAdmin(session)) return "redirect:/login";

        List<Usuario> usuarios = gerenciamentoService.buscar(nome, email, tipo, status);

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("tipos", TipoUsuario.values());
        model.addAttribute("statuses", StatusUsuario.values());
        model.addAttribute("filtrNome", nome);
        model.addAttribute("filtrEmail", email);
        model.addAttribute("filtrTipo", tipo);
        model.addAttribute("filtrStatus", status);

        return "admin/usuarios-lista";
    }

    // RF02 - Tela de edição de dados do usuário
    @GetMapping("/editar/{id}")
    public String telaEditar(@PathVariable Integer id, Model model, HttpSession session) {

        if (!isAdmin(session)) return "redirect:/login";

        Usuario usuario = usuarioRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        model.addAttribute("usuario", usuario);
        model.addAttribute("tipos", TipoUsuario.values());

        return "admin/usuario-editar";
    }

    // RF02 - Salvar alteração de dados
    @PostMapping("/editar/{id}")
    public String salvarEdicao(
            @PathVariable Integer id,
            @ModelAttribute Usuario dadosNovos,
            HttpSession session,
            RedirectAttributes redirect) {

        if (!isAdmin(session)) return "redirect:/login";

        try {
            Usuario admin = (Usuario) session.getAttribute("usuarioLogado");
            gerenciamentoService.alterar(id, dadosNovos, admin);
            redirect.addFlashAttribute("sucesso", "Usuário atualizado com sucesso.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    // RF02 - Alterar status (ativar / inativar / suspender)
    @PostMapping("/status/{id}")
    public String alterarStatus(
            @PathVariable Integer id,
            @RequestParam StatusUsuario novoStatus,
            HttpSession session,
            RedirectAttributes redirect) {

        if (!isAdmin(session)) return "redirect:/login";

        try {
            Usuario admin = (Usuario) session.getAttribute("usuarioLogado");
            gerenciamentoService.alterarStatus(id, novoStatus, admin);
            redirect.addFlashAttribute("sucesso", "Status atualizado para " + novoStatus.name() + ".");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    // RF02 - Histórico de alterações de um usuário
    @GetMapping("/historico/{id}")
    public String verHistorico(@PathVariable Integer id, Model model, HttpSession session) {

        if (!isAdmin(session)) return "redirect:/login";

        Usuario usuario = usuarioRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<HistoricoUsuario> historico = gerenciamentoService.buscarHistorico(id);

        model.addAttribute("usuario", usuario);
        model.addAttribute("historico", historico);

        return "admin/usuario-historico";
    }

    // Valida se o usuário logado é administrador (RS003)
    private boolean isAdmin(HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        return logado != null && logado.getTipo() == TipoUsuario.ADMINISTRADOR;
    }
}
