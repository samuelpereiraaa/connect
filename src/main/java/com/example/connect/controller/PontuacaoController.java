package com.example.connect.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.connect.model.HistoricoPontuacao;
import com.example.connect.model.Pontuacao;
import com.example.connect.model.Usuario;
import com.example.connect.service.PontuacaoService;

import jakarta.servlet.http.HttpSession;

/**
 * RF07 – Controller de Gerenciamento de Pontuação.
 *
 * Regra 1: pontuação calculada automaticamente pela trigger do banco.
 * Regra 2: exibe saldo e histórico atualizados conforme registros validados.
 *
 * Controle de acesso via HttpSession — padrão do projeto.
 */
@Controller
@RequestMapping("/pontuacao")
public class PontuacaoController {

    @Autowired
    private PontuacaoService pontuacaoService;

    // -------------------------------------------------------------------------
    // RF07 – Painel de pontuação do usuário logado
    // -------------------------------------------------------------------------

    /**
     * GET /pontuacao
     * Exibe o saldo atual, nível e histórico de lançamentos do usuário logado.
     */
    @GetMapping
    public String minhaPontuacao(HttpSession session, Model model) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null) {
            return "redirect:/login";
        }

        // RF07 – Regra 1 e 2: busca pontuação calculada pelo banco
        Pontuacao pontuacao = pontuacaoService.buscarPorUsuario(logado.getIdUsuario());

        // RF07 – Regra 2: histórico de créditos e débitos
        List<HistoricoPontuacao> historico =
                pontuacaoService.buscarHistorico(logado.getIdUsuario());

        model.addAttribute("usuarioLogado", logado);
        model.addAttribute("pontuacao", pontuacao);
        model.addAttribute("historico", historico);

        return "pontuacao/minha-pontuacao";
    }
}
