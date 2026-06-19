package com.example.MyWebsite.SecurityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/",
                                "/user.html",
                                "/form.html",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/signaling"
                        ).permitAll()

                        .requestMatchers("/admin.html")
                        .hasRole("ADMIN")

                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form
                        .defaultSuccessUrl("/admin.html", true)
                        .permitAll()
                )

                .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        UserDetails admin =
                User.withDefaultPasswordEncoder()
                        .username("superadmin")
                        .password("Admin@123")
                        .roles("ADMIN")
                        .build();

        return new InMemoryUserDetailsManager(admin);
    }
}
