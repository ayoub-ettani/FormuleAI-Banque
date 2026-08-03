package formulAI.project.bank.banqueCredit.service.impl;

import formulAI.project.bank.banqueCredit.dto.LoginRequest;
import formulAI.project.bank.banqueCredit.dto.LoginResponse;
import formulAI.project.bank.banqueCredit.dto.UserInfoResponse;
import formulAI.project.bank.banqueCredit.model.Role;
import formulAI.project.bank.banqueCredit.model.User;
import formulAI.project.bank.banqueCredit.repository.UserRepository;
import formulAI.project.bank.banqueCredit.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("conseiller");
        user.setPassword("motDePasseEncode");
        user.setRole(Role.ROLE_USER);
        user.setNomComplet("Jean Conseiller");
    }

    // ---------- Login ----------

    @Test
    void login_identifiantsValides_retourneToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("conseiller");
        request.setPassword("conseiller123");

        when(userRepository.findByUsername("conseiller")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("conseiller123", "motDePasseEncode")).thenReturn(true);
        when(jwtUtils.generateToken("conseiller", "ROLE_USER")).thenReturn("fake-jwt-token");

        LoginResponse response = authService.login(request);

        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("conseiller", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());
    }

    @Test
    void login_utilisateurInexistant_leveRuntimeException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("inconnu");
        request.setPassword("peuImporte");

        when(userRepository.findByUsername("inconnu")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void login_motDePasseIncorrect_leveRuntimeException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("conseiller");
        request.setPassword("mauvaisMotDePasse");

        when(userRepository.findByUsername("conseiller")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("mauvaisMotDePasse", "motDePasseEncode")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    // ---------- getCurrentUser (/me) ----------

    @Test
    void getCurrentUser_utilisateurExistant_retourneInfos() {
        when(userRepository.findByUsername("conseiller")).thenReturn(Optional.of(user));

        UserInfoResponse response = authService.getCurrentUser("conseiller");

        assertEquals("conseiller", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());
        assertEquals("Jean Conseiller", response.getNomComplet());
    }

    @Test
    void getCurrentUser_utilisateurInexistant_leveRuntimeException() {
        when(userRepository.findByUsername("fantome")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.getCurrentUser("fantome"));
    }
}