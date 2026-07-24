package iibs.gestionportefeuille.entity;

import iibs.gestionportefeuille.entity.enums.Devise;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "portefeuilles",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_portefeuille_utilisateur_devise",
        columnNames = {"utilisateur_id", "devise"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Portefeuille {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "utilisateur_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_portefeuille_utilisateur")
    )
    private Utilisateur utilisateur;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal solde;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Devise devise;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void prePersist() {
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        if (this.solde == null) {
            this.solde = BigDecimal.ZERO;
        }
    }
}