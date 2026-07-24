package iibs.gestionportefeuille.repository;

import iibs.gestionportefeuille.entity.Utilisateur;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Utilisateur> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    List<Utilisateur> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    @Query("""
           SELECT u FROM Utilisateur u
           WHERE (:nom IS NULL OR LOWER(u.nom) LIKE LOWER(CONCAT('%', :nom, '%')))
             AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
           """)
    List<Utilisateur> rechercher(@Param("nom") String nom,
                                 @Param("email") String email,
                                 Pageable pageable);
}