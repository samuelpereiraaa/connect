package com.example.connect.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.connect.model.EnderecoPonto;
import com.example.connect.model.PontoColeta;
import com.example.connect.model.StatusPontoColeta;
import com.example.connect.model.TipoUsuario;
import com.example.connect.model.Usuario;
import com.example.connect.service.PontoColetaService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/pontos")
public class PontoColetaController {

    @Autowired
    private PontoColetaService pontoService;

    /** RF04 - Regra 2: consulta/listagem */
    @GetMapping
    public String listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String status,
            Model model,
            HttpSession session) {

        if (!isAdmin(session)) return "redirect:/login";

        List<PontoColeta> pontos;

        if (nome != null && !nome.isBlank()) {
            pontos = pontoService.buscarPorNome(nome);
        } else {
            pontos = pontoService.listarTodos();
        }

        if (status != null && !status.isBlank()) {
            StatusPontoColeta st = StatusPontoColeta.valueOf(status);
            pontos = pontos.stream()
                    .filter(p -> p.getStatus() == st)
                    .toList();
        }

        model.addAttribute("pontos", pontos);
        model.addAttribute("filtrNome", nome);
        model.addAttribute("filtrStatus", status);
        model.addAttribute("statusOpcoes", StatusPontoColeta.values());
        return "admin/pontos-lista";
    }

    /** RF04 - Regra 2: tela de cadastro */
    @GetMapping("/novo")
    public String telaCadastro(Model model, HttpSession session) {

        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("ponto", new PontoColeta());
        model.addAttribute("endereco", new EnderecoPonto());
        model.addAttribute("statusOpcoes", StatusPontoColeta.values());
        return "admin/ponto-form";
    }

    /** RF04 - Regra 2: salvar novo ponto com endereço vinculado */
    @PostMapping("/novo")
    public String salvarNovo(
            @ModelAttribute PontoColeta ponto,
            @ModelAttribute EnderecoPonto endereco,
            HttpSession session,
            RedirectAttributes redirect) {

        if (!isAdmin(session)) return "redirect:/login";

        try {
            pontoService.cadastrar(ponto, endereco);
            redirect.addFlashAttribute("sucesso", "Ponto cadastrado com sucesso.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/admin/pontos/novo";
        }

        return "redirect:/admin/pontos";
    }

    /** RF04 - Regra 2: tela de edição */
    @GetMapping("/editar/{id}")
    public String telaEditar(
            @PathVariable Integer id,
            Model model,
            HttpSession session) {

        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("ponto", pontoService.buscarPorId(id));
        model.addAttribute("endereco", pontoService.buscarEnderecoPorPonto(id));
        model.addAttribute("statusOpcoes", StatusPontoColeta.values());
        return "admin/ponto-form";
    }

    /** RF04 - Regra 2: salvar edição */
    @PostMapping("/editar/{id}")
    public String salvarEdicao(
            @PathVariable Integer id,
            @ModelAttribute PontoColeta ponto,
            @ModelAttribute EnderecoPonto endereco,
            HttpSession session,
            RedirectAttributes redirect) {

        if (!isAdmin(session)) return "redirect:/login";

        try {
            pontoService.alterar(id, ponto, endereco);
            redirect.addFlashAttribute("sucesso", "Ponto atualizado com sucesso.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/admin/pontos";
    }

    @PostMapping("/status/{id}")
    public String alterarStatus(
            @PathVariable Integer id,
            @RequestParam String novoStatus,
            HttpSession session,
            RedirectAttributes redirect) {

        if (!isAdmin(session)) return "redirect:/login";

        try {
            pontoService.alterarStatus(id, novoStatus);
            redirect.addFlashAttribute("sucesso", "Status atualizado.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", "Status inválido: " + novoStatus);
        }

        return "redirect:/admin/pontos";
    }

    private boolean isAdmin(HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        return logado != null && logado.getTipo() == TipoUsuario.ADMINISTRADOR;
    }
}
