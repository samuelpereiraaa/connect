package com.example.connect.controller;

import com.example.connect.model.Pontuacao;
import com.example.connect.model.Usuario;
import com.example.connect.service.PontuacaoService;
import com.example.connect.service.RecompensaService;
import com.example.connect.model.Resgate;
import com.example.connect.service.ResgateService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * RF10 – Controller público de recompensas e resgates.
 */
@Controller
@RequestMapping("/recompensas")
public class RecompensaPublicController {

    private final RecompensaService recompensaService;
    private final ResgateService    resgateService;
    private final PontuacaoService  pontuacaoService;

    public RecompensaPublicController(RecompensaService recompensaService,
                                      ResgateService resgateService,
                                      PontuacaoService pontuacaoService) {
        this.recompensaService = recompensaService;
        this.resgateService    = resgateService;
        this.pontuacaoService  = pontuacaoService;
    }

    /**
     * RF10 – Lista recompensas ativas disponíveis para o usuário.
     * Passa o saldo atual para que a view possa indicar se o usuário
     * tem pontos suficientes para cada recompensa (Regra 1).
     */
    @GetMapping
    public String listarDisponiveis(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) return "redirect:/login";

        Pontuacao pontuacao = pontuacaoService.buscarPorUsuario(usuario.getIdUsuario());

        model.addAttribute("usuarioLogado", usuario);
        model.addAttribute("recompensas",   recompensaService.listarAtivas());
        model.addAttribute("saldoAtual",    pontuacao.getPontosTotal());
        return "recompensas/listar";
    }

    /**
     * RF10 – Solicita o resgate de uma recompensa.
     * Regra 1: valida pontos no service.
     * Regra 2: registra o resgate.
     */
    @PostMapping("/resgatar/{id}")
    public String resgatar(@PathVariable Integer id,
                           HttpSession session,
                           RedirectAttributes redirect) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) return "redirect:/login";

        try {
            resgateService.resgatar(id, usuario);
            redirect.addFlashAttribute("sucesso",
                    "Resgate solicitado com sucesso! Aguarde a aprovação do administrador.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/recompensas";
    }

    /**
     * RF10 – Exibe os resgates do usuário logado (Regra 2: registro visível).
     */
    @GetMapping("/meus-resgates")
    public String meusResgates(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuarioLogado", usuario);
        model.addAttribute("resgates",
                resgateService.listarPorUsuario(usuario.getIdUsuario()));
        return "recompensas/meus-resgates";
    }

    /**
     * RF10 – Permite ao próprio usuário cancelar um resgate SOLICITADO.
     */
    @PostMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Integer id,
                           HttpSession session,
                           RedirectAttributes redirect) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) return "redirect:/login";

        try {
            Resgate resgate = resgateService.buscarPorId(id);
            // Garante que o usuário só cancela seus próprios resgates
            if (!resgate.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                redirect.addFlashAttribute("erro", "Acesso negado.");
                return "redirect:/recompensas/meus-resgates";
            }
            resgateService.cancelar(id);
            redirect.addFlashAttribute("sucesso", "Resgate cancelado com sucesso.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/recompensas/meus-resgates";
    }
}
