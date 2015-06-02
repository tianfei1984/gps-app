package cn.com.gps169.jt808.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.gps169.jt808.handler.CenterHandler;
import cn.com.gps169.jt808.handler.DecodeMessageHandler;
import cn.com.gps169.jt808.handler.EncodeMessageHandler;
import cn.com.gps169.jt808.handler.HeartbeatHandler;
import cn.com.gps169.jt808.handler.LoginHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author tianfei
 *
 */
@Component("jt808Server")
public class JT808Server {
    
    private transient static Logger logger = LoggerFactory.getLogger(JT808Server.class);
    
    private ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    
    private static final int SERVER_PORT = 9991;
    
    @Autowired
    private CenterHandler centerHandler;

    /**
     * 启动服务
     */
    public void start() {
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
                    ch.pipeline().addLast(new EncodeMessageHandler());
                    ch.pipeline().addLast(new JT808Server.ConnectionHandler());
                    ch.pipeline().addLast(new LoginHandler()).addLast(new HeartbeatHandler()).addLast(centerHandler);
                }
            });
        try {
            //启动服务器
            ChannelFuture future = server.bind(SERVER_PORT).sync();
            allChannels.add(future.channel());
            logger.info("JT808服务器启动成功,端口号："+SERVER_PORT);
            //监听服务器关闭
            future.channel().closeFuture().sync();
            // Close the serverChannel and then all accepted connections.
            allChannels.close().awaitUninterruptibly();
        } catch (InterruptedException e) {
            logger.error("JT808服务启动失败，错误信息："+e.getMessage());
        } finally {
            //退出,释放资源
            acceptorGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    
    /**
     * TCP连接处理器
     * @author tianfei
     *
     */
    private class ConnectionHandler extends ChannelHandlerAdapter {
        
        private final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

        /* (non-Javadoc)
         * @see io.netty.channel.ChannelHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // TODO Auto-generated method stub
            logger.info("客户端连接服务器成功");
            allChannels.add(ctx.channel());
            super.channelActive(ctx);
        }

        /* (non-Javadoc)
         * @see io.netty.channel.ChannelHandlerAdapter#close(io.netty.channel.ChannelHandlerContext, io.netty.channel.ChannelPromise)
         */
        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise)
                throws Exception {
            // TODO Auto-generated method stub
            logger.info("服务器断开与客户端的连接");
            super.close(ctx, promise);
        }

        /* (non-Javadoc)
         * @see io.netty.channel.ChannelHandlerAdapter#channelInactive(io.netty.channel.ChannelHandlerContext)
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            allChannels.remove(ctx.channel());
            logger.info("客户端与服务器断开连接");
            super.channelInactive(ctx);
        }

        /* (non-Javadoc)
         * @see io.netty.channel.ChannelHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            ctx.close();
            cause.printStackTrace();
        }
    }
    
    /**
     * 根据channelId查询channel
     * @param channelId
     * @return
     */
    public Channel getChannel(ChannelId channelId){
        
        return allChannels.find(channelId);
    }

}
