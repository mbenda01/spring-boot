package iibs.gestionportefeuille.service;

import iibs.gestionportefeuille.controller.dto.AuthResponse;
import iibs.gestionportefeuille.controller.dto.LoginRequest;
import iibs.gestionportefeuille.controller.dto.RefreshRequest;
import iibs.gestionportefeuille.controller.dto.RegisterRequest;

public interface AuthService {

    AuthResponse enregistrer(RegisterRequest request);

    AuthResponse connecter(LoginRequest request);

    AuthResponse rafraichir(RefreshRequest request);
}