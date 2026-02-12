package controllers;

import com.fasterxml.jackson.databind.JsonNode;
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
import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;
import static play.test.Helpers.*;

public class AuthControllerTest extends WithApplication {

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

    @Test
    public void deveLogarComSucessoERetornarToken() {
        String email = "teste@email.com";
        String senhaPura = "123456";

        Usuario usuarioDoBanco = new Usuario();
        usuarioDoBanco.setEmail(email);
        usuarioDoBanco.setSenha(senhaPura);

        when(mockDao.findByEmail(email)).thenReturn(Optional.of(usuarioDoBanco));

        when(mockTokenService.signToken(any(Usuario.class))).thenReturn("token_jwt_sucesso");

        JsonNode jsonLogin = Json.newObject()
                .put("email", email)
                .put("senha", senhaPura);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/login")
                .bodyJson(jsonLogin);

        Result result = route(app, request);

        assertEquals(OK, result.status());

        JsonNode jsonResposta = Json.parse(contentAsString(result));
        assertTrue(jsonResposta.has("token"));
        assertEquals("token_jwt_sucesso", jsonResposta.get("token").asText());
    }

    @Test
    public void deveRetornarUnauthorizedSeUsuarioNaoExistir() {
        String email = "naoexiste@email.com";

        when(mockDao.findByEmail(email)).thenReturn(Optional.empty());

        JsonNode jsonLogin = Json.newObject()
                .put("email", email)
                .put("senha", "123456");

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/login")
                .bodyJson(jsonLogin);

        Result result = route(app, request);

        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void deveRetornarUnauthorizedSeSenhaEstiverErrada() {
        Usuario usuarioDoBanco = new Usuario();
        usuarioDoBanco.setEmail("teste@email.com");
        usuarioDoBanco.setSenha("senhaCerta");

        when(mockDao.findByEmail("teste@email.com")).thenReturn(Optional.of(usuarioDoBanco));

        JsonNode jsonLogin = Json.newObject()
                .put("email", "teste@email.com")
                .put("senha", "senhaErrada");

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/login")
                .bodyJson(jsonLogin);

        Result result = route(app, request);

        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void deveRetornarBadRequestSeJsonForInvalido() {
        JsonNode jsonIncompleto = Json.newObject()
                .put("email", "teste@email.com");

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/login")
                .bodyJson(jsonIncompleto);

        Result result = route(app, request);

        assertEquals(BAD_REQUEST, result.status());
    }
}