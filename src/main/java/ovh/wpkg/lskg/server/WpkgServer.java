package ovh.wpkg.lskg.server;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.command.commands.DefaultCommands;
import ovh.wpkg.lskg.server.handler.*;
import ovh.wpkg.lskg.server.command.CommandRegistry;
import ovh.wpkg.lskg.server.services.ConnectedRatService;
import ovh.wpkg.lskg.server.services.WtpClientService;

@Slf4j
@Singleton
@AllArgsConstructor
public class WpkgServer implements ApplicationEventListener<StartupEvent> {
    private static final int port = 5000;

    private final ApplicationContext applicationContext;
    private final WtpClientService wtpClientService;
    private final CommandRegistry commandRegistry;
    private final ConnectedRatService connectedRatService;

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        commandRegistry.registerCommand(applicationContext.createBean(DefaultCommands.class));

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
                                    .addLast(new ChannelMonitor(wtpClientService))
                                    .addLast(new WtpOutboundHandler())
                                    .addLast(new WtpDecoder())
                                    .addLast(new PayloadLogicHandler(wtpClientService, commandRegistry, connectedRatService));
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
