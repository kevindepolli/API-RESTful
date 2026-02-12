package services;

import com.auth0.jwt.interfaces.DecodedJWT;
import models.Usuario;
import models.Usuario.Role;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class TokenServiceTest {

    private TokenService tokenService;

    @Before
    public void setUp() {
        this.tokenService = new TokenService();
    }

    @Test
    public void deveGerarEValidarTokenComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setEmail("admin@teste.com");
        usuario.setRole(Role.ADMIN);

        String token = tokenService.signToken(usuario);

        assertNotNull("O token não deve ser nulo", token);

        Optional<DecodedJWT> jwtOpt = tokenService.validateToken(token);

        assertTrue("O token deveria ser válido", jwtOpt.isPresent());

        DecodedJWT jwt = jwtOpt.get();
        assertEquals("admin@teste.com", jwt.getSubject());
        assertEquals("ADMIN", jwt.getClaim("role").asString());
    }

    @Test
    public void naoDeveAceitarTokenInvalido() {
        String tokenFalso = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.tokenfalso.assinaturaerrada";

        Optional<DecodedJWT> result = tokenService.validateToken(tokenFalso);

        assertFalse("Token falso não deve ser validado", result.isPresent());
    }
}