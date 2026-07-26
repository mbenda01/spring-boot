package iibs.gestionportefeuille.service;

import iibs.gestionportefeuille.controller.dto.*;
import iibs.gestionportefeuille.entity.enums.Devise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PortefeuilleService {

    PortefeuilleResponseDto creer(PortefeuilleCreationDto dto);

    PortefeuilleResponseDto trouverParId(Long id);

    Page<PortefeuilleResponseDto> lister(Long utilisateurId, Devise devise, Pageable pageable);
}