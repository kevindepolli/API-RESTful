package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Usuario;
import models.LoginDTO;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.*;
import repository.UsuarioRepository;
import services.TokenService;
import jakarta.inject.Inject;
import java.util.Optional;

public class AuthController extends Controller {

    private final UsuarioRepository repository;
    private final TokenService tokenService;
    private final FormFactory formFactory;

    @Inject
    public AuthController(UsuarioRepository repository, TokenService tokenService, FormFactory formFactory) {
        this.repository = repository;
        this.tokenService = tokenService;
        this.formFactory = formFactory;
    }

    public Result login(Http.Request request) {
        Form<LoginDTO> loginForm = formFactory.form(LoginDTO.class).bindFromRequest(request);

        if (loginForm.hasErrors()) {
            return badRequest(loginForm.errorsAsJson());
        }

        LoginDTO loginData = loginForm.get();

        Optional<Usuario> usuarioOpt = repository.findByEmail(loginData.getEmail());

        if (usuarioOpt.isPresent() && usuarioOpt.get().checkSenha(loginData.getSenha())) {
            Usuario usuario = usuarioOpt.get();

            String token = tokenService.signToken(usuario);

            ObjectNode response = Json.newObject();
            response.put("token", token);
            response.put("usuario", usuario.getName());

            return ok(response);
        } else {
            return unauthorized(Json.newObject().put("error", "Credenciais inválidas"));
        }
    }
}