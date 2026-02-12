package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.security.Secured;
import controllers.security.SecuredAction;
import models.PagedResult;
import models.Usuario;
import models.Usuario.Role;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.UsuarioRepository;

import javax.inject.Inject;
import java.util.Optional;
@Secured({"ROOT", "ADMIN"})
public class UsuarioController extends Controller {

    private final UsuarioRepository usuarioRepository;

    @Inject
    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // GET /usuarios?page=1&termo=
    public Result list(Http.Request request) {
        // Pega parâmetros da URL ou usa padrão
        int page = 1;
        String pageStr = request.getQueryString("page");
        if (pageStr != null) page = Integer.parseInt(pageStr);

        String termo = request.getQueryString("termo");
        int pageSize = 10;

        PagedResult<Usuario> result = usuarioRepository.findList(page, pageSize, termo);

        return ok(Json.toJson(result));
    }

    // GET /usuarios/:id
    public Result getById(Long id) {
        Usuario usuario = usuarioRepository.findById(id);
        if (usuario == null) {
            return notFound(Json.newObject()
                    .put("codigo", "USUARIO_NAO_EXISTE")
                    .put("message", "Usuário não encontrado"));
        }
        return ok(Json.toJson(usuario));
    }

    // POST /usuarios
    public Result create(Http.Request request) {
        JsonNode json = request.body().asJson();

        if (json == null) {
            return badRequest(Json.newObject()
                    .put("codigo", "JSON_INVALIDO")
                    .put("message", "JSON inválido"));
        }

        Usuario usuario = Json.fromJson(json, Usuario.class);

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return status(Http.Status.CONFLICT, Json.newObject() // Retorna 409 Conflict
                    .put("codigo", "EMAIL_JA_EXISTE")
                    .put("erro", "O e-mail informado já está cadastrado no sistema."));
        }

        String roleString = request.attrs().get(SecuredAction.USER_ROLE);
        Role cargoDoLogado = Role.valueOf(roleString);


        if ((usuario.getRole() == Role.ADMIN || usuario.getRole() == Role.ROOT)
                && cargoDoLogado != Role.ROOT) {

            return forbidden(Json.newObject()
                    .put("codigo", "APENAS_ROOT")
                    .put("erro",
                    "Apenas o ROOT pode criar novos administradores."));
        }

        Usuario salvo = usuarioRepository.create(usuario);

        return created(Json.toJson(salvo));
    }

    // PUT /usuarios/:id
    public Result update(Long id, Http.Request request) {
        JsonNode json = request.body().asJson();
        if (json == null) {
            return badRequest(Json.newObject()
                    .put("codigo", "SEM_JSON")
                    .put("erro",
                            "JSON não encontrado."));
        }

        Usuario usuarioParaAtualizar = Json.fromJson(json, Usuario.class);

        usuarioParaAtualizar.setId(id);

        if (usuarioRepository.findById(id) == null) {
            return notFound(Json.newObject()
                    .put("codigo", "USUARIO_NAO_EXISTE")
                    .put("message", "Usuário não existe para ser atualizado"));
        }

        Usuario atualizado = usuarioRepository.update(usuarioParaAtualizar);
        return ok(Json.toJson(atualizado));
    }

    // DELETE /usuarios/:id
    public Result delete(Long id, Http.Request request) {
        String roleString = request.attrs().get(SecuredAction.USER_ROLE);
        Role cargoDoLogado = Role.valueOf(roleString);

        Usuario usuarioAlvo = usuarioRepository.findById(id);
        if (usuarioAlvo == null) {
            return notFound(Json.newObject()
                    .put("codigo", "USUARIO_NAO_EXISTE")
                    .put("message", "Usuário não encontrado"));
        }

        String emailLogado = request.attrs().get(SecuredAction.USER_EMAIL);
        if (usuarioAlvo.getEmail().equals(emailLogado)) {
            return badRequest(Json.newObject().put("codigo", "AUTO_DELECAO_PROIBIDA")
                    .put("erro", "Você não pode deletar a si mesmo."));
        }

        if (usuarioAlvo.getRole() == Role.ROOT) {
            return forbidden(Json.newObject()
                    .put("codigo", "ROOT_NAO_PODE_SER_DELETADO")
                    .put("erro",
                    "Ação Proibida: O Usuário ROOT não pode ser deletado via API."));
        }

        if (cargoDoLogado != Role.ROOT && usuarioAlvo.getRole() == Role.ADMIN) {
            return forbidden(Json.newObject()
                    .put("codigo", "APENAS_ROOT_PODE_DELETAR_ADMIN")
                    .put("erro",
                    "Ação Proibida: Apenas o ROOT pode deletar Administradores."));
        }

        boolean deletado = usuarioRepository.delete(id);
        if (deletado) {
            return ok(Json.newObject()
                    .put("codigo", "USUARIO_DELETADO")
                    .put("message", "Usuário deletado com sucesso"));
        } else {
            return notFound(Json.newObject()
                    .put("codigo", "USUARIO_NAO_EXISTE")
                    .put("message", "Usuário não encontrado"));
        }
    }

    //GET /usuarios/:email
    public Result findByEmail(String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isEmpty()) {
            return notFound(Json.newObject()
                    .put("codigo", "USUARIO_NAO_EXISTE")
                    .put("message", "Usuário não encontrado"));
        }
        return ok(Json.toJson(usuario));
    }

}