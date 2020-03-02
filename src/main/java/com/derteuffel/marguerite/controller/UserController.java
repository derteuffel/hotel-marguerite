package com.derteuffel.marguerite.controller;


import com.derteuffel.marguerite.domain.User;
import com.derteuffel.marguerite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequestMapping("/hotel/users")
public class UserController {

    @Value("${file.upload-dir}")
    private  String fileStorage;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/lists")
    public String findAll(Model model){
        System.out.println(fileStorage);
        model.addAttribute("userForm", new User());
        model.addAttribute("users", userRepository.findAll());
      return "user/all" ;
    }


    @PostMapping("/save")
    public String save(User user, Model model, BindingResult bindingResult, @RequestParam("file") MultipartFile file) {
        System.out.println(user.getNom());
        User userExists = userRepository.findByNomOrEmailOrTelephone(user.getNom(),user.getEmail(),user.getTelephone());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "there is already a user registered with an email or name or telephone provided ");
        }


        if(bindingResult.hasErrors()) {
            return  "user/form";
        }else{
            if (!(file.isEmpty())){
                try{
                    // Get the file and save it somewhere
                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(fileStorage + file.getOriginalFilename());
                    Files.write(path, bytes);
                }catch (IOException e){
                    e.printStackTrace();
                }
                user.setAvatar("/downloadFile/"+file.getOriginalFilename());
            }else {
                user.setAvatar("/img/default.jpeg");
            }
            System.out.println(user.getNom());
            userRepository.save(user);

        }

        return "redirect:/hotel/users/lists";
    }


    @GetMapping("/update/{id}")
    public String updateForm(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes){

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()){
            model.addAttribute("user", optionalUser.get());
            return "user/update";
        }else {
            redirectAttributes.addFlashAttribute("error", "There are no user with Id:"+id);
            return "redirect:/hotel/users";
        }
    }

    @PostMapping("/update/{id}")
    public String updateUser(User user, @PathVariable Long id, RedirectAttributes redirectAttributes, @RequestParam("file") MultipartFile file){
        Optional<User> user1 = userRepository.findById(id);


        if (user1.isPresent()) {
            User user2 =user1.get();
            user2.setNom(user.getNom());
            user2.setPrenom(user.getPrenom());
            user2.setEmail(user.getEmail());
            user2.setQuartier(user.getQuartier());
            user2.setTelephone(user.getTelephone());
            user2.setFonction(user.getFonction());
            if (!(file.isEmpty())){
                try{
                    // Get the file and save it somewhere
                    byte[] bytes = file.getBytes();
                    Path path = Paths.get(fileStorage + file.getOriginalFilename());
                    Files.write(path, bytes);
                }catch (IOException e){
                    e.printStackTrace();
                }
                user.setAvatar("/downloadFile/"+file.getOriginalFilename());
            }else {
                user2.setAvatar(user.getAvatar());
            }
            userRepository.save(user2);
            redirectAttributes.addFlashAttribute("success", "The user has been updated successfully");
            return "redirect:/hotel/users/get/"+user2.getId();
        }
        else {
            redirectAttributes.addFlashAttribute("error","There are no user with Id :"+id);
            return "redirect:/hotel/users";
        }
    }

    @GetMapping("/get/{id}")
    public String findUserById(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes){
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()){
            model.addAttribute("user",user.get());
            return "user/detail";
        }
        else {
            redirectAttributes.addFlashAttribute("error", "There no user with Id :"+id);
            return "redirect:/hotel/users";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
            userRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "You deleted the user with Id:"+id);
            return "redirect:/hotel/users";
    }
}
