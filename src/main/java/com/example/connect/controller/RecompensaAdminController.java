package com.example.connect.controller;

import com.example.connect.model.Recompensa;
import com.example.connect.model.StatusRecompensa;
import com.example.connect.model.TipoUsuario;
import com.example.connect.model.Usuario;
import com.example.connect.service.RecompensaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/recompensas")
public class RecompensaAdminController {

    private final RecompensaService recompensaService;

    public RecompensaAdminController(RecompensaService recompensaService) {
        this.recompensaService = recompensaService;
    }

    /**
     * RF09 – Regra 1: consulta/listagem com filtros opcionais de nome e status.
     * RF09 – Regra 2: apenas administradores.
     */
    @GetMapping
    public String listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String status,
            Model model,
            HttpSession session) {

        if (!isAdmin(session)) return "redirect:/login";

        StatusRecompensa statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = StatusRecompensa.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // valor inválido na URL: ignora o filtro de status
            }
        }

        model.addAttribute("recompensas", recompensaService.listarComFiltros(nome, statusEnum));
        model.addAttribute("filtrNome",   nome);
        model.addAttribute("filtrStatus", status);
        model.addAttribute("statusOpcoes", StatusRecompensa.values());
        return "admin/recompensas/listar";
    }

    /**
     * RF09 – Regra 1: tela de cadastro.
     * RF09 – Regra 2: apenas administradores.
     */
    @GetMapping("/nova")
    public String telaCadastro(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("recompensa", new Recompensa());
        model.addAttribute("statusOpcoes", StatusRecompensa.values());
        return "admin/recompensas/forms";
    }

    /**
     * RF09 – Regra 1: salvar nova recompensa.
     */
    @PostMapping("/nova")
    public String salvarNova(
            @ModelAttribute Recompensa recompensa,
            HttpSession session,
            RedirectAttributes redirect) {

        if (!isAdmin(session)) return "redirect:/login";

        try {
            recompensaService.cadastrar(recompensa);
            redirect.addFlashAttribute("sucesso", "Recompensa cadastrada com sucesso.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/admin/recompensas/nova";
        }

        return "redirect:/admin/recompensas";
    }

    /**
     * RF09 – Regra 1: tela de edição.
     * RF09 – Regra 2: apenas administradores.
     */
    @GetMapping("/editar/{id}")
    public String telaEditar(
            @PathVariable Integer id,
            Model model,
            HttpSession session) {

        if (!isAdmin(session)) return "redirect:/login";

        Recompensa recompensa = recompensaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Recompensa não encontrada."));

        model.addAttribute("recompensa", recompensa);
        model.addAttribute("statusOpcoes", StatusRecompensa.values());
        return "admin/recompensas/forms";
    }

    /**
     * RF09 – Regra 1: salvar alteração.
     */
    @PostMapping("/editar/{id}")
    public String salvarEdicao(
            @PathVariable Integer id,
            @ModelAttribute Recompensa recompensa,
            HttpSession session,
            RedirectAttributes redirect) {

        if (!isAdmin(session)) return "redirect:/login";

        try {
            recompensaService.alterar(id, recompensa);
            redirect.addFlashAttribute("sucesso", "Recompensa atualizada com sucesso.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/admin/recompensas";
    }

    /**
     * RF09 – altera apenas o status da recompensa (ativar / inativar / esgotar).
     */
    @PostMapping("/status/{id}")
    public String alterarStatus(
            @PathVariable Integer id,
            @RequestParam String novoStatus,
            HttpSession session,
            RedirectAttributes redirect) {

        if (!isAdmin(session)) return "redirect:/login";

        try {
            recompensaService.alterarStatus(id, novoStatus);
            redirect.addFlashAttribute("sucesso", "Status atualizado com sucesso.");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", "Status inválido: " + novoStatus);
        }

        return "redirect:/admin/recompensas";
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private boolean isAdmin(HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        return logado != null && logado.getTipo() == TipoUsuario.ADMINISTRADOR;
    }
}