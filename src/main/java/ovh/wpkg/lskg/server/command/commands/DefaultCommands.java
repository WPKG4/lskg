package ovh.wpkg.lskg.server.command.commands;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.command.Command;
import ovh.wpkg.lskg.server.command.CommandContext;
import ovh.wpkg.lskg.server.dto.RatClient;
import ovh.wpkg.lskg.server.dto.WtpClient;
import ovh.wpkg.lskg.server.services.RatClientPoller;
import ovh.wpkg.lskg.server.services.ConnectedRatService;
import ovh.wpkg.lskg.server.services.WtpClientService;
import ovh.wpkg.lskg.server.types.out.ActionOutPayload;
import ovh.wpkg.lskg.services.rat.RatInfoService;
import ovh.wpkg.lskg.services.users.UserService;

import java.util.UUID;

@Singleton
@Slf4j
@SuppressWarnings("unused")
public class DefaultCommands {

    @Inject
    public WtpClientService wtpClientService;

    @Inject
    public ConnectedRatService connectedRatService;

    @Inject
    public UserService userService;

    @Inject
    private RatClientPoller ratClientPoller;

    @Inject
    private RatInfoService ratInfoService;

    @Command(name = "hello")
    public ActionOutPayload hello(CommandContext context) {
        return context.response(0, "Hello World!");
    }

    @Command(name = "disconnect")
    public ActionOutPayload disconnect(CommandContext context) {
        context.getWtpClient().getChannel().disconnect();
        return context.response(0, "Disconnected!");
    }

    @Command(name = "core-init")
    public ActionOutPayload coreInit(CommandContext context) {
        try {
            if (context.checkParams("uuid", "user", "hostname"))
                return context.response(1, "Missing or empty parameter");

            UUID uuid = UUID.fromString(context.param("uuid"));
            String user = context.param("user");
            String hostname = context.param("hostname");

            ratInfoService.registerRat(userService.findUserById(1L), uuid, hostname, user);

            var wtpClient = wtpClientService.getClient(context.getWtpClient().getChannel());

            connectedRatService.addClient(wtpClient, uuid, user, hostname);

            return context.response(0,"Registered client " + user + " " + hostname + " has been added!");
        } catch (Exception e) {
            return context.response(3, "An unexpected error occurred: " + e.getMessage());
        }
    }

    @Command(name = "new-socket")
    public ActionOutPayload newSocket(CommandContext context) {
        if (context.checkParams("uuid")) {
            return context.response(1, "Missing or empty parameter");
        }

        try {
            UUID uuid = UUID.fromString(context.param("uuid"));
            RatClient rat = connectedRatService.getByUUID(uuid);

            if (rat == null)
                return context.response(1, "No entity found for UUID: " + uuid);

            WtpClient wtpClient = new WtpClient(context.getWtpClient().getChannel());
            rat.getSockets().add(wtpClient);

            // Emit new WTP client id to ratClientPoller
            ratClientPoller.getClientSink().tryEmitNext(context.getWtpClient().getChannel().id().asShortText());

            return context.response(0, "Socket successfully added!");
        } catch (IllegalArgumentException e) {
            return context.response(1, "Invalid UUID format: " + context.param("uuid"));
        }
    }
}