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


@Controller("/test")
@Secured(SecurityRule.IS_ANONYMOUS)
@Slf4j
public class TestController {

    @Inject
    RatService ratService;

    @Inject
    RatClientPoller ratClientPoller;

    @Inject
    WtpClientService wtpClientService;

    @Get
    @ExecuteOn(TaskExecutors.VIRTUAL)
    public void ping() {
        var client = ratService.getClientList()[0];


        ratClientPoller.getClientIdFlux().subscribe((id) -> {
            WtpClient wtpClient = wtpClientService.getClient(id);
            wtpClient.lock();

            wtpClient.receiveData(((client1, handler, payload) -> {

                log.debug("DATA: {}", payload);

                handler.dispose();
                client1.unlock();

            })).subscribe();
        });

        client.getMasterClient().send(new MessagePayload("NEW")).subscribe();
    }

    @Get("/broadcast")
    public void broadcast(){
        log.debug("Broadcast endpoint hit");
        for (RatClient client : ratService.getClientList()) {
            for (WtpClient socket : client.sockets) {
                socket.send(new MessagePayload("hello")).subscribe();
            }
        }
    }
}
