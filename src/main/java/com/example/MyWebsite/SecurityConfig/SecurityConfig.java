package com.example.MyWebsite.SecurityConfig;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Testing ke liye CSRF disable kiya
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user.html", "/signaling", "/css/**", "/js/**").permitAll() // User aur WebSocket open hain
                        .requestMatchers("/admin.html").hasRole("ADMIN") // Admin page ke liye ADMIN role chahiye
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/admin.html", true) // Login successful hone par admin page par bhejo
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Yahan aap apna Admin Username aur Password set kar sakte hain
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("superadmin") // Admin ka username
                .password("Admin@123")  // Admin ka password
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}