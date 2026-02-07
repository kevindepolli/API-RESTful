package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.PagedResult;
import models.Usuario;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.UsuarioRepository;

import javax.inject.Inject;
import java.util.Optional;

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
            return notFound(Json.newObject().put("message", "Usuário não encontrado"));
        }
        return ok(Json.toJson(usuario));
    }

    // POST /usuarios
    public Result create(Http.Request request) {
        JsonNode json = request.body().asJson();

        if (json == null) {
            return badRequest(Json.newObject().put("message", "JSON inválido"));
        }

        // Converte JSON -> Objeto Java
        Usuario usuario = Json.fromJson(json, Usuario.class);

        // Salva no banco
        Usuario salvo = usuarioRepository.create(usuario);

        return created(Json.toJson(salvo));
    }

    // PUT /usuarios/:id
    public Result update(Long id, Http.Request request) {
        JsonNode json = request.body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        }

        Usuario usuarioParaAtualizar = Json.fromJson(json, Usuario.class);

        // Garante que o ID do objeto é o mesmo da URL
        usuarioParaAtualizar.setId(id);

        // Verifica se existe antes de tentar atualizar (opcional, mas recomendado)
        if (usuarioRepository.findById(id) == null) {
            return notFound(Json.newObject().put("message", "Usuário não existe para ser atualizado"));
        }

        Usuario atualizado = usuarioRepository.update(usuarioParaAtualizar);
        return ok(Json.toJson(atualizado));
    }

    // DELETE /usuarios/:id
    public Result delete(Long id) {
        boolean deletado = usuarioRepository.delete(id);
        if (deletado) {
            return ok(Json.newObject().put("message", "Usuário deletado com sucesso"));
        } else {
            return notFound(Json.newObject().put("message", "Usuário não encontrado"));
        }
    }

    //GET /usuarios/:email
    public Result findByEmail(String email) {
        // Busca exata pelo email
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isEmpty()) {
            return notFound(Json.newObject().put("message", "Usuário não encontrado"));
        }
        return ok(Json.toJson(usuario));
    }

}