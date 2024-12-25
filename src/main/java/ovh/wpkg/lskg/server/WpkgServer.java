package ovh.wpkg.lskg.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.command.commands.DefaultCommands;
import ovh.wpkg.lskg.server.config.ServerConfig;
import ovh.wpkg.lskg.server.handler.CommandExecutorHandler;
import ovh.wpkg.lskg.server.handler.HeaderDecoder;
import ovh.wpkg.lskg.server.command.CommandRegistry;
import ovh.wpkg.lskg.server.handler.WtpOutboundHandler;

@Slf4j
public class WpkgServer {
    private final int port;

    public WpkgServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        CommandRegistry.registerCommand(DefaultCommands.class);

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_RCVBUF, 64 * 1024)
                    .childOption(ChannelOption.SO_SNDBUF, 64 * 1024)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(64 * 1024))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast("HeaderDecoder", new HeaderDecoder(ServerConfig.MAX_HEADER_SIZE, ServerConfig.HEADER_DELIMITER))
                                    .addLast(new WtpOutboundHandler())
                                    .addLast(new CommandExecutorHandler());
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            log.debug("LSKG Server started on port: {}", port);
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
