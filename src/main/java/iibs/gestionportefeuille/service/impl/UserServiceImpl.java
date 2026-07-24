package iibs.gestionportefeuille.service.impl;

import iibs.gestionportefeuille.controller.dto.*;
import iibs.gestionportefeuille.entity.Utilisateur;
import iibs.gestionportefeuille.exception.RessourceNonTrouveeException;
import iibs.gestionportefeuille.repository.UtilisateurRepository;
import iibs.gestionportefeuille.service.UserService;
import iibs.gestionportefeuille.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UtilisateurRepository utilisateurRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto creer(UserCreationDto dto) {
        Utilisateur utilisateur = utilisateurRepository.save(userMapper.versEntite(dto));
        return userMapper.versReponse(utilisateur);
    }

    @Override
    public UserResponseDto trouverParId(Long id) {
        return utilisateurRepository.findById(id)
                .map(userMapper::versReponse)
                .orElseThrow(() -> new RessourceNonTrouveeException(
                        "Aucun utilisateur trouvé avec l'identifiant " + id));
    }

    @Override
    public List<UserResponseDto> lister(String nom, String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());

        return utilisateurRepository.rechercher(nettoyer(nom), nettoyer(email), pageable)
                .stream()
                .map(userMapper::versReponse)
                .toList();
    }

    private String nettoyer(String valeur) {
        return (valeur == null || valeur.isBlank()) ? null : valeur.trim();
    }
}