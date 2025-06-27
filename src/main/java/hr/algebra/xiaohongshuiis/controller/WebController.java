package hr.algebra.xiaohongshuiis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Dashboard - Xiaohongshu IIS");
        return "index";
    }

    @GetMapping("/xml-validation")
    public String xmlValidation(Model model) {
        model.addAttribute("title", "XML Validacija");
        return "xml-validation";
    }

    @GetMapping("/soap-search")
    public String soapSearch(Model model) {
        model.addAttribute("title", "SOAP Pretraživanje");
        return "soap-search";
    }

    @GetMapping("/weather")
    public String weather(Model model) {
        model.addAttribute("title", "Vrijeme XML-RPC");
        return "weather";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("title", "Upravljanje korisnicima");
        return "users";
    }
}