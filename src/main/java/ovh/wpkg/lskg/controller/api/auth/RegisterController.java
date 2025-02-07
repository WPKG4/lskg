package ovh.wpkg.lskg.controller.api.auth;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.dto.request.UserRegisterRequest;
import ovh.wpkg.lskg.dto.response.ErrorResponse;
import ovh.wpkg.lskg.services.users.UserService;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/api/auth/register")
@Slf4j
public class RegisterController {

    @Inject
    UserService userService;

    @Post
    public HttpResponse<?> register(@Body UserRegisterRequest request) {
        try {
            log.info("Registering new user {}", request.getEmail());
            userService.registerUser(request.getEmail(), request.getPassword());
            return HttpResponse.ok();
        } catch (IllegalArgumentException e) {
            log.error("Registering user {}: {}",request.getEmail(), e.getMessage());
            return HttpResponse.badRequest(new ErrorResponse(500, e.getMessage()));
        }
    }
}
