package com.derteuffel.marguerite.controller;


import com.derteuffel.marguerite.domain.Stock;
import com.derteuffel.marguerite.repository.CompteRepository;
import com.derteuffel.marguerite.repository.StockRepository;
import com.derteuffel.marguerite.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
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
        model.addAttribute("stocks", stockRepository.findAll());
        return "stocks/stockList";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("stock", new Stock());
        model.addAttribute("comptes", compteService.findAllCompte());
        return "stocks/formStock";
    }

    @PostMapping("/save")
    public String save(Stock stock){
      stockRepository.save(stock);
      return "stocks/stockList";
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
        return "stocks/editStock";
    }

    @PutMapping("/update/{id}")
    public String update(Stock stock, @PathVariable Long id, String nom,
                         String categorie, String type, Integer qty, Date date){

        stock.setNom(nom);
        stock.setCategorie(categorie);
        stock.setType(type);
        stock.setQty(qty);
        stock.setDate(date);
        stockRepository.save(stock);
        return "stocks/stockList";
    }

    @DeleteMapping("/{id}")
    public String delete(Model model, @PathVariable Long id){
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid stock id:" +id));
        stockRepository.deleteById(id);
        model.addAttribute("stocks", stockRepository.findAll());
        return "stocks/stockList";
    }

    @GetMapping("/categorie/{categorie}")
    public String findAllByCategorie(@PathVariable String categorie) {
        List<Stock> stocks = new ArrayList<>();
            try {
                stockRepository.findAllByCategorie(categorie).forEach(stocks :: add);
                if ( stocks.isEmpty()) {
                    return "";
                }
                return "";
            } catch (Exception e){
                e.printStackTrace();
            }
            return "";
    }
}
