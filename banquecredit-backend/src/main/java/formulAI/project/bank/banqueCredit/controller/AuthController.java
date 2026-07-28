package formulAI.project.bank.banqueCredit.controller;

import formulAI.project.bank.banqueCredit.dto.LoginRequest;
import formulAI.project.bank.banqueCredit.dto.LoginResponse;
import formulAI.project.bank.banqueCredit.dto.UserInfoResponse;
import formulAI.project.bank.banqueCredit.service.AuthService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserInfoResponse me(@Parameter(hidden = true) Authentication authentication) {
        return authService.getCurrentUser(authentication.getName());
    }
}
