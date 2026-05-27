package com.example.connect.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.connect.model.RegistroReciclagem;
import com.example.connect.model.StatusReciclagem;
import com.example.connect.model.TipoUsuario;
import com.example.connect.model.Usuario;
import com.example.connect.service.ValidacaoReciclagemService;

import jakarta.servlet.http.HttpSession;

/**
 * RF06 - Validação de Reciclagem
 *
 * Regra 1: O catador deve validar ou recusar o registro.
 * Regra 2: O status deve ser atualizado após validação.
 * Regra 3: O sistema deve registrar data e responsável pela validação.
 */
@Controller
@RequestMapping("/validacao")
public class ValidacaoReciclagemController {

    @Autowired
    private ValidacaoReciclagemService validacaoService;

    // -------------------------------------------------------------------------
    // RF06 - Regra 1: tela de pendentes
    // -------------------------------------------------------------------------

    /**
     * Lista os registros PENDENTES de validação.
     * CATADOR vê somente os registros vinculados a ele.
     * ADMINISTRADOR vê todos os pendentes.
     */
    @GetMapping("/pendentes")
    public String pendentes(Model model, HttpSession session) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (!isCatadorOuAdmin(logado)) return "redirect:/home";

        List<RegistroReciclagem> pendentes = validacaoService.listarPendentes(logado);

        model.addAttribute("pendentes", pendentes);
        model.addAttribute("usuario", logado);
        model.addAttribute("totalPendentes", pendentes.size());

        return "reciclagem/validacao-pendentes";
    }

    // -------------------------------------------------------------------------
    // RF06 - Regras 1, 2 e 3: executar validação
    // -------------------------------------------------------------------------

    /**
     * Processa a validação ou recusa de um registro PENDENTE.
     *
     * RF06 - Regra 1: verifica se o responsável é catador ou admin.
     * RF06 - Regra 2: atualiza o status para VALIDADO ou RECUSADO.
     * RF06 - Regra 3: registra data e responsável automaticamente no service.
     */
    @PostMapping("/avaliar/{id}")
    public String avaliar(
            @PathVariable Integer id,
            @RequestParam StatusReciclagem novoStatus,
            @RequestParam(required = false) String observacao,
            HttpSession session,
            RedirectAttributes redirect) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (!isCatadorOuAdmin(logado)) return "redirect:/home";

        try {
            // RF06 - Regras 1, 2 e 3 aplicadas no service
            validacaoService.validar(id, novoStatus, observacao, logado);

            String acao = novoStatus == StatusReciclagem.VALIDADO ? "validado" : "recusado";
            redirect.addFlashAttribute("sucesso",
                "Registro #" + id + " " + acao + " com sucesso.");

        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/validacao/pendentes";
    }

    // -------------------------------------------------------------------------
    // RF06 - Regra 3: histórico de validações realizadas
    // -------------------------------------------------------------------------

    /**
     * Exibe o histórico de registros já avaliados pelo catador/admin logado.
     * RF06 - Regra 3: comprova que data e responsável foram registrados.
     */
    @GetMapping("/historico")
    public String historico(Model model, HttpSession session) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (!isCatadorOuAdmin(logado)) return "redirect:/home";

        List<RegistroReciclagem> historico =
                validacaoService.listarHistoricoValidacoes(logado);

        model.addAttribute("historico", historico);
        model.addAttribute("usuario", logado);

        return "reciclagem/validacao-historico";
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private boolean isCatadorOuAdmin(Usuario usuario) {
        return usuario != null
                && (usuario.getTipo() == TipoUsuario.CATADOR
                    || usuario.getTipo() == TipoUsuario.ADMINISTRADOR);
    }
}
