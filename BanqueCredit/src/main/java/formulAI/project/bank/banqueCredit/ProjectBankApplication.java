package formulAI.project.bank.banqueCredit;

import formulAI.project.bank.banqueCredit.model.Role;
import formulAI.project.bank.banqueCredit.model.User;
import formulAI.project.bank.banqueCredit.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ProjectBankApplication {


	public static void main(String[] args) {
		SpringApplication.run(ProjectBankApplication.class, args);
	}


	// Cree deux utilisateurs de test au demarrage in case la table est vide
	@Bean
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.count() == 0) {
				User conseiller = new User();
				conseiller.setUsername("conseiller");
				conseiller.setPassword(passwordEncoder.encode("conseiller123"));
				conseiller.setRole(Role.ROLE_USER);
				conseiller.setNomComplet("Jean Conseiller");
				userRepository.save(conseiller);

				User manager = new User();
				manager.setUsername("manager");
				manager.setPassword(passwordEncoder.encode("manager123"));
				manager.setRole(Role.ROLE_MANAGER);
				manager.setNomComplet("Marie Responsable");
				userRepository.save(manager);
			}
		};
	}
}
