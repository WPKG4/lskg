package ovh.wpkg.lskg;

import io.micronaut.openapi.annotation.OpenAPIInclude;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
    info = @Info(
            title = "LSKG",
            version = "0.0"
    )
)
@OpenAPIInclude(
        classes = {
                io.micronaut.security.endpoints.LoginController.class,
                io.micronaut.security.endpoints.OauthController.class
        },
        tags = { @Tag(name = "Auth") }
)
public class Application {

    public static void main(String[] args) throws Exception {
        Micronaut.run(args);
    }
}