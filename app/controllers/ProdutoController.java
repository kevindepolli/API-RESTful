package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.security.Secured;
import models.PagedResult;
import models.Produto;
import play.libs.Json;
import play.mvc.*;
import repository.ProdutoRepository;

import javax.inject.Inject;

@Secured
public class ProdutoController extends Controller {

    private final ProdutoRepository produtoRepository;

    @Inject
    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // GET /produtos?page=1&termo=celular
    public Result list(Http.Request request) {
        // Pega parametros da URL ou usa padrão
        int page = 1;
        String pageStr = request.getQueryString("page");
        if (pageStr != null) page = Integer.parseInt(pageStr);

        String termo = request.getQueryString("termo");
        int pageSize = 10;

        PagedResult<Produto> result = produtoRepository.findList(page, pageSize, termo);

        return ok(Json.toJson(result));
    }

    // GET /produtos/:id
    public Result getById(Long id) {
        Produto produto = produtoRepository.findById(id);
        if (produto == null) {
            return notFound(Json.newObject().put("message", "Produto não encontrado"));
        }
        return ok(Json.toJson(produto));
    }

    // POST /produtos
    @Secured("ADMIN")
    public Result create(Http.Request request) {
        JsonNode json = request.body().asJson();
        if (json == null) {
            return badRequest(Json.newObject().put("message", "JSON inválido"));
        }

        // Converte JSON -> Objeto Java
        Produto produto = Json.fromJson(json, Produto.class);

        // Salva no banco
        Produto salvo = produtoRepository.create(produto);

        return created(Json.toJson(salvo));
    }

    // PUT /produtos/:id
    @Secured("ADMIN")
    public Result update(Long id, Http.Request request) {
        JsonNode json = request.body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        }

        Produto produtoParaAtualizar = Json.fromJson(json, Produto.class);

        // Garante que o ID do objeto é o mesmo da URL
        produtoParaAtualizar.id = id;

        // Verifica se existe antes de tentar atualizar
        if (produtoRepository.findById(id) == null) {
            return notFound(Json.newObject().put("message", "Produto não existe para ser atualizado"));
        }

        Produto atualizado = produtoRepository.update(produtoParaAtualizar);
        return ok(Json.toJson(atualizado));
    }

    // DELETE /produtos/:id
    @Secured("ADMIN")
    public Result delete(Long id) {
        boolean deletado = produtoRepository.delete(id);
        if (deletado) {
            return ok(Json.newObject().put("message", "Produto deletado com sucesso"));
        } else {
            return notFound(Json.newObject().put("message", "Produto não encontrado"));
        }
    }
}