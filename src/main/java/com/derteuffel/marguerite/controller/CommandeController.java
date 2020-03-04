package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.Commande;
import com.derteuffel.marguerite.repository.ChambreRepository;
import com.derteuffel.marguerite.repository.CommandeRepository;
import com.derteuffel.marguerite.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/hotel/commandes")
public class CommandeController {

    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private ChambreRepository chambreRepository;
    @Autowired
    private PlaceRepository placeRepository;


    @GetMapping("/all")
    public String findAll(Model model){
        model.addAttribute("commandes", commandeRepository.findAll());

        return "commandes/commandeList";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("commande", new Commande());
        model.addAttribute("places", placeRepository.findAll());
        model.addAttribute("chambres", chambreRepository.findAll());
        return "commandes/formCommande";
    }

    @PostMapping("/save")
    public String save(Commande commande) {
        commandeRepository.save(commande);
        return "redirect:/hotel/commandes/all";
    }

    @GetMapping("/detail/{id}")
    public String findById(@PathVariable Long id, Model model){
        Commande commande = commandeRepository.getOne(id);
        model.addAttribute("commande", commande);
        return "commandes/detail";
    }

    @GetMapping("/edit/{id}")
    public String updateForm(Model model, @PathVariable Long id) {
        Commande commande = commandeRepository.getOne(id);
        model.addAttribute("commande", commande);
        model.addAttribute("places", placeRepository.findAll());
        model.addAttribute("chambres", chambreRepository.findAll());
        return "commandes/editCommande";
    }

    @PostMapping("/update/{id}")
    public String update(Commande commande, @PathVariable Long id){
        Optional<Commande> commande1 = commandeRepository.findById(id);

        if (commande1.isPresent()){
            Commande commande2 = commande1.get();
            commande2.setNumero(commande.getNumero());
            commande2.setMontantT(commande.getMontantT());
            commande2.setDate(commande.getDate());
            commande2.setHeure(commande.getHeure());
            commande2.setNumTable(commande.getNumTable());
            commande2.setNbreSurTable(commande.getNbreSurTable());
            commandeRepository.save(commande2);
            return "redirect:/hotel/commandes/all";
        }else {
            return "redirect:/hotel/commandes/form";
        }

    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable Long id){
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid commande id:" +id));
        commandeRepository.deleteById(id);
        model.addAttribute("commandes", commandeRepository.findAll());
        return "redirect:/hotel/commandes/all";
    }
}
