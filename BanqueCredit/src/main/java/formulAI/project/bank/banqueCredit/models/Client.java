package formulAI.project.bank.banqueCredit.models;

import formulAI.project.bank.banqueCredit.Enumerations.SituationClient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String nom;

    @Column(nullable = false, unique = true)
    @NotNull
    private String email;

    @Column(nullable = false)
    @NotNull
    private Double revenuMensuel;

    @Column(nullable = false)
    @NotNull
    private Double chargesMensuelles;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituationClient situationProfessionnelle;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "client")
    private List<DemandeCredit> demandes;

    @Column(nullable = false)
    private Boolean deleted = false;

}
