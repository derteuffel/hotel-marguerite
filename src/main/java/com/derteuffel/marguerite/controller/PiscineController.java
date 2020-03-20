package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.Piscine;
import com.derteuffel.marguerite.repository.PiscineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("piscine", new Piscine());
        return "piscines/form";
    }

    @PostMapping("/save")
    public String save(Piscine piscine, RedirectAttributes redirectAttributes) {

        piscine.setPrixT(piscine.getPrixU() * piscine.getNbreHeure());
        piscineRepository.save(piscine);
        redirectAttributes.addFlashAttribute("success","You've been save your data successfully");
        return "redirect:/hotel/piscines/all";
    }

    @GetMapping("/edit/{id}")
    public String updateForm(Model model, @PathVariable Long id) {
        Piscine piscine = piscineRepository.getOne(id);
        model.addAttribute("piscine", piscine);
        return "piscines/update";
    }

    @PostMapping("/update")
    public String update(Piscine piscine, RedirectAttributes redirectAttributes){

        piscine.setPrixT(piscine.getPrixU() * piscine.getNbreHeure());
        piscineRepository.save(piscine);
        redirectAttributes.addFlashAttribute("suuccess","You've been save your data successfully");
        return "redirect:/hotel/piscines/all";

    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes){
        Piscine piscine = piscineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid piscine id:" +id));
        piscineRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success","You've delete data successfully");
        return "redirect:/hotel/piscines/all";
    }
}
