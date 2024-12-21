package ovh.wpkg.lskg;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;
import ovh.wpkg.lskg.server.WpkgServer;

@OpenAPIDefinition(
    info = @Info(
            title = "LSKG",
            version = "0.0"
    )
)
public class Application {

    public static void main(String[] args) throws Exception {
        var tcpServer = new WpkgServer(8080);
        tcpServer.start();
    }
}