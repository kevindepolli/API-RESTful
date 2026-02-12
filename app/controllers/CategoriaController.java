package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.security.Secured;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Categoria;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.CategoriaService;

import javax.inject.Inject;
import java.util.List;
@Secured
public class CategoriaController extends Controller {

    private final CategoriaService service;

    @Inject
    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    //GET /categorias
//    public Result listarCategorias() {
//        List<Categoria> categorias = service.listarCategorias();
//        return ok(Json.toJson(categorias));
//    }

    public Result listarCategorias(Http.Request request) {

        String descricao = request.getQueryString("descricao");
        int page = Integer.parseInt(request.getQueryString("page") != null ? request.getQueryString("page") : "0");
        int size = Integer.parseInt(request.getQueryString("size") != null ? request.getQueryString("size") : "10");

        List<Categoria> categorias = service.listarComFiltro(descricao, page, size);
        long total = service.contarComFiltro(descricao);

        ObjectNode resposta = Json.newObject();
        resposta.put("page", page);
        resposta.put("size", size);
        resposta.put("total", total);
        resposta.set("dados", Json.toJson(categorias));

        return ok(resposta);
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
    @Secured("ADMIN")
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
    @Secured("ADMIN")
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
    @Secured("ADMIN")
    public Result deletarCategoria(Long id) {
        try {
            service.deletarCategoria(id);
            return noContent();
        } catch (RuntimeException e) {
            return notFound(e.getMessage());
        }
    }
}
