package ovh.wpkg.lskg.services.users;

import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import ovh.wpkg.lskg.utils.HashUtils;

import java.util.NoSuchElementException;
import java.util.Objects;

@Singleton
public class UserAuthenticationProvider<B> implements HttpRequestAuthenticationProvider<B> {

    @Inject
    UserService userService;

    @Override
    public AuthenticationResponse authenticate(
            HttpRequest<B> httpRequest,
            AuthenticationRequest<String, String> authenticationRequest
    ) {
        try {
            var user = userService.findUserByEmail(authenticationRequest.getIdentity()).orElseThrow();

            return Objects.equals(HashUtils.generateSHA256(authenticationRequest.getSecret()), user.getPasswordHash())
                    ? AuthenticationResponse.success(authenticationRequest.getIdentity())
                    : AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
        } catch (NoSuchElementException e) {
            return AuthenticationResponse.failure(AuthenticationFailureReason.USER_NOT_FOUND);
        }
    }
}
