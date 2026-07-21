package formulAI.project.bank.banqueCredit.service;


import formulAI.project.bank.banqueCredit.dto.LoginRequest;
import formulAI.project.bank.banqueCredit.dto.LoginResponse;
import formulAI.project.bank.banqueCredit.dto.UserInfoResponse;
import formulAI.project.bank.banqueCredit.model.User;
import formulAI.project.bank.banqueCredit.repository.UserRepository;
import formulAI.project.bank.banqueCredit.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Identifiants invalides"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Identifiants invalides");
        }

        String token = jwtUtils.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, user.getUsername(), user.getRole().name());
    }

    public UserInfoResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        return new UserInfoResponse(user.getUsername(), user.getRole().name(), user.getNomComplet());
    }
}
