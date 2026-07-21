package formulAI.project.bank.banqueCredit.mapper;

import formulAI.project.bank.banqueCredit.dto.DemandeCreditResponse;
import formulAI.project.bank.banqueCredit.model.DemandeCredit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DemandeCreditMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientNom", source = "client.nom")
    @Mapping(target = "statut", expression = "java(entity.getStatut().name())")
    DemandeCreditResponse toResponse(DemandeCredit entity);

}
