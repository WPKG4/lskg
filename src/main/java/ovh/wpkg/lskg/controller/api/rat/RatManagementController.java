package ovh.wpkg.lskg.controller.api.rat;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.dto.response.RatDTO;
import ovh.wpkg.lskg.services.rat.RatInfoService;
import ovh.wpkg.lskg.services.users.UserService;

import java.util.List;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/api/rat")
@Slf4j
public class RatManagementController {

    @Inject
    UserService userService;

    @Inject
    RatInfoService ratInfoService;

    @Get("/list")
    public List<RatDTO> list(Authentication authentication) {
        var user = userService.findUserByEmail(authentication.getName());
        return ratInfoService.getRatForUser(user);
    }
}
