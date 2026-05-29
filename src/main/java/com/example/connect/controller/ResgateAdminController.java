package com.example.connect.controller;

import com.example.connect.model.TipoUsuario;
import com.example.connect.model.Usuario;
import com.example.connect.model.enums.StatusResgate;
import com.example.connect.service.ResgateService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * RF10 – Painel administrativo de resgates.
 *
 * Regra 3: pontos são descontados via trigger do banco ao aprovar.
 * Apenas ADMINISTRADOR tem acesso.
 */
@Controller
@RequestMapping("/admin/resgates")
public class ResgateAdminController {

    private final ResgateService resgateService;

    public ResgateAdminController(ResgateService resgateService) {
        this.resgateService = resgateService;
    }

    /**
     * RF10 – Lista todos os resgates com filtro opcional por status.
     */
    @GetMapping
    public String listar(@RequestParam(required = false) String status,
                         Model model,
                         HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        StatusResgate statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = StatusResgate.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) { }
        }

        model.addAttribute("resgates",
                statusEnum != null
                        ? resgateService.listarPorStatus(statusEnum)
                        : resgateService.listarTodos());
        model.addAttribute("filtrStatus",  status);
        model.addAttribute("statusOpcoes", StatusResgate.values());
        return "admin/resgates/listar";
    }

    /**
     * RF10 – Regra 3: aprovar resgate.
     * A trigger fn_debitar_pontos_resgate desconta os pontos automaticamente.
     */
    @PostMapping("/aprovar/{id}")
    public String aprovar(@PathVariable Integer id,
                          HttpSession session,
                          RedirectAttributes redirect) {
        if (!isAdmin(session)) return "redirect:/login";

        try {
            resgateService.aprovar(id);
            redirect.addFlashAttribute("sucesso",
                    "Resgate #" + id + " aprovado. Pontos debitados automaticamente.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/resgates";
    }

    /**
     * RF10 – Cancelar resgate pendente.
     */
    @PostMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Integer id,
                           HttpSession session,
                           RedirectAttributes redirect) {
        if (!isAdmin(session)) return "redirect:/login";

        try {
            resgateService.cancelar(id);
            redirect.addFlashAttribute("sucesso", "Resgate #" + id + " cancelado.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/resgates";
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private boolean isAdmin(HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        return logado != null && logado.getTipo() == TipoUsuario.ADMINISTRADOR;
    }
}
