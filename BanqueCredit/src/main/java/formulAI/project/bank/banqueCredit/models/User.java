package formulAI.project.bank.banqueCredit.models;

import formulAI.project.bank.banqueCredit.Enumerations.RoleUsers;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    private String email;
    private String password;
    private RoleUsers roleUsers;

    @OneToMany(mappedBy = "user")
    private List<HistoriqueDecision> historiques;

}
