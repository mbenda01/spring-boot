package iibs.gestionportefeuille.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre JWT exécuté une seule fois par requête (OncePerRequestFilter),
 * enregistré via {@code addFilterBefore} dans {@link iibs.gestionportefeuille.config.SecurityConfig}.
 *
 * Implémente le diagramme de séquence "Validation d'un token sur une ressource protégée" :
 *  1-2. La SecurityFilterChain applique ce filtre avant d'atteindre le contrôleur.
 *  3-4. extractUsername(token) → JwtService extrait l'email (subject du JWT).
 *  5-6. loadUserByUsername(email) → UtilisateurDetailsService renvoie un UtilisateurDetails
 *       (implémentation propre au domaine, pas un simple UserDetails générique).
 *  7-8. isTokenValid(token, UtilisateurDetails) → vérifie signature + expiration.
 *  9.   Enregistre l'authentification dans le SecurityContext.
 *  10.  Redonne la main à la chaîne de filtres (doFilter).
 *  11-13. La ressource protégée est atteinte, la réponse redescend jusqu'au client.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UtilisateurDetailsService utilisateurDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String enTete = request.getHeader("Authorization");

        if (enTete == null || !enTete.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = enTete.substring(7);

        // Étapes 3-4 : extractUsername(token) -> "username"
        String email = jwtService.extraireEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Étapes 5-6 : loadUserByUsername("username") -> UtilisateurDetails
            // (Note du prof : interroger la DB ici contredit le "pur" Stateless,
            // mais permet de révoquer instantanément un compte banni/désactivé.)
            UtilisateurDetails utilisateurDetails =
                    (UtilisateurDetails) utilisateurDetailsService.loadUserByUsername(email);

            // Étapes 7-8 : isTokenValid(token, UtilisateurDetails) -> signature OK, non expiré
            if (jwtService.estValide(token, utilisateurDetails)) {

                // Étape 9 : enregistre l'authentification dans le SecurityContext
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                utilisateurDetails, null, utilisateurDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // Étape 10 : redonne la main à la chaîne (doFilter)
        filterChain.doFilter(request, response);
    }
}
