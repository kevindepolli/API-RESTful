package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.PagedResult;
import models.Produto;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import repository.ProdutoRepository;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.*;

public class ProdutoControllerTest {

    private ProdutoRepository produtoRepositoryMock;
    private ProdutoController controller;

    @Before
    public void setup() {
        // Mock do DAO (o falso)
        produtoRepositoryMock = mock(ProdutoRepository.class);

        // Injeta o mock no Controller em vez do DAO real
        controller = new ProdutoController(produtoRepositoryMock);
    }

    // (Listar e Buscar por ID)

    @Test
    public void testGetById_Encontrado() {
        // Cenario: DAO retorna um produto valido
        Produto p = new Produto();
        p.id = 1L;
        p.descricao = "Teste";
        when(produtoRepositoryMock.findById(1L)).thenReturn(p);

        // Acao
        Result result = controller.getById(1L);

        // Verificacao
        assertEquals(OK, result.status()); // Espera 200
        String body = contentAsString(result);
        assertTrue(body.contains("Teste"));
    }

    @Test
    public void testGetById_NaoEncontrado() {
        // Cenario: DAO retorna null
        when(produtoRepositoryMock.findById(99L)).thenReturn(null);

        // Acao
        Result result = controller.getById(99L);

        // Verificacao
        assertEquals(NOT_FOUND, result.status()); // Espera 404
    }

    @Test
    public void testList() {
        // Cenario: Preparar resultado paginado
        PagedResult<Produto> fakePage = new PagedResult<>(Collections.emptyList(), 0, 1, 10);
        when(produtoRepositoryMock.findList(anyInt(), anyInt(), any())).thenReturn(fakePage);

        // Criar um Request falso com Query String
        Http.Request request = fakeRequest("GET", "/produtos?page=1&termo=celular").build();

        // Acao
        Result result = controller.list(request);

        // Verificacao
        assertEquals(OK, result.status());
        verify(produtoRepositoryMock).findList(1, 10, "celular"); // Garante que o DAO foi chamado com os parametros certos
    }

    // Teste de Criação

    @Test
    public void testCreate_Sucesso() {
        // Cenario
        Produto novo = new Produto();
        novo.descricao = "Novo Produto";

        Produto salvo = new Produto();
        salvo.id = 50L;
        salvo.descricao = "Novo Produto";

        when(produtoRepositoryMock.create(any(Produto.class))).thenReturn(salvo);

        // Criar JSON para enviar no body
        JsonNode jsonBody = Json.toJson(novo);
        Http.Request request = fakeRequest().bodyJson(jsonBody).build();

        // Acao
        Result result = controller.create(request);

        // Verificacao
        assertEquals(CREATED, result.status()); // Espera 201
        assertTrue(contentAsString(result).contains("\"id\":50"));
    }

    // DELETE

    @Test
    public void testDelete_Sucesso() {
        // Cenario: DAO retorna true (deletou)
        when(produtoRepositoryMock.delete(1L)).thenReturn(true);

        Result result = controller.delete(1L);

        assertEquals(OK, result.status());
        assertTrue(contentAsString(result).contains("sucesso"));
    }

    @Test
    public void testDelete_Falha() {
        // Cenario: DAO retorna false (não achou)
        when(produtoRepositoryMock.delete(99L)).thenReturn(false);

        Result result = controller.delete(99L);

        assertEquals(NOT_FOUND, result.status());
    }
}