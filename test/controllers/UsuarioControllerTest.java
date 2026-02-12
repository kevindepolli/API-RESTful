package controllers;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import models.Usuario.Role;
import models.Usuario;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;
import repository.UsuarioRepository;
import services.TokenService;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.*;

public class UsuarioControllerTest extends WithApplication {

    private UsuarioRepository mockDao;
    private TokenService mockTokenService;

    @Override
    protected Application provideApplication() {
        mockDao = mock(UsuarioRepository.class);
        mockTokenService = mock(TokenService.class);

        return new GuiceApplicationBuilder()
                .overrides(bind(UsuarioRepository.class).toInstance(mockDao))
                .overrides(bind(TokenService.class).toInstance(mockTokenService))
                .build();
    }

    private void mockLogin(Role role, String email) {
        DecodedJWT jwt = mock(DecodedJWT.class);
        Claim roleClaim = mock(Claim.class);

        when(roleClaim.asString()).thenReturn(role.name());
        when(jwt.getClaim("role")).thenReturn(roleClaim);
        when(jwt.getSubject()).thenReturn(email);

        when(mockTokenService.validateToken(anyString())).thenReturn(Optional.of(jwt));
    }

    private void mockAlvoNoBanco(Long id, Role role, String email) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setRole(role);
        usuario.setEmail(email);

        when(mockDao.findById(id)).thenReturn(usuario);
        when(mockDao.delete(id)).thenReturn(true);
    }

    @Test
    public void rootDevePoderDeletarAdmin() {
        mockLogin(Role.ROOT, "root@sistema.com");
        mockAlvoNoBanco(2L, Role.ADMIN, "admin@empresa.com");

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .uri("/usuarios/2")
                .header("Authorization", "Bearer token_valido");

        Result result = route(app, request);


        assertEquals(OK, result.status());
        verify(mockDao, times(1)).delete(2L);
    }

    @Test
    public void adminDevePoderDeletarUser() {
        mockLogin(Role.ADMIN, "admin@empresa.com");
        mockAlvoNoBanco(3L, Role.USER, "cliente@gmail.com");

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .uri("/usuarios/3")
                .header("Authorization", "Bearer token_valido");

        Result result = route(app, request);

        assertEquals(OK, result.status());
        verify(mockDao, times(1)).delete(3L);
    }

    @Test
    public void adminNAODevePoderDeletarOutroAdmin() {
        mockLogin(Role.ADMIN, "admin1@empresa.com");
        mockAlvoNoBanco(5L, Role.ADMIN, "admin2@empresa.com");

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .uri("/usuarios/5")
                .header("Authorization", "Bearer token_valido");

        Result result = route(app, request);

        assertEquals(FORBIDDEN, result.status());

        JsonNode json = Json.parse(contentAsString(result));
        assertTrue(json.get("erro").asText().contains("Apenas o ROOT pode deletar Administradores"));

        verify(mockDao, never()).delete(any());
    }

    @Test
    public void ninguemDevePoderDeletarORoot() {

        mockLogin(Role.ROOT, "hacker@sistema.com");
        mockAlvoNoBanco(1L, Role.ROOT, "root@sistema.com");

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .uri("/usuarios/1")
                .header("Authorization", "Bearer token_valido");

        Result result = route(app, request);

        assertEquals(FORBIDDEN, result.status());
        JsonNode json = Json.parse(contentAsString(result));
        assertTrue(json.get("erro").asText().contains("Ação Proibida: O Usuário ROOT não pode ser deletado via API."));

        verify(mockDao, never()).delete(any());
    }

    @Test
    public void usuarioNaoPodeDeletarASiMesmo() {
        String meuEmail = "eu@empresa.com";
        mockLogin(Role.ADMIN, meuEmail);
        mockAlvoNoBanco(10L, Role.ADMIN, meuEmail);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .uri("/usuarios/10")
                .header("Authorization", "Bearer token_valido");

        Result result = route(app, request);

        String body = contentAsString(result);
        System.out.println("STATUS: " + result.status());
        System.out.println("JSON RECEBIDO: " + body);

        JsonNode json = Json.parse(contentAsString(result));

        assertEquals("AUTO_DELECAO_PROIBIDA", json.get("codigo").asText());
        verify(mockDao, never()).delete(any());
    }

    @Test
    public void naoDeveCriarUsuarioComEmailDuplicado() {
        mockLogin(Role.ADMIN, "admin@empresa.com");

        JsonNode jsonEntrada = Json.newObject()
                .put("nome", "Novo User")
                .put("email", "duplicado@teste.com")
                .put("senha", "123456")
                .put("role", "USER");

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(99L);
        usuarioExistente.setEmail("duplicado@teste.com");

        when(mockDao.findByEmail("duplicado@teste.com"))
                .thenReturn(Optional.of(usuarioExistente));

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/usuarios")
                .header("Authorization", "Bearer token_valido")
                .bodyJson(jsonEntrada);

        Result result = route(app, request);

        assertEquals(CONFLICT, result.status());

        JsonNode jsonRetorno = Json.parse(contentAsString(result));
        assertEquals("EMAIL_JA_EXISTE", jsonRetorno.path("codigo").asText());

        verify(mockDao, never()).create(any(Usuario.class));
    }

    @Test
    public void deveCriarUsuarioComSucesso() {
        mockLogin(Role.ROOT, "root@sistema.com");

        JsonNode jsonEntrada = Json.newObject()
                .put("nome", "Novo Usuario")
                .put("email", "novo@teste.com")
                .put("senha", "123456")
                .put("role", "USER");

        when(mockDao.findByEmail("novo@teste.com")).thenReturn(Optional.empty());

        Usuario usuarioSalvo = Json.fromJson(jsonEntrada, Usuario.class);
        usuarioSalvo.setId(1L);
        when(mockDao.create(any(Usuario.class))).thenReturn(usuarioSalvo);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/usuarios")
                .header("Authorization", "Bearer token_valido")
                .bodyJson(jsonEntrada);

        Result result = route(app, request);

        assertEquals(CREATED, result.status());
        JsonNode jsonRetorno = Json.parse(contentAsString(result));
        assertEquals(1L, jsonRetorno.get("id").asLong());
        assertEquals("novo@teste.com", jsonRetorno.get("email").asText());
    }

    @Test
    public void adminNaoPodeCriarOutroAdmin() {
        mockLogin(Role.ADMIN, "admin@sistema.com");

        JsonNode jsonEntrada = Json.newObject()
                .put("nome", "Novo Admin")
                .put("email", "futuroadmin@teste.com")
                .put("senha", "123456")
                .put("role", "ADMIN");

        when(mockDao.findByEmail(anyString())).thenReturn(Optional.empty());

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/usuarios")
                .header("Authorization", "Bearer token_valido")
                .bodyJson(jsonEntrada);

        Result result = route(app, request);

        assertEquals(FORBIDDEN, result.status());

        verify(mockDao, never()).create(any());
    }

    @Test
    public void deveAtualizarUsuarioComSucesso() {
        Long idParaEditar = 5L;
        mockLogin(Role.ADMIN, "admin@sistema.com");

        JsonNode jsonNovosDados = Json.newObject()
                .put("nome", "Nome Editado")
                .put("email", "velho@teste.com")
                .put("role", "USER");

        Usuario usuarioAntigo = new Usuario();
        usuarioAntigo.setId(idParaEditar);
        usuarioAntigo.setName("Nome Velho");
        when(mockDao.findById(idParaEditar)).thenReturn(usuarioAntigo);

        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setId(idParaEditar);
        usuarioAtualizado.setName("Nome Editado");
        when(mockDao.update(any(Usuario.class))).thenReturn(usuarioAtualizado);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .uri("/usuarios/" + idParaEditar)
                .header("Authorization", "Bearer token_valido")
                .bodyJson(jsonNovosDados);

        Result result = route(app, request);

        assertEquals(OK, result.status());
        JsonNode json = Json.parse(contentAsString(result));
        assertEquals("Nome Editado", json.get("name").asText());

        verify(mockDao).update(any(Usuario.class));
    }

    @Test
    public void naoDeveAtualizarUsuarioInexistente() {
        Long idInexistente = 999L;
        mockLogin(Role.ADMIN, "admin@sistema.com");

        JsonNode json = Json.newObject().put("nome", "Qualquer Coisa");

        when(mockDao.findById(idInexistente)).thenReturn(null);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .uri("/usuarios/" + idInexistente)
                .header("Authorization", "Bearer token_valido")
                .bodyJson(json);

        Result result = route(app, request);

        assertEquals(NOT_FOUND, result.status());

        verify(mockDao, never()).update(any());
    }
}