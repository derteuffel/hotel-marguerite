package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.Compte;
import com.derteuffel.marguerite.domain.Role;
import com.derteuffel.marguerite.repository.RoleRepository;
import com.derteuffel.marguerite.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private CompteService compteService;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/")
    public String home(){

            return "index";
    }
}
