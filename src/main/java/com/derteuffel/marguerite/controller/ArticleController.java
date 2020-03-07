package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.Article;
import com.derteuffel.marguerite.repository.ArticleRepository;
import com.derteuffel.marguerite.repository.CommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/hotel/articles")
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommandeRepository commandeRepository;

    @GetMapping("/all")
    public String findAll(Model model){
        model.addAttribute("articles", commandeRepository.findAll());

        return "articles/articlesList";
    }

    @GetMapping("/form/{id}")
    public String form(Model model, @PathVariable Long id){
        model.addAttribute("article", new Article());
        model.addAttribute("commande", commandeRepository.getOne(id));
        return "articles/formArticle";
    }

    @PostMapping("/save")
    public String save(Article article) {
        articleRepository.save(article);
        return "redirect:/hotel/articles/all";
    }

    @GetMapping("/detail/{id}")
    public String findById(@PathVariable Long id, Model model){
        Article article = articleRepository.getOne(id);
        model.addAttribute("article", article);
        return "articles/detail";
    }

    @GetMapping("/edit/{id}")
    public String updateForm(Model model, @PathVariable Long id) {
        Article article = articleRepository.getOne(id);
        model.addAttribute("article", article);
        model.addAttribute("commandes", commandeRepository.findAll());
        return "commandes/edit";
    }

    @PostMapping("/update/{id}")
    public String update(Article article , @PathVariable Long id){
        Optional<Article> article1 = articleRepository.findById(id);

        if (article1.isPresent()){
            Article article2 = article1.get();
            article2.setNom(article.getNom());
            article2.setPrixT(article.getPrixT());
            article2.setPrixU(article.getPrixU());
            article2.setQty(article.getQty());
            article2.setCommande(article.getCommande());
            articleRepository.save(article2);
            return "redirect:/hotel/articles/all";
        }else {
            return "redirect:/hotel/articles/form";
        }

    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable Long id){
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid article id:" +id));
        articleRepository.deleteById(id);
        model.addAttribute("articles", articleRepository.findAll());
        return "redirect:/hotel/articles/all";
    }
}
