package formulAI.project.bank.banqueCredit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoResponse {
    private String username;
    private String role;
    private String nomComplet;
}
