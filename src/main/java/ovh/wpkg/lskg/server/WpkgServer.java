package ovh.wpkg.lskg.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.handler.CommandExecutorHandler;
import ovh.wpkg.lskg.server.handler.WtpInboundHandler;
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

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {

                            System.out.println("SSSS");
                            ch.pipeline()
                                    .addLast(new WtpInboundHandler())
                                    .addLast(new CommandExecutorHandler())
                                    .addLast(new WtpOutboundHandler());
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
