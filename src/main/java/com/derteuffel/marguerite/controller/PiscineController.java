package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.repository.PiscineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hotel/piscines")
public class PiscineController {

    @Autowired
    private PiscineRepository piscineRepository;


    @GetMapping("/all")
    public String all(Model model){
        model.addAttribute("lists",piscineRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));
        return "piscines/all";
    }
}
