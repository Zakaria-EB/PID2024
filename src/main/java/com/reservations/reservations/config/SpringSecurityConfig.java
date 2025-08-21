package com.reservations.reservations.config;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Bean
    public SecurityFilterChain configure(final HttpSecurity http) throws Exception {
        System.out.println("Config de la sécurité en cours ...");
        return http.cors(Customizer.withDefaults())
                .csrf(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/logout"))
                .authorizeHttpRequests(auth -> {

                    auth.requestMatchers("/api/localities").permitAll();
                    auth.requestMatchers("/api/localities/**").authenticated();
                    auth.requestMatchers("/api/artists/**").permitAll();
                    auth.requestMatchers("/api/auth/check").permitAll();
                    auth.requestMatchers("/api/login").permitAll();  // Ajout de /api/login
                    auth.requestMatchers("/api/logout").permitAll();

                    auth.requestMatchers( "/api/tags" ).permitAll();
                    auth.requestMatchers( "/api/tags/**" ).hasRole("ADMIN");

                    auth.requestMatchers( "/cart/view" ).permitAll();
                    auth.requestMatchers( "/cart/delete" ).permitAll();
                    auth.requestMatchers( HttpMethod.POST, "/cart/checkout" ).authenticated();

                    auth.requestMatchers("/admin").hasRole("ADMIN");
                    auth.requestMatchers("/user").hasRole("MEMBER");
                    auth.anyRequest().permitAll();
                })
                .formLogin(form -> form
                        .loginPage("/login")  // La page de login est gérée par Spring Security
                        .usernameParameter("login")
                        .failureUrl("/login?loginError=true")
                        .successHandler((request, response, authentication) -> {
                            // Exemple de redirection après une connexion réussie
                            response.sendRedirect("/");
                        })
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logoutSuccess=true")
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login?loginRequired=true")))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
        System.out.println("creation de authenticationmanager ...");
        AuthenticationManagerBuilder authMngrBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authMngrBuilder.userDetailsService(customUserDetailsService).passwordEncoder(bCryptPasswordEncoder);

        return authMngrBuilder.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}