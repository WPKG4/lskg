package ovh.wpkg.lskg.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.db.repository.RatInfoRepository;
import ovh.wpkg.lskg.db.repository.UserRepository;

@Secured(SecurityRule.IS_ANONYMOUS)
@Slf4j
@Controller
public class DebugController {

    @Inject
    UserRepository userRepository;

    @Inject
    RatInfoRepository ratInfoRepository;

    @Get("/debug/users")
    @Transactional
    public HttpResponse<?> listUsers() {
        var users = userRepository.findAll();

        for (var user : users) {
            log.debug("User: {}", user.getEmail());

            log.debug("Rats size: {}", user.getOwnedRats().size());
        }

        return HttpResponse.ok();
    }

    @Get("/debug/rats")
    @Transactional
    public HttpResponse<?> listRats() {
        var rats = ratInfoRepository.findAll();

        for (var rat : rats) {
            log.debug("RAT: {}", rat.getUuid());
        }

        return HttpResponse.ok();
    }
}
