package controllers.security;

import play.libs.typedmap.TypedKey;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import services.TokenService;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.auth0.jwt.interfaces.DecodedJWT;

public class SecuredAction extends Action<Secured> {

    private final TokenService tokenService;
    public static final TypedKey<String> USER_EMAIL = TypedKey.create("user_email");
    public static final TypedKey<String> USER_ROLE = TypedKey.create("user_role");

    @Inject
    public SecuredAction(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public CompletionStage<Result> call(Http.Request req) {
        Optional<String> authHeader = req.header("Authorization");

        if (authHeader.isEmpty() || !authHeader.get().startsWith("Bearer ")) {
            return CompletableFuture.completedFuture(unauthorized("Token não fornecido ou inválido"));
        }

        String token = authHeader.get().substring(7);

        Optional<DecodedJWT> jwtOpt = tokenService.validateToken(token);

        if (jwtOpt.isEmpty()) {
            return CompletableFuture.completedFuture(unauthorized("Token inválido"));
        }

        DecodedJWT jwt = jwtOpt.get();
        String userRole = jwt.getClaim("role").asString();
        String userEmail = jwt.getSubject();

        String[] requiredRoles = configuration.value();

        if (requiredRoles.length > 0) {
            boolean temPermissao = false;
            for (String role : requiredRoles) {
                if (role.equalsIgnoreCase(userRole)) {
                    temPermissao = true;
                    break;
                }
            }

            if (!temPermissao) {
                return CompletableFuture.completedFuture(forbidden("Acesso negado: Você não tem permissão de " + String.join(",", requiredRoles)));
            }
        }

        return delegate.call(req.addAttr(USER_EMAIL, userEmail).addAttr(USER_ROLE, userRole));
    }
}
