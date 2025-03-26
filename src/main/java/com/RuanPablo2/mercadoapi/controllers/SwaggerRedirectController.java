package com.RuanPablo2.mercadoapi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerRedirectController {

    @GetMapping({"", "/api"})
    public String redirectToSwagger() {
        return "redirect:/swagger-ui/index.html";
    }
}