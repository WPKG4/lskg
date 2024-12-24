package ovh.wpkg.lskg.server.config;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ServerConfig {
    public static final ByteBuf HEADER_DELIMITER = Unpooled.copiedBuffer("\n".getBytes());
    public static final int MAX_HEADER_SIZE = 1024;
}
