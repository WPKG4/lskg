package ovh.wpkg.lskg.server;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.command.commands.DefaultCommands;
import ovh.wpkg.lskg.server.handler.CommandExecutorHandler;
import ovh.wpkg.lskg.server.handler.HeaderDecoder;
import ovh.wpkg.lskg.server.command.CommandRegistry;
import ovh.wpkg.lskg.server.handler.WtpOutboundHandler;

@Slf4j
@Singleton
public class WpkgServer implements ApplicationEventListener<StartupEvent> {
    // TODO: Add loading from file
    private static final int port = 5000;

    @Inject
    private DefaultCommands defaultCommands;

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        CommandRegistry.registerCommand(defaultCommands);

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
                                    .addLast("HeaderDecoder", new HeaderDecoder())
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

    @Override
    public void onApplicationEvent(StartupEvent event) {
        Thread.ofVirtual().start(() -> {
            try {
                start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
