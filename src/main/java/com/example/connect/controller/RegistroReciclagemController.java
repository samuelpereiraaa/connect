package com.example.connect.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.connect.model.PontoColeta;
import com.example.connect.model.RegistroReciclagem;
import com.example.connect.model.StatusReciclagem;
import com.example.connect.model.TipoUsuario;
import com.example.connect.model.Usuario;
import com.example.connect.repository.PontoColetaRepository;
import com.example.connect.repository.TipoMaterialRepository;
import com.example.connect.repository.UsuarioRepository;
import com.example.connect.service.RegistroReciclagemService;
import com.example.connect.model.StatusPontoColeta;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/reciclagem")
public class RegistroReciclagemController {

    @Autowired
    private RegistroReciclagemService service;

    @Autowired
    private PontoColetaRepository pontoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoMaterialRepository tipoMaterialRepository;

    @GetMapping("/registrar")
    public String telaRegistrar(Model model, HttpSession session) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null) return "redirect:/login";

        List<PontoColeta> pontosAtivos = pontoRepository.findByStatus(StatusPontoColeta.ATIVO);
        List<Usuario> catadores = usuarioRepository.findByTipo(TipoUsuario.CATADOR);

        model.addAttribute("registro", new RegistroReciclagem());
        model.addAttribute("pontos", pontosAtivos);
        model.addAttribute("catadores", catadores);
        model.addAttribute("materiais", tipoMaterialRepository.findAll());
        model.addAttribute("usuario", logado);

        return "reciclagem/registrar";
    }

 
    @PostMapping("/registrar")
    public String registrar(
            @ModelAttribute RegistroReciclagem registro,
            HttpSession session,
            RedirectAttributes redirect) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null) return "redirect:/login";

        // RF05 - Regra 1: vincula o usuário logado
        registro.setUsuario(logado);

        try {
            service.registrar(registro);
            redirect.addFlashAttribute("sucesso", "Reciclagem registrada com sucesso! Aguardando validação.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/reciclagem/registrar";
        }

        return "redirect:/home";
    }

   
    @GetMapping("/meus-registros")
    public String meusRegistros(Model model, HttpSession session) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null) return "redirect:/login";

        model.addAttribute("registros",
                service.listarPorUsuario(logado.getIdUsuario()));

        return "reciclagem/meus-registros";
    }


    @GetMapping("/admin/lista")
    public String listarAdmin(
            @RequestParam(required = false) StatusReciclagem filtroStatus,
            Model model,
            HttpSession session) {

        if (!isCatadorOuAdmin(session)) return "redirect:/home";

        List<RegistroReciclagem> registros = (filtroStatus != null)
                ? service.listarTodos().stream()
                    .filter(r -> r.getStatus() == filtroStatus)
                    .toList()
                : service.listarTodos();

        model.addAttribute("registros", registros);
        model.addAttribute("statusOpcoes", StatusReciclagem.values());
        model.addAttribute("filtroStatus", filtroStatus);

        return "reciclagem/admin-lista";
    }


    @PostMapping("/admin/validar/{id}")
    public String validar(
            @PathVariable Integer id,
            @RequestParam StatusReciclagem novoStatus,
            @RequestParam(required = false) String observacao,
            HttpSession session,
            RedirectAttributes redirect) {

        if (!isCatadorOuAdmin(session)) return "redirect:/home";

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        try {
            service.validar(id, novoStatus, observacao, logado);
            String acao = novoStatus == StatusReciclagem.VALIDADO ? "validado" : "recusado";
            redirect.addFlashAttribute("sucesso", "Registro #" + id + " " + acao + " com sucesso.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/reciclagem/admin/lista";
    }

   
    private boolean isCatadorOuAdmin(HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        return logado != null
                && (logado.getTipo() == TipoUsuario.ADMINISTRADOR
                    || logado.getTipo() == TipoUsuario.CATADOR);
    }
}