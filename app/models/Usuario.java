package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;


@Table(name = "USUARIO")
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;


    @Constraints.Required
    @Column(name = "NOME")
    private String name;
    @Constraints.Required
    @Constraints.Email
    @Column(unique = true, name = "EMAIL")
    private String email;

    public enum Role {
        ADMIN,
        USER
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false, columnDefinition = "varchar(20) default 'USER'")
    private Role role = Role.USER;

    @Constraints.Required
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "SENHA")
    private String senha;

    public void setSenha(String rawPassword) {
        this.senha = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public boolean checkSenha(String rawPassword) {
        if (this.senha == null) return false;
        return BCrypt.checkpw(rawPassword, this.senha);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
