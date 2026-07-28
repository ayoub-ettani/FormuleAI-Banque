package formulAI.project.bank.banqueCredit.service.impl;


import formulAI.project.bank.banqueCredit.dto.LoginRequest;
import formulAI.project.bank.banqueCredit.dto.LoginResponse;
import formulAI.project.bank.banqueCredit.dto.UserInfoResponse;
import formulAI.project.bank.banqueCredit.exception.ResourceNotFoundException;
import formulAI.project.bank.banqueCredit.model.User;
import formulAI.project.bank.banqueCredit.repository.UserRepository;
import formulAI.project.bank.banqueCredit.security.JwtUtils;
import formulAI.project.bank.banqueCredit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Identifiants invalides"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Identifiants invalides");
        }

        String token = jwtUtils.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, user.getUsername(), user.getRole().name());
    }

    @Override
    public UserInfoResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        return new UserInfoResponse(user.getUsername(), user.getRole().name(), user.getNomComplet());
    }
}
