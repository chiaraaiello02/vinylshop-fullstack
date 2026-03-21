package it.chiara.vinylshop.config;

import it.chiara.vinylshop.security.JwtAuthTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration // indica che questa classe contiene configurazioni di Spring
@EnableMethodSecurity(prePostEnabled = true) // abilita l'uso di @PreAuthorize nei metodi
public class SecurityConfig {

    // Encoder usato per criptare le password nel database
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // algoritmo sicuro per hash delle password
    }

    // Configurazione principale della sicurezza Spring
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthTokenFilter jwtFilter) throws Exception {

        http
                // disabilita la protezione CSRF perché usiamo JWT e non sessioni
                .csrf(csrf -> csrf.disable())

                // abilita CORS per permettere al frontend Angular di chiamare il backend
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // applicazione stateless: niente sessioni lato server
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // configurazione delle autorizzazioni sugli endpoint
                .authorizeHttpRequests(auth -> auth

                        // ===== ENDPOINT PUBBLICI =====

                        // login e registrazione accessibili senza autenticazione
                        .requestMatchers("/auth/**").permitAll()

                        // lettura dei vinili pubblica
                        .requestMatchers(HttpMethod.GET, "/vinili/**").permitAll()

                        // lettura delle categorie pubblica
                        .requestMatchers(HttpMethod.GET, "/categorie/**").permitAll()


                        // ===== ENDPOINT SOLO ADMIN =====

                        // creazione vinili
                        .requestMatchers(HttpMethod.POST, "/vinili/**")
                        .hasAuthority("ROLE_ADMIN")

                        // modifica vinili
                        .requestMatchers(HttpMethod.PUT, "/vinili/**")
                        .hasAuthority("ROLE_ADMIN")

                        // eliminazione vinili
                        .requestMatchers(HttpMethod.DELETE, "/vinili/**")
                        .hasAuthority("ROLE_ADMIN")

                        // creazione categorie
                        .requestMatchers(HttpMethod.POST, "/categorie/**")
                        .hasAuthority("ROLE_ADMIN")

                        // modifica categorie
                        .requestMatchers(HttpMethod.PUT, "/categorie/**")
                        .hasAuthority("ROLE_ADMIN")

                        // eliminazione categorie
                        .requestMatchers(HttpMethod.DELETE, "/categorie/**")
                        .hasAuthority("ROLE_ADMIN")


                        // ===== TUTTI GLI ALTRI ENDPOINT =====

                        // qualunque altra richiesta richiede autenticazione
                        .anyRequest().authenticated()
                )

                // disabilita il form login di Spring (non lo usiamo)
                .formLogin(form -> form.disable());

        // aggiunge il filtro JWT prima del filtro standard di autenticazione
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    // configurazione CORS per permettere chiamate dal frontend
    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // permette richieste da qualunque origine (Angular localhost)
        configuration.setAllowedOrigins(List.of("*"));

        // metodi HTTP consentiti
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // headers consentiti
        configuration.setAllowedHeaders(List.of("*"));

        // disabilita l'uso di credenziali (cookies)
        configuration.setAllowCredentials(false);

        // applica questa configurazione a tutte le rotte
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
