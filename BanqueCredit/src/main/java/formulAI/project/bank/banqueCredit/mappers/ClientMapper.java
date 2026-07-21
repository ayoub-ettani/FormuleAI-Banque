package formulAI.project.bank.banqueCredit.mappers;

import formulAI.project.bank.banqueCredit.dto.ClientDTO;
import formulAI.project.bank.banqueCredit.model.Client;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientDTO toDTO(Client client);
    Client toEntity(ClientDTO clientDTO);
}
