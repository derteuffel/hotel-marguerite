package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.Compte;
import com.derteuffel.marguerite.domain.Role;
import com.derteuffel.marguerite.helpers.CompteRegistrationDto;
import com.derteuffel.marguerite.repository.CompteRepository;
import com.derteuffel.marguerite.repository.RoleRepository;
import com.derteuffel.marguerite.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private CompteService compteService;
    @Value("${file.upload-dir}")
    private  String fileStorage;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CompteRepository compteRepository;

    @ModelAttribute("compte")
    public CompteRegistrationDto compteRegistrationDto(){
        return new CompteRegistrationDto();
    }

     @GetMapping("/registration")
    public String registrationForm(Model model){
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUserAccount(@ModelAttribute("compte") @Valid CompteRegistrationDto compteDto,
                                      BindingResult result, RedirectAttributes redirectAttributes, Model model, @RequestParam("file") MultipartFile file) {

        System.out.println(compteDto.getUsername());
        Compte existing = compteService.findByUsername(compteDto.getUsername());
        if (existing != null) {
            System.out.println("i'm there");
            result.rejectValue("username", null, "There is already an account registered with that login");
            model.addAttribute("error","There is already an account registered with that login");
        }

        if (result.hasErrors()) {
            System.out.println(result.toString());
            System.out.println("i'm inside");
            return "registration";
        }

        if (!(file.isEmpty())){
            try{
                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();
                Path path = Paths.get(fileStorage + file.getOriginalFilename());
                Files.write(path, bytes);
            }catch (IOException e){
                e.printStackTrace();
            }
            compteService.save(compteDto, "/downloadFile/"+file.getOriginalFilename());
        }else {
            compteService.save(compteDto,"/img/default.jpeg");
        }

        redirectAttributes.addFlashAttribute("success","You've been registered successfully, try to login to your account");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model){
        return "login";
    }

    @GetMapping("/profile/{username}")
    public String getCompte(@PathVariable String username, Model model){
        Compte compte = compteService.findByUsername(username);
        model.addAttribute("compte",compte);
        return "compte/profile";
    }


    @GetMapping("/accounts")
    public String all(Model model){
        List<Compte> compteList = compteRepository.findAll(Sort.by(Sort.Direction.ASC,"username"));
        model.addAttribute("lists",compteList);
        return "user/accounts";
    }

    @GetMapping("/accounts/change/{id}")
    public String changeRole(String role, @PathVariable Long id){
        Compte compte = compteRepository.getOne(id);
        compte.getRoles().clear();
        Optional<Role> role1 = roleRepository.findByName(role);
        if (role1.isPresent()){
            compte.getRoles().add(role1.get());

        }else {
            Role role2 = new Role();
            role2.setName(role);
            roleRepository.save(role2);
            compte.getRoles().add(role2);
        }
        compteRepository.save(compte);
        return "redirect:/accounts";
    }
}
