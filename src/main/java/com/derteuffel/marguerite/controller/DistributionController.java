package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.Compte;
import com.derteuffel.marguerite.domain.Distribution;
import com.derteuffel.marguerite.domain.Stock;
import com.derteuffel.marguerite.repository.CompteRepository;
import com.derteuffel.marguerite.repository.DistributionRepository;
import com.derteuffel.marguerite.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/hotel/distributions")
public class DistributionController {

    @Autowired
    private DistributionRepository distributionRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private CompteRepository compteRepository;

    @GetMapping("/all")
    public String findAll(Model model){
        model.addAttribute("distributions", distributionRepository.findAll());
        return "distributions/all";
    }

    @GetMapping("/form/{id}")
    public String form(Model model,@PathVariable Long id){
        Stock stock = stockRepository.getOne(id);
        model.addAttribute("distribution", new Distribution());
        model.addAttribute("stock", stock);
        return "distributions/new";
    }

    @PostMapping("/save")
    public String save(@Valid Distribution distribution){
        distributionRepository.save(distribution);
        return "redirect:/hotel/distributions/all"  ;
    }

    @GetMapping("/edit/{id}")
    public String updatedDistribution(Model model, @PathVariable Long id){
        Distribution distribution =  distributionRepository.findById(id).get();
        model.addAttribute("distribution", distribution);
        return "distributions/edit";
    }

    @PostMapping("/update/{id}")
    public String save(@Valid Distribution distribution, @PathVariable("id") Long id,
                       BindingResult result, Model model, HttpSession session, String nom, String secteur, String date, int qty ){
        Stock stock = stockRepository.getOne((Long)session.getAttribute("id"));
        Compte compte = compteRepository.getOne((Long)session.getAttribute("id"));
        distribution.setNom(nom);
        distribution.setSecteur(secteur);
        distribution.setDate(date);
        distribution.setQty(qty);
        distribution.setStock(stock);
        distribution.setCompte(compte);
       distributionRepository.save(distribution);
        model.addAttribute("distributions", distributionRepository.findAll());
        return "redirect:/hotel/distributions/all";

    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model, HttpSession session) {
        Distribution distribution = distributionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid distribution id:" +id));
        System.out.println("distribution id: " + distribution.getNom());
        distributionRepository.delete(distribution);
        model.addAttribute("distributions", distributionRepository.findAll());
        return "redirect:/hotel/distributions/all" ;
    }

    @GetMapping("/detail/{id}")
    public String getTask(Model model, @PathVariable Long id){
        Distribution distribution = distributionRepository.findById(id).get();
        model.addAttribute("distribution", distribution);
        return "distributions/detail";

    }
}
