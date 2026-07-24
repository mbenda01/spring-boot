package iibs.gestionportefeuille.repository;

import iibs.gestionportefeuille.entity.Transaction;
import iibs.gestionportefeuille.entity.enums.StatutTransaction;
import iibs.gestionportefeuille.entity.enums.TypeTransaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByPortefeuilleId(Long portefeuilleId);

    List<Transaction> findByPortefeuilleIdAndTypeAndStatut(
            Long portefeuilleId,
            TypeTransaction type,
            StatutTransaction statut);

    List<Transaction> findByPortefeuilleIdAndDateTransactionBetween(
            Long portefeuilleId,
            LocalDateTime debut,
            LocalDateTime fin);

    @Query("""
           SELECT COALESCE(SUM(t.montant), 0)
           FROM Transaction t
           WHERE t.portefeuille.id = :portefeuilleId
             AND t.type = :type
             AND t.statut = iibs.gestionportefeuille.entity.enums.StatutTransaction.SUCCES
           """)
    BigDecimal totalParType(@Param("portefeuilleId") Long portefeuilleId,
                            @Param("type") TypeTransaction type);
}