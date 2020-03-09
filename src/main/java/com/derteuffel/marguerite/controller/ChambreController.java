package com.derteuffel.marguerite.controller;


import com.derteuffel.marguerite.domain.Chambre;
import com.derteuffel.marguerite.repository.ChambreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/hotel/chambres")
public class ChambreController {

    @Autowired
    private ChambreRepository chambreRepository;


    @GetMapping("/all")
    public String findAll(Model model){
        model.addAttribute("chambres", chambreRepository.findAll());
        return "chambres/all";
    }

    @GetMapping("/bedroom")
    public String getAll(Model model){
        model.addAttribute("chambres", chambreRepository.findAll());
        return "chambres/all-2";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("chambre", new Chambre());
        return "chambres/form";
    }

    @PostMapping("/save")
    public String save(Chambre chambre, RedirectAttributes redirectAttributes) {

        chambreRepository.save(chambre);
        redirectAttributes.addFlashAttribute("suuccess","You've been save your data successfully");
        return "redirect:/hotel/chambres/all";
    }

    @GetMapping("/detail/{id}")
    public String findById(@PathVariable Long id, Model model){

        Chambre chambre = chambreRepository.getOne(id);
        model.addAttribute("chambre", chambre);
        return "chambres/detail";
    }

    @GetMapping("/edit/{id}")
    public String updateForm(Model model, @PathVariable Long id) {
        Chambre chambre = chambreRepository.getOne(id);
        model.addAttribute("chambre", chambre);
        return "chambres/update";
    }

    @PostMapping("/update")
    public String update(Chambre chambre, RedirectAttributes redirectAttributes){

        chambreRepository.save(chambre);
        redirectAttributes.addFlashAttribute("suuccess","You've been save your data successfully");
        return "redirect:/hotel/chambres/all";

    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes){
        Chambre chambre = chambreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid chambre id:" +id));
        chambreRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success","You've delete data successfully");
        return "redirect:/hotel/chambres/all";
    }
}
