package com.library.LibraryManagementSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Bean for encrypting passwords
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean for all security rules
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                // 1. Allow access to static resources (CSS, JS) and public pages
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/", "/register", "/login").permitAll()

                // 2. Restrict pages based on roles
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/member/**").hasRole("MEMBER")

                // 3. All other requests must be authenticated
                .anyRequest().authenticated()
        );

        // 4. Configure the Login Form
        http.formLogin(form -> form
                .loginPage("/login") // Use our custom /login page
                .loginProcessingUrl("/login") // The URL to submit the form to
                .defaultSuccessUrl("/dashboard", true) // Redirect to a dashboard after login
                .failureUrl("/login?error=true") // Redirect back to login on failure
                .permitAll()
        );

        // 5. Configure Logout
        http.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/") // Redirect to home page after logout
                .permitAll()
        );

        return http.build();
    }
}