package iibs.gestionportefeuille.service.impl;

import iibs.gestionportefeuille.controller.dto.*;
import iibs.gestionportefeuille.entity.Utilisateur;
import iibs.gestionportefeuille.exception.IdentifiantsInvalidesException;
import iibs.gestionportefeuille.repository.UtilisateurRepository;
import iibs.gestionportefeuille.security.*;
import iibs.gestionportefeuille.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
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

        // Étape 8 : getLoginByUsername → UserDetails
        UserDetails userDetails = utilisateurDetailsService.loadUserByUsername(email);

        // Étapes 9-10 : findUserByEmail → user
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new IdentifiantsInvalidesException(
                        "Email ou mot de passe incorrect"));

        // Étape 11 : validerPassword
        if (!passwordEncoder.matches(request.motDePasse(), userDetails.getPassword())) {
            throw new IdentifiantsInvalidesException("Email ou mot de passe incorrect");
        }

        // Étape 12 : generateToken(user)
        String token = jwtService.genererToken(userDetails);

        // Étapes 13-14 : token
        return construire(token, utilisateur);
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