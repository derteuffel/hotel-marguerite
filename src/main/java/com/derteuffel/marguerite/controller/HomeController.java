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
    public String home(HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        request.getSession().setAttribute("compte",compte);
        if (compte.getRoles().size() <= 1 && compte.getRoles().contains(roleRepository.findByName("ROLE_RECEIP"))){
            return "redirect:/hotel/reservations/reservation";
        }else if (compte.getRoles().size() <= 1 && compte.getRoles().contains(roleRepository.findByName("SELLER"))){
            return "redirect:/hotel/commandes/orders";
        }else {
            return "index";
        }
    }
}
