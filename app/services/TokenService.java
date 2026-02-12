package services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import models.Usuario;
import jakarta.inject.Singleton;
import java.util.Date;
import java.util.Optional;

@Singleton
public class TokenService {

    private static final String SECRET = "ChaveDeCriptografia";
    private static final long EXPIRATION_TIME = 86400000;

    public String signToken(Usuario usuario) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        return JWT.create()
                .withIssuer("api-play")
                .withSubject(usuario.getEmail())
                .withClaim("id", usuario.getId())
                .withClaim("role", usuario.getRole().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);
    }

    public Optional<DecodedJWT> validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("api-play")
                    .build();

            DecodedJWT jwt = verifier.verify(token);
            return Optional.of(jwt);
        } catch (Exception exception){
            return Optional.empty();
        }
    }
}