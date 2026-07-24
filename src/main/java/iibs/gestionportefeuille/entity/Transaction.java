package iibs.gestionportefeuille.entity;

import iibs.gestionportefeuille.entity.enums.StatutTransaction;
import iibs.gestionportefeuille.entity.enums.TypeTransaction;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "transactions",
    indexes = {
        @Index(name = "idx_transaction_portefeuille", columnList = "portefeuille_id"),
        @Index(name = "idx_transaction_date", columnList = "date_transaction")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "portefeuille_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_transaction_portefeuille")
    )
    private Portefeuille portefeuille;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeTransaction type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_transaction", nullable = false, updatable = false)
    private LocalDateTime dateTransaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StatutTransaction statut;

    @PrePersist
    protected void prePersist() {
        if (this.dateTransaction == null) {
            this.dateTransaction = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutTransaction.SUCCES;
        }
    }
}