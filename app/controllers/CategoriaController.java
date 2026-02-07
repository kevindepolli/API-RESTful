package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import model.Categoria;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.CategoriaService;

import javax.inject.Inject;
import java.util.List;

public class CategoriaController extends Controller {

    private final CategoriaService service;

    @Inject
    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    //GET /categorias
    public Result listarCategorias() {
        List<Categoria> categorias = service.listarCategorias();
        return ok(Json.toJson(categorias));
    }

    //GET /categorias/:id
    public Result buscarCategoria(Long id) {
        try {
            Categoria categoria = service.buscarCategoria(id);
            return ok(Json.toJson(categoria));
        } catch (RuntimeException e) {
            return notFound(e.getMessage());
        }
    }

    //POST /categorias
    public Result criarCategoria(Http.Request request) {
        JsonNode json = request.body().asJson();

        if (json == null) {
            return badRequest("JSON inválido");
        }

        Categoria categoria = Json.fromJson(json, Categoria.class);

        try {
            Categoria nova = service.criarCategoria(categoria);
            return created(Json.toJson(nova));
        } catch (RuntimeException e) {
            return badRequest(e.getMessage());
        }
    }

    //PUT /categorias/:id
    public Result atualizarCategoria(Long id, Http.Request request) {
        JsonNode json = request.body().asJson();

        if (json == null) {
            return badRequest("JSON inválido");
        }

        Categoria dadosAtualizados = Json.fromJson(json, Categoria.class);

        try {
            Categoria atualizada = service.atualizarCategoria(id, dadosAtualizados);
            return ok(Json.toJson(atualizada));
        } catch (RuntimeException e) {
            return badRequest(e.getMessage());
        }
    }

    //DELETE /categorias/:id
    public Result deletarCategoria(Long id) {
        try {
            service.deletarCategoria(id);
            return noContent();
        } catch (RuntimeException e) {
            return notFound(e.getMessage());
        }
    }
}
