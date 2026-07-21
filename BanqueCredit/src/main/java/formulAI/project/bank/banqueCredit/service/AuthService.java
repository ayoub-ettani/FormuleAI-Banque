package formulAI.project.bank.banqueCredit.service;


import formulAI.project.bank.banqueCredit.dto.LoginRequest;
import formulAI.project.bank.banqueCredit.dto.LoginResponse;
import formulAI.project.bank.banqueCredit.dto.UserInfoResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    UserInfoResponse getCurrentUser(String username);
}
