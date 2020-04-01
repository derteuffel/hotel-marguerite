package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.Appartement;
import com.derteuffel.marguerite.domain.Compte;
import com.derteuffel.marguerite.repository.AppartementRepository;
import com.derteuffel.marguerite.repository.ReservationRepository;
import com.derteuffel.marguerite.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@RequestMapping("/hotel/appartements")
public class AppartementController {

    @Autowired
    private AppartementRepository appartementRepository;

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private CompteService compteService;

    @Value("${file.upload-dir}")
    private String fileStorage;

    @GetMapping("/all")
    public String findAllAppartement(Model model,HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        request.getSession().setAttribute("compte",compte);
        model.addAttribute("appartements", appartementRepository.findAll());

        return "appartements/all";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("appartement", new Appartement());
        return "appartements/form";
    }

    @PostMapping("/save")
    public String save(Appartement appartement, RedirectAttributes redirectAttributes) {
        appartement.setStatus(false);
        appartementRepository.save(appartement);
        redirectAttributes.addFlashAttribute("success","You've been save your data successfully");
        return "redirect:/appartements/all";
    }

    @GetMapping("/detail/{id}")
    public String findAppartement(@PathVariable Long id, Model model){

        Appartement appartement = appartementRepository.getOne(id);
        model.addAttribute("appartement", appartement);
        return "appartements/detail";
    }

    @GetMapping("/edit/{id}")
    public String updateAppartement(Model model, @PathVariable Long id) {
        Appartement appartement = appartementRepository.getOne(id);
        model.addAttribute("appartement", appartement);
        return "appartements/update";
    }

    @PostMapping("/update")
    public String update(Appartement appartement, RedirectAttributes redirectAttributes){

        appartementRepository.save(appartement);
        redirectAttributes.addFlashAttribute("suuccess","You've been save your data successfully");
        return "redirect:/appartements/all";

    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes){
        Appartement appartement = appartementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid appartement id:" +id));
        appartementRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success","You've delete data successfully");
        return "redirect:/appartements/all";
    }

}
