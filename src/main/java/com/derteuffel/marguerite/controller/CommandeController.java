package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.Chambre;
import com.derteuffel.marguerite.domain.Commande;
import com.derteuffel.marguerite.domain.Place;
import com.derteuffel.marguerite.repository.ChambreRepository;
import com.derteuffel.marguerite.repository.CommandeRepository;
import com.derteuffel.marguerite.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        model.addAttribute("commandes", commandeRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));
        return "commandes/all";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("commande", new Commande());
        return "commandes/form";
    }

    @PostMapping("/save")
    public String save(Commande commande, Model model) {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat format1 = new SimpleDateFormat("hh:mm");
        if (commande.getNumTable().contains("C")){
            Chambre chambre = chambreRepository.findByNumero(commande.getNumero());
            if (chambre != null){
                commande.setChambre(chambre);
            }else {
                model.addAttribute("error", "There are no room with the number"+commande.getNumTable());
                return "commandes/form";
            }
        }else {
            Place place = placeRepository.findByNumTable(commande.getNumTable());
            if (place != null){
                commande.setPlace(place);
            }else {
                model.addAttribute("error","There are no Table with the number"+commande.getNumTable());
                return "commandes/form";
            }
        }
        commande.setDate(format.format(date));
        commande.setHeure(format1.format(date));
        commande.setNumero("C"+commandeRepository.findAll().size()+commande.getNumTable());
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
