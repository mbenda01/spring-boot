package iibs.gestionportefeuille.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public UtilisateurDetails utilisateurConnecte() {
        return (UtilisateurDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public boolean estAdmin() {
        return utilisateurConnecte().getUtilisateur().getRole().name().equals("ADMIN");
    }

    public Long idUtilisateurConnecte() {
        return utilisateurConnecte().getUtilisateur().getId();
    }
}
