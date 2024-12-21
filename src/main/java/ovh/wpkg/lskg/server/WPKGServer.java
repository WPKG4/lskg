package ovh.wpkg.lskg.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ovh.wpkg.lskg.server.handler.CommandExecutorHandler;
import ovh.wpkg.lskg.server.handler.WPKGDecoder;
import ovh.wpkg.lskg.server.handler.WPKGEncoder;

public class WPKGServer {
    private final int port;

    public WPKGServer(int port) {
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
                            ch.pipeline()
                                    .addLast(new WPKGDecoder())  // Dekodowanie przychodzących danych
                                    .addLast(new CommandExecutorHandler())  // Przetwarzanie danych
                                    .addLast(new WPKGEncoder());  // Kodowanie wychodzących danych
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            System.out.println("Server started on port: " + port);
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
