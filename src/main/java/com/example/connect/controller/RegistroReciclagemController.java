
package com.example.connect.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.connect.model.PontoColeta;
import com.example.connect.model.RegistroReciclagem;
import com.example.connect.repository.TipoMaterialRepository;
import com.example.connect.model.Usuario;
import com.example.connect.repository.PontoColetaRepository;
import com.example.connect.repository.UsuarioRepository;
import com.example.connect.service.RegistroReciclagemService;
import com.example.connect.model.StatusPontoColeta;
import com.example.connect.model.TipoUsuario;

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
    private TipoMaterialRepository tipoMaterialRepository; // <- adicione aqui

    @GetMapping("/registrar")
    public String telaRegistrar(Model model, HttpSession session) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null) return "redirect:/login";

        List<PontoColeta> pontosAtivos = pontoRepository.findByStatus(StatusPontoColeta.ATIVO);
        List<Usuario> catadores = usuarioRepository.findByTipo(TipoUsuario.CATADOR);

        model.addAttribute("registro", new RegistroReciclagem());
        model.addAttribute("pontos", pontosAtivos);
        model.addAttribute("catadores", catadores);
        model.addAttribute("materiais", tipoMaterialRepository.findAll()); // <- adicione aqui
        model.addAttribute("usuario", logado);

        return "reciclagem/registrar";
    }

    /**
     * RF05 - Regra 1: vinculado a usuário, ponto e catador.
     * RF05 - Regra 2: QR Code único gerado automaticamente.
     * RF05 - Regra 3: status PENDENTE ao criar.
     */
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

    /** Histórico de registros do usuário logado */
    @GetMapping("/meus-registros")
    public String meusRegistros(Model model, HttpSession session) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null) return "redirect:/login";

        model.addAttribute("registros",
                service.listarPorUsuario(logado.getIdUsuario()));

        return "reciclagem/meus-registros";
    }

    /** Listagem admin de todos os registros */
    @GetMapping("/admin/lista")
    public String listarAdmin(Model model, HttpSession session) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null) return "redirect:/login";
        if (logado.getTipo() != TipoUsuario.ADMINISTRADOR
                && logado.getTipo() != TipoUsuario.CATADOR) {
            return "redirect:/home";
        }

        model.addAttribute("registros", service.listarTodos());
        return "reciclagem/admin-lista";
    }
}
