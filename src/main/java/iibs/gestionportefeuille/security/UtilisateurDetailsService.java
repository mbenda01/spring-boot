package iibs.gestionportefeuille.security;

import iibs.gestionportefeuille.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilisateurDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return utilisateurRepository.findByEmail(email.trim().toLowerCase())
                .map(UtilisateurDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Aucun utilisateur avec l'email : " + email));
    }
}