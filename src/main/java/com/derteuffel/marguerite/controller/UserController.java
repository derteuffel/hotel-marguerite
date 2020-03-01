package com.derteuffel.marguerite.controller;


import com.derteuffel.marguerite.domain.User;
import com.derteuffel.marguerite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/hotel/users")
public class UserController {


    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public String findAll(Model model){
        model.addAttribute("users", userRepository.findAll());
      return "index" ;
    }

    @GetMapping("/registration")
    public String form(Model model){
        model.addAttribute("user", new User());
        return "user/form";
    }

    @PostMapping("/save")
    public String save(User user, Model model, BindingResult bindingResult) {
        User userExists = userRepository.findByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "there is already a user registered with a email provided");
        }

        if(bindingResult.hasErrors()) {
            return  "user/form";
        }else{
            System.out.println(user.getNom());
            userRepository.save(user);

        }

        return "redirect:/login";
    }

    @PutMapping("/{id}")
    public String updateUser(User user, @PathVariable Long id){
        Optional<User> user1 = userRepository.findById(id);

        if (user1.isPresent()) {
            User user2 =user1.get();
            user2.setNom(user.getNom());
            user2.setPrenom(user.getPrenom());
            user2.setEmail(user.getEmail());
            user2.setQuartier(user.getQuartier());
            user2.setTelephone(user.getTelephone());
            user2.setFonction(user.getFonction());
            userRepository.save(user2);
            return "redirect: ";
        }
        else {
            return "";
        }
    }

    @GetMapping("/{id}")
    public String findUserById(@PathVariable Long id){
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()){
            return "";
        }
        else {
            return "";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        try {
            userRepository.deleteById(id);
            return "";
        }
        catch (Exception e) {
            return  "";
        }
    }
}
