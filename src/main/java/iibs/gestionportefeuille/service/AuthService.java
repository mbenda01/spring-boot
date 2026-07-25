package iibs.gestionportefeuille.service;

import iibs.gestionportefeuille.controller.dto.*;

public interface AuthService {

    AuthResponse enregistrer(RegisterRequest request);

    AuthResponse connecter(LoginRequest request);
}
