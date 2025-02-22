package ovh.wpkg.lskg.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.controller.base.WpkgBaseController;
import ovh.wpkg.lskg.server.dto.RatClient;
import ovh.wpkg.lskg.server.dto.WtpClient;
import ovh.wpkg.lskg.server.types.bi.MessagePayload;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Controller("/test")
@Secured(SecurityRule.IS_ANONYMOUS)
@Slf4j
@ExecuteOn(TaskExecutors.VIRTUAL)
public class TestController extends WpkgBaseController {

    @Get
    public Mono<String> ping() {
        return Mono.create(sink -> {
            RatClient client = connectedRatService.getRatsList().getFirst();

            WtpClient wtpClient = ratClientPoller.poolClient(client);
            wtpClient.lock();

            AtomicInteger i = new AtomicInteger(0);

            wtpClient.send(new MessagePayload("TEST")).subscribe();

            wtpClient.receiveData((client1, payload) -> {
                log.debug("DATA: {}", payload);

                if (i.get() == 999) {
                    client1.stopReceive();
                    client1.unlock();

                    sink.success("Success");
                }

                i.incrementAndGet();
            });
        });
    }

    @Get("/hello")
    public void helloCommand() {
        RatClient client = connectedRatService.getRatsList().getFirst();

        WtpClient wtpClient = ratClientPoller.poolClient(client);
        wtpClient.lock();

        wtpClient.send(
                commandPayload("hello", Map.of())
        ).block();
        
        wtpClient.unlock();
    }
}
