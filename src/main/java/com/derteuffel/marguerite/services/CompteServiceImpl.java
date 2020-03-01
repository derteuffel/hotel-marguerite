package com.derteuffel.marguerite.services;

import com.derteuffel.marguerite.domain.Compte;
import com.derteuffel.marguerite.domain.Role;
import com.derteuffel.marguerite.domain.User;
import com.derteuffel.marguerite.helpers.CompteRegistrationDto;
import com.derteuffel.marguerite.repository.CompteRepository;
import com.derteuffel.marguerite.repository.RoleRepository;
import com.derteuffel.marguerite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompteServiceImpl  implements CompteService{

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Compte findByUsername(String username) {
        return compteRepository.findByUsername(username);
    }

    @Override
    public Compte save(CompteRegistrationDto compteRegistrationDto, String s) {
        Compte compte = new Compte();
        User user = new User();
        user.setNom(compteRegistrationDto.getUsername());
        user.setEmail(compteRegistrationDto.getEmail());
        user.setAvatar(s);
        compte.setUsername(compteRegistrationDto.getUsername());
        compte.setPassword(passwordEncoder.encode(compteRegistrationDto.getPassword()));
        compte.setEmail(compteRegistrationDto.getEmail());
        compte.setAvatar(s);
        userRepository.save(user);
        Role role = new Role();
        if (compteRepository.findAll().size()<=2){
            role.setName("ROLE_ROOT");
        }else {
            role.setName("ROLE_USER");
        }
        Optional<Role> existRole =  roleRepository.findByName(role.getName());
        if (existRole.isPresent()){
            compte.setRoles(Arrays.asList(existRole.get()));
        }else {
            System.out.println(role.getName());
            roleRepository.save(role);
            compte.setRoles(Arrays.asList(role));
        }
        compte.setUser(user);
        compteRepository.save(compte);
        return compte;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println(username);
        Compte compte = compteRepository.findByUsername(username);
        if (compte == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(compte.getUsername(),
                compte.getPassword(),
                mapRolesToAuthorities(compte.getRoles()));
    }

    private Collection <? extends GrantedAuthority> mapRolesToAuthorities(Collection< Role > roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
