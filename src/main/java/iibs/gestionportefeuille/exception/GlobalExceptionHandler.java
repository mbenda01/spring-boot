package iibs.gestionportefeuille.exception;

import iibs.gestionportefeuille.controller.dto.ErreurResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurResponse> gererValidationCorps(MethodArgumentNotValidException ex) {
        Map<String, String> champs = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors()
          .forEach(erreur -> champs.put(erreur.getField(), erreur.getDefaultMessage()));

        ex.getBindingResult().getGlobalErrors()
          .forEach(erreur -> champs.put(erreur.getObjectName(), erreur.getDefaultMessage()));

        return construire(HttpStatus.BAD_REQUEST, "Données invalides", champs);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErreurResponse> gererValidationParametres(ConstraintViolationException ex) {
        Map<String, String> champs = new LinkedHashMap<>();
        ex.getConstraintViolations()
          .forEach(v -> champs.put(v.getPropertyPath().toString(), v.getMessage()));

        return construire(HttpStatus.BAD_REQUEST, "Paramètres invalides", champs);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErreurResponse> gererCorpsIllisible(HttpMessageNotReadableException ex) {
        return construire(HttpStatus.BAD_REQUEST,
            "Le corps de la requête est illisible ou contient une valeur non reconnue "
            + "(vérifiez notamment la devise : XOF, EUR ou USD)", null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErreurResponse> gererTypeParametre(MethodArgumentTypeMismatchException ex) {
        return construire(HttpStatus.BAD_REQUEST,
            "Valeur invalide pour le paramètre « " + ex.getName() + " »", null);
    }

    @ExceptionHandler(RessourceNonTrouveeException.class)
    public ResponseEntity<ErreurResponse> gererNonTrouvee(RessourceNonTrouveeException ex) {
        return construire(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(IdentifiantsInvalidesException.class)
    public ResponseEntity<ErreurResponse> gererIdentifiants(IdentifiantsInvalidesException ex) {
        return construire(HttpStatus.UNAUTHORIZED, ex.getMessage(), null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErreurResponse> gererIntegrite(DataIntegrityViolationException ex) {
        return construire(HttpStatus.CONFLICT,
            "L'opération viole une contrainte d'intégrité de la base", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurResponse> gererInattendue(Exception ex) {
        return construire(HttpStatus.INTERNAL_SERVER_ERROR,
            "Une erreur interne est survenue", null);
    }

    private ResponseEntity<ErreurResponse> construire(HttpStatus statut,
                                                     String message,
                                                     Map<String, String> champs) {
        ErreurResponse corps = ErreurResponse.builder()
                .horodatage(LocalDateTime.now())
                .statut(statut.value())
                .erreur(statut.getReasonPhrase())
                .message(message)
                .champs(champs)
                .build();

        return ResponseEntity.status(statut).body(corps);
    }
    @ExceptionHandler(AccesRefuseException.class)
    public ResponseEntity<ErreurResponse> gererAccesRefuse(AccesRefuseException ex) {
        return construire(HttpStatus.FORBIDDEN, ex.getMessage(), null);
    }
}