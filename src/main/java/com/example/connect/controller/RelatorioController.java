package com.example.connect.controller;

import com.example.connect.model.Administrador;
import com.example.connect.model.Relatorio;
import com.example.connect.model.TipoUsuario;
import com.example.connect.model.Usuario;
import com.example.connect.service.RelatorioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * RF11 – Controller de Relatórios e Controle Administrativo.
 *
 * Rotas:
 *   GET  /admin/relatorios            → lista relatórios com filtros opcionais
 *   GET  /admin/relatorios/gerar      → formulário de geração
 *   POST /admin/relatorios/gerar      → processa geração do relatório
 *   GET  /admin/relatorios/{id}       → detalhe do relatório
 *   POST /admin/relatorios/{id}/excluir → exclui (nível 3+)
 */
@Controller
@RequestMapping("/admin/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    // ── Guarda de sessão ──────────────────────────────────────────────────────

    /**
     * Retorna o usuário logado se for ADMINISTRADOR; redireciona caso contrário.
     * Retorna null como sinal de redirecionamento (tratado no caller).
     */
    private Usuario getAdmin(HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null || logado.getTipo() != TipoUsuario.ADMINISTRADOR) {
            return null;
        }
        return logado;
    }

    // ── Listar ────────────────────────────────────────────────────────────────

    /**
     * RF11-1: lista relatórios com filtro opcional por tipo e período.
     */
    @GetMapping
    public String listar(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            HttpSession session,
            Model model) {

        Usuario logado = getAdmin(session);
        if (logado == null) return "redirect:/login";

        // RF11-3: obtém o admin para exibir seu nível na view
        Administrador admin = relatorioService.obterAdmin(logado);

        List<Relatorio> relatorios = relatorioService.listar(tipo, dataInicio, dataFim);

        model.addAttribute("usuarioLogado", logado);
        model.addAttribute("admin", admin);
        model.addAttribute("relatorios", relatorios);
        model.addAttribute("filtroTipo", tipo);
        model.addAttribute("filtroInicio", dataInicio);
        model.addAttribute("filtroFim", dataFim);

        return "relatorio/listar";
    }

    // ── Formulário de geração ─────────────────────────────────────────────────

    @GetMapping("/gerar")
    public String formGerar(HttpSession session, Model model) {
        Usuario logado = getAdmin(session);
        if (logado == null) return "redirect:/login";

        Administrador admin = relatorioService.obterAdmin(logado);

        // RF11-3: apenas nível 2+ pode gerar relatórios
        if (admin.getNivelAcesso() < 2) {
            model.addAttribute("erro", "Seu nível de acesso não permite gerar relatórios.");
            return "redirect:/admin/relatorios";
        }

        model.addAttribute("usuarioLogado", logado);
        model.addAttribute("admin", admin);
        model.addAttribute("hoje", LocalDate.now());
        return "relatorio/form-gerar";
    }

    // ── Processar geração ─────────────────────────────────────────────────────

    /**
     * RF11-1: gera relatório para o período informado.
     * RF11-2: vincula ao administrador da sessão.
     * RF11-3: requer nível >= 2.
     */
    @PostMapping("/gerar")
    public String gerar(
            @RequestParam String tipo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            HttpSession session,
            RedirectAttributes redirect) {

        Usuario logado = getAdmin(session);
        if (logado == null) return "redirect:/login";

        try {
            Administrador admin = relatorioService.obterAdmin(logado);
            relatorioService.verificarNivel(admin, 2); // RF11-3

            Relatorio gerado = relatorioService.gerarRelatorio(tipo, dataInicio, dataFim, admin);
            redirect.addFlashAttribute("sucesso",
                    "Relatório #" + gerado.getIdRelatorio() + " gerado com sucesso!");
        } catch (SecurityException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", "Erro inesperado ao gerar relatório: " + e.getMessage());
        }

        return "redirect:/admin/relatorios";
    }

    // ── Detalhe ───────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Integer id, HttpSession session, Model model) {
        Usuario logado = getAdmin(session);
        if (logado == null) return "redirect:/login";

        Administrador admin = relatorioService.obterAdmin(logado);

        try {
            Relatorio relatorio = relatorioService.buscarPorId(id);
            model.addAttribute("usuarioLogado", logado);
            model.addAttribute("admin", admin);
            model.addAttribute("relatorio", relatorio);
            return "relatorio/detalhe";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "redirect:/admin/relatorios";
        }
    }

    // ── Excluir ───────────────────────────────────────────────────────────────

    /**
     * RF11-3: exclui relatório — requer nível >= 3.
     */
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Integer id, HttpSession session, RedirectAttributes redirect) {
        Usuario logado = getAdmin(session);
        if (logado == null) return "redirect:/login";

        try {
            Administrador admin = relatorioService.obterAdmin(logado);
            relatorioService.excluir(id, admin);
            redirect.addFlashAttribute("sucesso", "Relatório #" + id + " excluído.");
        } catch (SecurityException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/admin/relatorios";
    }
}
