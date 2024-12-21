package ovh.wpkg.lskg;

import io.micronaut.http.annotation.*;

@Controller("/lskg")
public class LskgController {

    @Get(uri="/", produces="text/plain")
    public String index() {
        return "Example Response";
    }
}