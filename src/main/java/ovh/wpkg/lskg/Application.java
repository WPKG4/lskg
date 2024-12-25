package ovh.wpkg.lskg;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;

@OpenAPIDefinition(
    info = @Info(
            title = "LSKG",
            version = "0.0"
    )
)
public class Application {

    public static void main(String[] args) throws Exception {
        Micronaut.run(args);
    }
}