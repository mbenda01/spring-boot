package iibs.gestionportefeuille.service.impl;

import iibs.gestionportefeuille.controller.dto.*;
import iibs.gestionportefeuille.entity.Utilisateur;
import iibs.gestionportefeuille.exception.IdentifiantsInvalidesException;
import iibs.gestionportefeuille.repository.UtilisateurRepository;
import iibs.gestionportefeuille.security.*;
import iibs.gestionportefeuille.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurDetailsService utilisateurDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse enregistrer(RegisterRequest request) {
        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.nom().trim())
                .email(request.email().trim().toLowerCase())
                .motDePasse(passwordEncoder.encode(request.motDePasse()))
                .build();

        Utilisateur enregistre = utilisateurRepository.save(utilisateur);
        String token = jwtService.genererToken(new UtilisateurDetails(enregistre));

        return construire(token, enregistre);
    }

    @Override
    public AuthResponse connecter(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        // Étape 8 : loadUserByUsername → UtilisateurDetails (encapsule déjà l'entité Utilisateur,
        // pas besoin d'un second aller-retour en base via le repository)
        UtilisateurDetails utilisateurDetails =
                (UtilisateurDetails) utilisateurDetailsService.loadUserByUsername(email);

        // Étape 11 : validerPassword
        if (!passwordEncoder.matches(request.motDePasse(), utilisateurDetails.getPassword())) {
            throw new IdentifiantsInvalidesException("Email ou mot de passe incorrect");
        }

        // Étape 12 : generateToken(user)
        String token = jwtService.genererToken(utilisateurDetails);

        // Étapes 13-14 : construction de la réponse à partir de l'Utilisateur porté par UtilisateurDetails
        return construire(token, utilisateurDetails.getUtilisateur());
    }

    private AuthResponse construire(String token, Utilisateur utilisateur) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .email(utilisateur.getEmail())
                .build();
    }
}
