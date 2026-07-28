package formulAI.project.bank.banqueCredit.dto;
import formulAI.project.bank.banqueCredit.model.SituationClient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ClientDTO
{
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @Email(message = "Format email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotNull(message = "Le revenu mensuel est obligatoire")
    @PositiveOrZero(message = "Le revenu ne peut pas être négatif")
    private Double revenuMensuel;

    @NotNull(message = "Les charges mensuelles sont obligatoires")
    @PositiveOrZero(message = "Les charges ne peuvent pas être négatives")
    private Double chargesMensuelles;

    @NotNull(message = "La situation professionnelle est obligatoire")
    private SituationClient situationProfessionnelle;
}
