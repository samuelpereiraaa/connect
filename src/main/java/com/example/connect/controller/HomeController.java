package com.example.connect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.connect.model.Usuario;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado != null) {
            return "redirect:/home";
        }
        return "redirect:/login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuarioLogado", usuario);
        return "home";
    }

    /** Regra de negócio: registro de reciclagem deve estar vinculado a usuário, ponto e catador. */
    @GetMapping("/regras/registro-reciclagem")
    public String regraRegistroReciclagem(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuarioLogado", usuario);
        return "regras/registro-reciclagem";
    }
}
