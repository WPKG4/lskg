package ovh.wpkg.lskg.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.dto.RatClient;
import ovh.wpkg.lskg.server.dto.WtpClient;
import ovh.wpkg.lskg.server.services.RatClientPoller;
import ovh.wpkg.lskg.server.services.RatService;
import ovh.wpkg.lskg.server.services.WtpClientService;
import ovh.wpkg.lskg.server.types.bi.MessagePayload;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;


@Controller("/test")
@Secured(SecurityRule.IS_ANONYMOUS)
@Slf4j
public class TestController {

    @Inject
    RatService ratService;

    @Inject
    RatClientPoller ratClientPoller;


    @Get
    @ExecuteOn(TaskExecutors.VIRTUAL)
    public String ping() {
        RatClient client = ratService.getClientList()[0];

        WtpClient wtpClient = ratClientPoller.poolClient(client);

        wtpClient.lock();

        AtomicInteger i = new AtomicInteger(0);

        wtpClient.receiveData((client1, handler, payload) -> {
            log.debug("DATA: {}", payload);

            i.incrementAndGet();

            if (i.get() > 5) {
                handler.dispose();
                client1.unlock();
            }
        }).block();

        return "Success";
    }


    @Get("/broadcast")
    public void broadcast() {
        log.debug("Broadcast endpoint hit");
        for (RatClient client : ratService.getClientList()) {
            for (WtpClient socket : client.sockets) {
                socket.send(new MessagePayload("hello")).subscribe();
            }
        }
    }
}
