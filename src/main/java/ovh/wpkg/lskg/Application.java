package ovh.wpkg.lskg;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;
import ovh.wpkg.lskg.server.WPKGServer;

@OpenAPIDefinition(
    info = @Info(
            title = "lskg",
            version = "0.0"
    )
)
public class Application {

    //public static void main(String[] args) {
    //    Micronaut.run(LskgController.class, args);
    //}
    public static void main(String[] args) throws Exception {
        new WPKGServer(8080).start();
    }
}