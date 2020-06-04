package com.derteuffel.marguerite.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Created by user on 31/03/2020.
 */
@Configuration
@Order(5)
public class LoungeSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/lounges/**").authorizeRequests()
                .antMatchers("/downloadFile/**").permitAll()
                .antMatchers("/lounges/**").access("hasAnyRole('ROLE_LOUNGE_BAR','ROLE_ROOT','ROLE_ADMIN')")
                .and()
                .formLogin()
                .loginPage("/lounges/login")
                .loginProcessingUrl("/lounges/login")
                .defaultSuccessUrl("/lounges/places/orders")
                .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/lounges/logout"))
                .logoutSuccessUrl("/?logout")
                .and()
                .exceptionHandling().accessDeniedPage("/lounges/access-denied");
    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private DaoAuthenticationProvider authenticationProvider;

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/lounges/registration**",
                        "/js/**",
                        "/css/**",
                        "/img/**",
                        "/vendor/**",
                        "/fonts/**",
                        "/downloadFile/**",
                        "/static/**"
                );
    }
}
