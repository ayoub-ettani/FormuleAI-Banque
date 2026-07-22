package formulAI.project.bank.banqueCredit.mapper;

import formulAI.project.bank.banqueCredit.dto.HistoriqueResponse;
import formulAI.project.bank.banqueCredit.model.HistoriqueDecision;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoriqueDecisionMapper {

    @Mapping(source = "demandeCredit.id", target = "demandeCreditId")
    @Mapping(source = "auteur", target = "auteur")
    HistoriqueResponse toResponse(HistoriqueDecision historique);
}