package iibs.gestionportefeuille.service;

import iibs.gestionportefeuille.controller.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponseDto creer(UserCreationDto dto);

    UserResponseDto trouverParId(Long id);

    Page<UserResponseDto> lister(String nom, String email, Pageable pageable);
}