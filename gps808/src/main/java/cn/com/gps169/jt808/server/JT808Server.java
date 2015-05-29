package cn.com.gps169.jt808.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.gps169.jt808.handler.DecodeMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author tianfei
 *
 */
public class JT808Server {
    
    private transient static Logger logger = LoggerFactory.getLogger(JT808Server.class);
    
    private static final int SERVER_PORT = 9991;

    /**
     * @param args
     */
    public static void main(String[] args) {
        //线程池定义 
        EventLoopGroup acceptorGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //服务器参数
        ServerBootstrap server = new ServerBootstrap();
        server.group(acceptorGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 10000)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 定义职责链
                    ch.pipeline().addLast(new DecodeMessageHandler());
                }
            });
        try {
            //启动服务器
            ChannelFuture future = server.bind(SERVER_PORT).sync();
            logger.info("JT808服务器启动成功,端口号："+SERVER_PORT);
            //监听服务器关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("JT808服务启动失败，错误信息："+e.getMessage());
        } finally {
            //退出,释放资源
            acceptorGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
