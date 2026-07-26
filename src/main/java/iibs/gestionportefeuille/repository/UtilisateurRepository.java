package iibs.gestionportefeuille.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import iibs.gestionportefeuille.entity.Utilisateur;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
           SELECT u FROM Utilisateur u
           WHERE (:nom IS NULL OR LOWER(u.nom) LIKE LOWER(CONCAT('%', :nom, '%')))
             AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
           """)
    Page<Utilisateur> rechercher(@Param("nom") String nom,
                                  @Param("email") String email,
                                  Pageable pageable);
}