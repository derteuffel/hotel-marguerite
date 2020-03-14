package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.Compte;
import com.derteuffel.marguerite.domain.Place;
import com.derteuffel.marguerite.enums.ERole;
import com.derteuffel.marguerite.enums.ESecteur;
import com.derteuffel.marguerite.repository.PlaceRepository;
import com.derteuffel.marguerite.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/hotel/places")
public class PlaceController {
    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private CompteService compteService;

    @GetMapping("/all")
    public String findAll(Model model){
        model.addAttribute("places", placeRepository.findAll());
        return "places/all";
    }
    @GetMapping("/restaurant/orders")
    public String restaurant(Model model){
        model.addAttribute("lists", placeRepository.findAllBySecteur(ESecteur.RESTAURANT.toString()));
        return "places/all-2";
    }

    @GetMapping("/lounge_bar/orders")
    public String lounge_bar(Model model){
        model.addAttribute("lists", placeRepository.findAllBySecteur(ESecteur.LOUNGE_BAR.toString()));
        return "places/all-2";
    }
    @GetMapping("/terasse/orders")
    public String terasse(Model model){
        model.addAttribute("lists", placeRepository.findAllBySecteur(ESecteur.TERASSE.toString()));
        return "places/all-2";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("place", new Place());
        return "places/form";
    }

    @PostMapping("/save")
    public String save(@Valid Place place, RedirectAttributes redirectAttributes){

        if (place.getSecteur().contains("TERASSE")){
            place.setNumTable(("TR"+(placeRepository.findAllBySecteur("TERASSE").size()+1)).toUpperCase());
        }else if (place.getSecteur().contains("LOUNGE_BAR")){
            place.setNumTable(("LB"+(placeRepository.findAllBySecteur("LOUNGE_BAR").size()+1)).toUpperCase());
        }else {
            place.setNumTable(("RS"+(placeRepository.findAllBySecteur("RESTAURANT").size()+1)).toUpperCase());
        }
        place.setStatus(false);
        placeRepository.save(place);
        redirectAttributes.addFlashAttribute("success", "You've been save your data successfully");
        return "redirect:/hotel/places/all";
    }

    @GetMapping("/edit/{id}")
    public String updatedPlace(Model model, @PathVariable Long id){
        Place place =  placeRepository.findById(id).get();
        model.addAttribute("place", place);
        return "places/edit";
    }

    @PostMapping("/update")
    public String save(@Valid Place place){
        if (place.getSecteur().contains("TERRASSE")){
            place.setNumTable(("TR"+placeRepository.findAllBySecteur("TERRASSE").size()).toUpperCase());
        }else if (place.getSecteur().contains("LOUNGE_BAR")){
            place.setNumTable(("LB"+placeRepository.findAllBySecteur("LOUNGE_BAR").size()).toUpperCase());
        }else {
            place.setNumTable(("RS"+placeRepository.findAllBySecteur("RESTAURANT").size()).toUpperCase());
        }
        place.setStatus(false);
        placeRepository.save(place);
        return "redirect:/hotel/places/all";

    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model) {
        Place place =  placeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid place id:" +id));
        System.out.println("place id: " + place.getSecteur());
        placeRepository.delete(place);
        model.addAttribute("places", placeRepository.findAll());
        return "redirect:/hotel/places/all" ;
    }

    @GetMapping("/detail/{id}")
    public String getTask(Model model, @PathVariable Long id){
        Place place =  placeRepository.findById(id).get();
        model.addAttribute("place", place);
        return "places/detail";

    }
}
