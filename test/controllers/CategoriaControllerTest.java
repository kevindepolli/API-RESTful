package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Categoria;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import services.CategoriaService;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.*;

public class CategoriaControllerTest {

    private CategoriaService serviceMock;
    private CategoriaController controller;

    @Before
    public void setup() {
        serviceMock = mock(CategoriaService.class);
        controller = new CategoriaController(serviceMock);
    }

    // LISTAR CATEGORIAS
    @Test
    public void testListarCategorias() {
        Categoria c = new Categoria();
        c.setId(1L);
        c.setDescricao("Eletrônicos");

        when(serviceMock.listarComFiltro(anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(c));

        when(serviceMock.contarComFiltro(anyString())).thenReturn(1L);

        Http.Request request = fakeRequest("GET", "/categorias?descricao=Ele&page=1&size=10").build();

        Result result = controller.listarCategorias(request);

        assertEquals(OK, result.status());
        String body = contentAsString(result);

        assertTrue(body.contains("\"page\":1"));
        assertTrue(body.contains("\"total\":1"));
        assertTrue(body.contains("Eletrônicos"));
    }

    // BUSCAR POR ID
    @Test
    public void testBuscarCategoria_Sucesso() {
        Categoria cat = new Categoria();
        cat.setId(1L);
        cat.setDescricao("Livros");

        when(serviceMock.buscarCategoria(1L)).thenReturn(cat);

        Result result = controller.buscarCategoria(1L);

        assertEquals(OK, result.status());
        assertTrue(contentAsString(result).contains("Livros"));
    }

    @Test
    public void testBuscarCategoria_NaoEncontrado() {
        when(serviceMock.buscarCategoria(99L)).thenThrow(new RuntimeException("Categoria não encontrada"));

        Result result = controller.buscarCategoria(99L);

        assertEquals(NOT_FOUND, result.status());
    }

    // CRIAR
    @Test
    public void testCriarCategoria_Sucesso() {
        Categoria nova = new Categoria();
        nova.setId(10L);
        nova.setDescricao("Nova Categoria");

        when(serviceMock.criarCategoria(any(Categoria.class))).thenReturn(nova);

        JsonNode json = Json.parse("{\"descricao\":\"Nova Categoria\"}");
        Http.Request req = fakeRequest("POST", "/categorias").bodyJson(json).build();

        Result result = controller.criarCategoria(req);

        assertEquals(CREATED, result.status());
        assertTrue(contentAsString(result).contains("\"id\":10"));
    }

    @Test
    public void testCriarCategoria_Erro() {
        when(serviceMock.criarCategoria(any())).thenThrow(new RuntimeException("Descrição inválida"));

        JsonNode json = Json.parse("{\"descricao\":\"\"}");
        Http.Request req = fakeRequest("POST", "/categorias").bodyJson(json).build();

        Result result = controller.criarCategoria(req);

        assertEquals(BAD_REQUEST, result.status());
    }

    // ATUALIZAR
    @Test
    public void testAtualizarCategoria_Sucesso() {
        Categoria atualizada = new Categoria();
        atualizada.setId(1L);
        atualizada.setDescricao("Atualizada");

        when(serviceMock.atualizarCategoria(eq(1L), any())).thenReturn(atualizada);

        JsonNode json = Json.parse("{\"descricao\":\"Atualizada\"}");
        Http.Request req = fakeRequest("PUT", "/categorias/1").bodyJson(json).build();

        Result result = controller.atualizarCategoria(1L, req);

        assertEquals(OK, result.status());
        assertTrue(contentAsString(result).contains("Atualizada"));
    }

    @Test
    public void testAtualizarCategoria_Erro() {
        when(serviceMock.atualizarCategoria(eq(99L), any())).thenThrow(new RuntimeException("Categoria não existe"));

        JsonNode json = Json.parse("{\"descricao\":\"Falha\"}");
        Http.Request req = fakeRequest("PUT", "/categorias/99").bodyJson(json).build();

        Result result = controller.atualizarCategoria(99L, req);

        assertEquals(BAD_REQUEST, result.status());
    }

    // DELETAR
    @Test
    public void testDeletarCategoria_Sucesso() {
        doNothing().when(serviceMock).deletarCategoria(1L);

        Result result = controller.deletarCategoria(1L);

        assertEquals(NO_CONTENT, result.status());
    }

    @Test
    public void testDeletarCategoria_Erro() {
        doThrow(new RuntimeException("Não existe")).when(serviceMock).deletarCategoria(99L);

        Result result = controller.deletarCategoria(99L);

        assertEquals(NOT_FOUND, result.status());
    }
}
