package formulAI.project.bank.banqueCredit.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private Long id;
    private String nom;
    private String email;
    private Double revenuMensuel;
    private Double chargesMensuelles;
    private String situationProfessionnelle;
}
