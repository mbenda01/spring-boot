package iibs.gestionportefeuille.service;

import iibs.gestionportefeuille.controller.dto.*;
import iibs.gestionportefeuille.entity.enums.Devise;

import java.util.List;

public interface PortefeuilleService {

    PortefeuilleResponseDto creer(PortefeuilleCreationDto dto);

    PortefeuilleResponseDto trouverParId(Long id);

    List<PortefeuilleResponseDto> lister(Long utilisateurId, Devise devise, int page, int size);
}