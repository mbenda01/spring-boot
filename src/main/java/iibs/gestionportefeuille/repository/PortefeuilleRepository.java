package iibs.gestionportefeuille.repository;

import iibs.gestionportefeuille.entity.Portefeuille;
import iibs.gestionportefeuille.entity.enums.Devise;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortefeuilleRepository extends JpaRepository<Portefeuille, Long> {

    boolean existsByUtilisateurIdAndDevise(Long utilisateurId, Devise devise);

    Optional<Portefeuille> findByUtilisateurIdAndDevise(Long utilisateurId, Devise devise);

    @EntityGraph(attributePaths = "utilisateur")
    Optional<Portefeuille> findWithUtilisateurById(Long id);

    @EntityGraph(attributePaths = "utilisateur")
    @Query(value = """
           SELECT p FROM Portefeuille p
           WHERE (:utilisateurId IS NULL OR p.utilisateur.id = :utilisateurId)
             AND (:devise IS NULL OR p.devise = :devise)
           """,
           countQuery = """
           SELECT COUNT(p) FROM Portefeuille p
           WHERE (:utilisateurId IS NULL OR p.utilisateur.id = :utilisateurId)
             AND (:devise IS NULL OR p.devise = :devise)
           """)
    Page<Portefeuille> rechercher(@Param("utilisateurId") Long utilisateurId,
                                   @Param("devise") Devise devise,
                                   Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Portefeuille p WHERE p.id = :id")
    Optional<Portefeuille> findByIdPourMiseAJour(@Param("id") Long id);
}