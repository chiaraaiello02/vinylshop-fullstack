package it.chiara.vinylshop.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component // registra questo filtro come bean Spring
public class JwtAuthTokenFilter extends OncePerRequestFilter {

    // classe che valida e decodifica il JWT
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // legge l'header Authorization
        String authHeader = request.getHeader("Authorization");

        try {
            // controlla che l'header esista e inizi con "Bearer "
            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                // estrae il token eliminando "Bearer "
                String jwt = authHeader.substring(7);

                // decodifica e valida il token
                Claims claims = jwtTokenProvider.parseToken(jwt);

                // prende l'username dal subject
                String username = claims.getSubject();

                // crea autenticazione solo se:
                // 1) username esiste
                // 2) non c'è già un utente autenticato nel contesto
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // estrae i ruoli dal token e li trasforma in authorities Spring
                    List<SimpleGrantedAuthority> authorities = extractRolesFromClaims(claims);

                    // log per debug
                    System.out.println("USERNAME JWT: " + username);
                    System.out.println("ROLES JWT: " + claims.get("roles"));
                    System.out.println("AUTHORITIES ESTRATTE: " + authorities);

                    // crea l'oggetto Authentication
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    // aggiunge i dettagli della request corrente
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // salva l'autenticazione nel contesto Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            // se il token è non valido o scaduto, non autentica nessuno
            System.out.println("ERRORE JWT: " + e.getMessage());
        }

        // continua la catena dei filtri
        filterChain.doFilter(request, response);
    }

    // converte il claim "roles" del token in authorities Spring
    private List<SimpleGrantedAuthority> extractRolesFromClaims(Claims claims) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // legge il claim "roles"
        Object rolesObject = claims.get("roles");

        // controlla che sia una lista
        if (rolesObject instanceof List<?> rolesList) {
            for (Object role : rolesList) {

                String roleName = String.valueOf(role);
                // se il ruolo non inizia con ROLE_, lo aggiunge
                if (!roleName.startsWith("ROLE_")) {
                    roleName = "ROLE_" + roleName;
                }

                // aggiunge il ruolo alla lista delle authorities
                authorities.add(new SimpleGrantedAuthority(roleName));
            }
        }

        return authorities;
    }
}