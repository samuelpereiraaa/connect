package com.example.connect.controller;

import com.example.connect.model.TipoUsuario;
import com.example.connect.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminHomeController {

    @GetMapping("/admin/home")
    public String home(HttpSession session, Model model) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null || logado.getTipo() != TipoUsuario.ADMINISTRADOR) {
            return "redirect:/login";
        }
        model.addAttribute("usuarioLogado", logado);
        return "admin/home";
    }
}
