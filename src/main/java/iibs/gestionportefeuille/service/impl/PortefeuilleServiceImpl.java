package iibs.gestionportefeuille.service.impl;

import iibs.gestionportefeuille.controller.dto.*;
import iibs.gestionportefeuille.entity.*;
import iibs.gestionportefeuille.entity.enums.Devise;
import iibs.gestionportefeuille.exception.RessourceNonTrouveeException;
import iibs.gestionportefeuille.repository.*;
import iibs.gestionportefeuille.service.PortefeuilleService;
import iibs.gestionportefeuille.service.mapper.PortefeuilleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortefeuilleServiceImpl implements PortefeuilleService {

    private final PortefeuilleRepository portefeuilleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PortefeuilleMapper portefeuilleMapper;

    @Override
    @Transactional
    public PortefeuilleResponseDto creer(PortefeuilleCreationDto dto) {
        Utilisateur utilisateur = utilisateurRepository.findById(dto.utilisateurId())
                .orElseThrow(() -> new RessourceNonTrouveeException(
                        "Aucun utilisateur trouvé avec l'identifiant " + dto.utilisateurId()));

        Portefeuille portefeuille = portefeuilleRepository.save(
                portefeuilleMapper.versEntite(dto, utilisateur));

        return portefeuilleMapper.versReponse(portefeuille);
    }

    @Override
    public PortefeuilleResponseDto trouverParId(Long id) {
        return portefeuilleRepository.findWithUtilisateurById(id)
                .map(portefeuilleMapper::versReponse)
                .orElseThrow(() -> new RessourceNonTrouveeException(
                        "Aucun portefeuille trouvé avec l'identifiant " + id));
    }

    @Override
    public List<PortefeuilleResponseDto> lister(Long utilisateurId, Devise devise,
                                                int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());

        return portefeuilleRepository.rechercher(utilisateurId, devise, pageable)
                .stream()
                .map(portefeuilleMapper::versReponse)
                .toList();
    }
}