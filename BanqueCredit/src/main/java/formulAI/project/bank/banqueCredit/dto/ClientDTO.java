package formulAI.project.bank.banqueCredit.dto;
import formulAI.project.bank.banqueCredit.model.SituationClient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ClientDTO
{
    @NotNull(message = "Le nom est obligatoire")
    private String nom;

    @Email(message = "Format email invalide")
    @NotNull(message = "L'email est obligatoire")
    private String email;

    @NotNull(message = "Le revenu mensuel est obligatoire")
    private Double revenuMensuel;

    @NotNull(message = "Les charges mensuelles sont obligatoires")
    private Double chargesMensuelles;

    @NotNull(message = "La situation professionnelle est obligatoire")
    private SituationClient situationProfessionnelle;
}
