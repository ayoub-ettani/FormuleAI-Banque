package formulAI.project.bank.banqueCredit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueResponse {
    private Long id;
    private Long demandeCreditId;
    private String ancienStatut;
    private String nouveauStatut;
    private String commentaire;
    private String auteur;
    private LocalDateTime date;
}