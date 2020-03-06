package com.derteuffel.marguerite.controller;


import com.derteuffel.marguerite.domain.MJ;
import com.derteuffel.marguerite.domain.Stock;
import com.derteuffel.marguerite.enums.ECategory;
import com.derteuffel.marguerite.repository.MJRepository;
import com.derteuffel.marguerite.repository.StockRepository;
import com.derteuffel.marguerite.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private MJRepository mjRepository;

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
    public String save(Stock stock, RedirectAttributes redirectAttributes, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        stock.setCompte(compteService.findByUsername(principal.getName()));
        stock.setType(stock.getType().toString());
        stock.setCategorie(stock.getCategorie().toString());
      stockRepository.save(stock);
      redirectAttributes.addFlashAttribute("success", "You've save your stock successfully");
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
        return "stocks/update";
    }

    @PostMapping("/update")
    public String update(Stock stock, RedirectAttributes redirectAttributes, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        stock.setCompte(compteService.findByUsername(principal.getName()));
        stock.setType(stock.getType());
        stock.setCategorie(stock.getCategorie());
        stockRepository.save(stock);
        redirectAttributes.addFlashAttribute("success", "You've save your stock successfully");

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

    @GetMapping("/mjs/{id}")
    public String mjs(@PathVariable Long id, Model model){
       List<MJ> mjs = mjRepository.findAllByStock_Id(id);
       model.addAttribute("add", new MJ());
       model.addAttribute("update", new MJ());
       model.addAttribute("stock", stockRepository.getOne(id));
       model.addAttribute("lists",mjs);

       return "stocks/mjs";
    }

    @PostMapping("/mjs/save/{id}")
    public String saveMjs(@PathVariable Long id, MJ mj, RedirectAttributes redirectAttributes){
        Stock stock = stockRepository.getOne(id);
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat();
        mj.setDate(dateFormat.format(date));
        mj.setNom(stock.getNom());
        mj.setStock(stock);
        stock.setQty(mj.getQty()+stock.getQty());
        stockRepository.save(stock);
        mjRepository.save(mj);
        redirectAttributes.addFlashAttribute("success","you've data saved successfully");
        return "redirect:/hotel/stocks/mjs/"+stock.getId();

    }
    @PostMapping("/mjs/update/{id}")
    public String Mjs(@PathVariable Long id, MJ mj, RedirectAttributes redirectAttributes){
        Stock stock = stockRepository.getOne(id);

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat();
        mj.setDate(dateFormat.format(date));
        mj.setNom(stock.getNom());
        mj.setStock(stock);
        stock.setQty(stock.getQty()+mj.getQty());
        mjRepository.save(mj);
        redirectAttributes.addFlashAttribute("success","you've data saved successfully");
        return "redirect:/hotel/stocks/mjs/"+stock.getId();

    }
}
