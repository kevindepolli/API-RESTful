package models;

import play.data.validation.Constraints;

public class LoginDTO {

    @Constraints.Required(message = "O email é obrigatório")
    @Constraints.Email(message = "Formato de email inválido")
    private String email;

    @Constraints.Required(message = "A senha é obrigatória")
    private String senha;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

}