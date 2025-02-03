package ovh.wpkg.lskg.services.users;

import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.event.RefreshTokenGeneratedEvent;
import io.micronaut.security.token.refresh.RefreshTokenPersistence;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import ovh.wpkg.lskg.db.entities.Token;
import ovh.wpkg.lskg.db.repository.TokenRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Optional;

@Slf4j
@Singleton
public class UserTokenPersistence implements RefreshTokenPersistence {

    private final TokenRepository tokenRepository;

    public UserTokenPersistence(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void persistToken(RefreshTokenGeneratedEvent event) {
        Token token = new Token();

        token.setToken(event.getRefreshToken());
        token.setUsername(event.getAuthentication().getName());
        token.setRevoked(false);

        tokenRepository.save(token);
        log.debug("Persisted token for user: {}", event.getAuthentication().getName());
    }

    @Override
    public Publisher<Authentication> getAuthentication(String refreshToken) {
        return Flux.create(emitter -> {
            Optional<Token> tokenEntity = tokenRepository.findByToken(refreshToken);

            if (tokenEntity.isPresent() && !tokenEntity.get().isRevoked()) {
                String username = tokenEntity.get().getUsername();
                Authentication authentication = Authentication.build(username);

                emitter.next(authentication);
                emitter.complete();
            } else {
                emitter.error(new IllegalArgumentException("Refresh token unknown or revoked"));
                emitter.complete();
            }
        }, FluxSink.OverflowStrategy.ERROR);
    }
}