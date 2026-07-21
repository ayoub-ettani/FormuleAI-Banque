package formulAI.project.bank.banqueCredit.service;

import formulAI.project.bank.banqueCredit.DTO.CreateDemandeCreditRequest;
import formulAI.project.bank.banqueCredit.DTO.DemandeCreditResponse;
import formulAI.project.bank.banqueCredit.DTO.UpdateDemandeCreditRequest;

import java.util.List;

public interface IDemandeCreditService {

    DemandeCreditResponse createDemandeCredit(CreateDemandeCreditRequest request);
    DemandeCreditResponse getDemandeCreditById(Long id);
    List<DemandeCreditResponse> getAll();
    DemandeCreditResponse updateDemandeCredit(Long id, UpdateDemandeCreditRequest request);
    void deleteDemandeCredit(Long id);


}