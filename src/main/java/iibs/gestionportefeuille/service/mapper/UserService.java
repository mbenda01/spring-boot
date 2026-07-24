package iibs.gestionportefeuille.service;

import iibs.gestionportefeuille.controller.dto.*;

import java.util.List;

public interface UserService {

    UserResponseDto creer(UserCreationDto dto);

    UserResponseDto trouverParId(Long id);

    List<UserResponseDto> lister(String nom, String email, int page, int size);
}