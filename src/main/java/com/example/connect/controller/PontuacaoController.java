package com.example.connect.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.connect.model.HistoricoPontuacao;
import com.example.connect.model.Pontuacao;
import com.example.connect.model.Usuario;
import com.example.connect.service.PontuacaoService;

import jakarta.servlet.http.HttpSession;

/**
 * RF07 – Gerenciamento de Pontuação (cálculo automático via trigger).
 * RF08 – Consulta de Pontuação: visualização de pontos, nível e histórico.
 *
 * Regra 1 (RF08): o usuário visualiza seus pontos e nível.
 * Regra 2 (RF08): permite consulta histórica com filtro por período e tipo.
 *
 * Controle de acesso via HttpSession — padrão do projeto.
 */
@Controller
@RequestMapping("/pontuacao")
public class PontuacaoController {

    @Autowired
    private PontuacaoService pontuacaoService;

    // -------------------------------------------------------------------------
    // RF08 – Regra 1: painel de pontuação (saldo + nível + histórico completo)
    // -------------------------------------------------------------------------

    /**
     * GET /pontuacao
     * Exibe saldo atual, nível e histórico completo do usuário logado.
     */
    @GetMapping
    public String minhaPontuacao(HttpSession session, Model model) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null) {
            return "redirect:/login";
        }

        // RF08 – Regra 1: pontos e nível do usuário
        Pontuacao pontuacao = pontuacaoService.buscarPorUsuario(logado.getIdUsuario());

        // RF08 – Regra 2: histórico completo na carga inicial
        List<HistoricoPontuacao> historico =
                pontuacaoService.buscarHistorico(logado.getIdUsuario());

        model.addAttribute("usuarioLogado", logado);
        model.addAttribute("pontuacao", pontuacao);
        model.addAttribute("historico", historico);

        // Parâmetros de filtro em branco para o formulário inicial
        model.addAttribute("dataInicio", null);
        model.addAttribute("dataFim", null);
        model.addAttribute("tipo", "TODOS");

        return "pontuacao/minha-pontuacao";
    }

    // -------------------------------------------------------------------------
    // RF08 – Regra 2: consulta histórica com filtros
    // -------------------------------------------------------------------------

    /**
     * GET /pontuacao/historico
     * Filtra o histórico de lançamentos por período e/ou tipo de operação.
     *
     * @param dataInicio  Data inicial (formato yyyy-MM-dd); opcional
     * @param dataFim     Data final   (formato yyyy-MM-dd); opcional
     * @param tipo        "CREDITO", "DEBITO" ou "TODOS"; opcional
     */
    @GetMapping("/historico")
    public String consultarHistorico(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false, defaultValue = "TODOS") String tipo,
            HttpSession session,
            Model model) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null) {
            return "redirect:/login";
        }

        // RF08 – Regra 1: pontos e nível sempre visíveis no topo
        Pontuacao pontuacao = pontuacaoService.buscarPorUsuario(logado.getIdUsuario());

        // RF08 – Regra 2: histórico filtrado
        List<HistoricoPontuacao> historico =
                pontuacaoService.consultarHistorico(
                        logado.getIdUsuario(), dataInicio, dataFim, tipo);

        model.addAttribute("usuarioLogado", logado);
        model.addAttribute("pontuacao", pontuacao);
        model.addAttribute("historico", historico);

        // Devolve os filtros ao formulário para manter o estado na view
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("tipo", tipo);

        return "pontuacao/minha-pontuacao";
    }
}