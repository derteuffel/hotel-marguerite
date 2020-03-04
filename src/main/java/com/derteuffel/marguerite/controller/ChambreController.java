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

import java.util.Optional;

@Controller
@RequestMapping("/hotel/chambres")
public class ChambreController {

    @Autowired
    private ChambreRepository chambreRepository;


    @GetMapping("/all")
    public String findAll(Model model){
        model.addAttribute("chambres", chambreRepository.findAll());
        return "chambres/chambreList";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("chambre", new Chambre());
        return "chambres/formChambre";
    }

    @PostMapping("/save")
    public String save(Chambre chambre) {
        chambreRepository.save(chambre);
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
        return "chambres/editChambre";
    }

    @PostMapping("/update/{id}")
    public String update(Chambre chambre, @PathVariable Long id){
        Optional<Chambre> chambre1 = chambreRepository.findById(id);

        if (chambre1.isPresent()){
            Chambre chambre2 = chambre1.get();
            chambre2.setNumero(chambre.getNumero());
            chambre2.setNbreLit(chambre.getNbreLit());
            chambre2.setNbrePiece(chambre.getNbrePiece());
            chambre2.setNbrePlace(chambre.getNbrePlace());
            chambre2.setStatus(chambre.getStatus());
            chambre2.setPrix(chambre.getPrix());
            chambreRepository.save(chambre2);
            return "redirect:/hotel/chambres/all";
        }else {
            return "redirect:/hotel/chambres/form";
        }

    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable Long id){
        Chambre chambre = chambreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid chambre id:" +id));
        chambreRepository.deleteById(id);
        model.addAttribute("chambres", chambreRepository.findAll());
        return "redirect:/hotel/chambres/all";
    }
}
