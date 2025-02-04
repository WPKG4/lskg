package ovh.wpkg.lskg.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.db.repository.RatSharesRepository;
import ovh.wpkg.lskg.db.repository.UserRepository;
import ovh.wpkg.lskg.services.rat.RatInfoService;

import java.util.UUID;

@Secured(SecurityRule.IS_ANONYMOUS)
@Slf4j
@Controller
public class DebugController {

    @Inject
    RatInfoService ratInfoService;

    @Inject
    UserRepository userRepository;

    @Inject
    RatSharesRepository ratSharesRepository;

    @Get("/debug/users")
    public HttpResponse<?> listUsers() {
        var users = userRepository.findAll();

        for (var user : users) {
            log.debug("User: {}", user.getEmail());
        }

        return HttpResponse.ok();
    }

    @Get("/debug/shares")
    public HttpResponse<?> listShares() {
        var shares = ratSharesRepository.findAll();

        for (var share : shares) {
            log.debug("================================");
            log.debug("RAT: {}", share.getRatInfo().getUuid());
            log.debug("User: {}", share.getUser().getEmail());
        }

        return HttpResponse.ok();
    }

    @Get("/debug/shares/{uuid}")
    public HttpResponse<?> listShares(@PathVariable String uuid) {
        var shares = ratSharesRepository.findByRatInfoUuid(UUID.fromString(uuid));

        for (var share : shares) {
            log.debug("RAT: {}", share.getRatInfo().getUuid());
            log.debug("User: {}", share.getUser().getEmail());
        }

        return HttpResponse.ok();
    }
}
