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
        UserDetails userDetails = new UtilisateurDetails(enregistre);

        return construire(userDetails, enregistre);
    }

    @Override
    public AuthResponse connecter(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        UserDetails userDetails = utilisateurDetailsService.loadUserByUsername(email);

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new IdentifiantsInvalidesException(
                        "Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.motDePasse(), userDetails.getPassword())) {
            throw new IdentifiantsInvalidesException("Email ou mot de passe incorrect");
        }

        return construire(userDetails, utilisateur);
    }

    @Override
    public AuthResponse rafraichir(RefreshRequest request) {
        String token = request.refreshToken();

        if (!jwtService.estRefreshValide(token)) {
            throw new IdentifiantsInvalidesException(
                    "Refresh token invalide ou expiré");
        }

        String email = jwtService.extraireEmail(token);
        UserDetails userDetails = utilisateurDetailsService.loadUserByUsername(email);

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new IdentifiantsInvalidesException(
                        "Utilisateur introuvable"));

        return construire(userDetails, utilisateur);
    }

    private AuthResponse construire(UserDetails userDetails, Utilisateur utilisateur) {
        return AuthResponse.builder()
                .accessToken(jwtService.genererAccessToken(userDetails))
                .refreshToken(jwtService.genererRefreshToken(userDetails))
                .type("Bearer")
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .email(utilisateur.getEmail())
                .build();
    }
}