package ovh.wpkg.lskg.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.services.WtpClientService;
import ovh.wpkg.lskg.server.types.bi.MessagePayload;

@Controller("/test")
@Secured(SecurityRule.IS_ANONYMOUS)
@Slf4j
public class TestController {

    @Inject
    WtpClientService wtpClientService;

    @Get
    public void ping() {
        var client = wtpClientService.getClientList()[0];

        log.debug("Test");

        client.getChannel().writeAndFlush(new MessagePayload("Test")).awaitUninterruptibly();
    }
}
