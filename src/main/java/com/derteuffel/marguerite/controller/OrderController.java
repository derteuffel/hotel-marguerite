package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.repository.CommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hotel")
public class OrderController {

    @Autowired
    private CommandeRepository commandeRepository;


    @GetMapping("/order")
    public String findAll(Model model){
        //model.addAttribute("commandes", commandeRepository.findAllByNumTable(numTable));
        model.addAttribute("commandes", commandeRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));
        return "commandes/all-2";
    }
}
