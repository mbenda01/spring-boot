package iibs.gestionportefeuille.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import iibs.gestionportefeuille.controller.dto.UserCreationDto;
import iibs.gestionportefeuille.controller.dto.UserResponseDto;
import iibs.gestionportefeuille.entity.Utilisateur;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "nom", expression = "java(dto.nom().trim())")
    @Mapping(target = "email", expression = "java(dto.email().trim().toLowerCase())")
    Utilisateur versEntite(UserCreationDto dto);

    UserResponseDto versReponse(Utilisateur utilisateur);
}