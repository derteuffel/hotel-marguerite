package com.derteuffel.marguerite.controller;


import com.derteuffel.marguerite.domain.Stock;
import com.derteuffel.marguerite.enums.ECategory;
import com.derteuffel.marguerite.repository.StockRepository;
import com.derteuffel.marguerite.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/hotel/stocks")
public class StockController {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private CompteService compteService;

    @GetMapping("/all")
    public String findAll(Model model){

        model.addAttribute("aliments",stockRepository.findAllByCategorie("ALIMENTS"));
        model.addAttribute("boissons",stockRepository.findAllByCategorie("BOISSONS"));
        model.addAttribute("couverts",stockRepository.findAllByCategorie("COUVERTS"));
        model.addAttribute("lingeries",stockRepository.findAllByCategorie("LINGERIES"));
        model.addAttribute("autres",stockRepository.findAllByCategorie("AUTRES"));
        model.addAttribute("stocks", stockRepository.findAll());
        return "stocks/all";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("stock", new Stock());
        model.addAttribute("comptes", compteService.findAllCompte());
        return "stocks/form";
    }

    @PostMapping("/save")
    public String save(Stock stock){
      stockRepository.save(stock);
      return "redirect:/hotel/stocks/all";
    }

    @GetMapping("/detail/{id}")
    public String getStock(@PathVariable Long id, Model model) {
       Optional<Stock> stock = stockRepository.findById(id);
       model.addAttribute("stock", stock.get());
       return "stocks/detail";
    }

    @GetMapping("/edit/{id}")
    public String updateForm(Model model, @PathVariable Long id) {
        Stock stock = stockRepository.findById(id).get();
        model.addAttribute("stock", stock);
        model.addAttribute("comptes", compteService.findAllCompte());
        return "stocks/update";
    }

    @PostMapping("/update/{id}")
    public String update(Stock stock, @PathVariable Long id, RedirectAttributes redirectAttributes){

        Optional<Stock> stockOptional = stockRepository.findById(id);
        if (stockOptional.isPresent()){
            Stock stock1 = stockOptional.get();
            stock1.setNom(stock.getNom());
            stock1.setCategorie(stock.getCategorie());
            stock1.setDate(stock.getDate());
            stock1.setQty(stock.getQty());
            stock1.setType(stock.getType());
            stockRepository.save(stock);
        }else {
            redirectAttributes.addFlashAttribute("error","There are not Stock with Id:"+id);
        }

        return "redirect:/hotel/stocks/all";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes){
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid stock id:" +id));
        stockRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "You've deleted your element successfully");
        return "redirect:/hotel/stocks/all";
    }

    @GetMapping("/categories/{categorie}")
    public String findAllByCategorie(@PathVariable String categorie, Model model, RedirectAttributes redirectAttributes) {
        List<Stock> stocks = new ArrayList<>();
                stockRepository.findAllByCategorie(categorie).forEach(stocks :: add);
                if ( stocks.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error","There are no elemennt in this category:"+categorie);
                    return "redirect:/hotel/stocks/all";
                }else {
                 model.addAttribute("stocks",stocks);
                 return "stocks/all";
                }

    }
}
