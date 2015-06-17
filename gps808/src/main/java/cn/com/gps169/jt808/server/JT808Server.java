package cn.com.gps169.jt808.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.gps169.common.cache.ICacheManager;
import cn.com.gps169.common.model.VehicleVo;
import cn.com.gps169.jt808.handler.CenterHandler;
import cn.com.gps169.jt808.handler.DecodeMessageHandler;
import cn.com.gps169.jt808.handler.EncodeMessageHandler;
import cn.com.gps169.jt808.handler.HeartbeatHandler;
import cn.com.gps169.jt808.handler.LoginHandler;
import cn.com.gps169.jt808.tool.JT808Constants;
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
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * JT808服务器
 * @author tianfei
 *
 */
@Component("jt808Server")
public class JT808Server {
    
    private transient static Logger logger = LoggerFactory.getLogger(JT808Server.class);
    /**
     * 通道集合
     */
    private ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    
    /**
     * 端口号
     */
    private static final int SERVER_PORT = 9991;
    
    @Autowired
    private CenterHandler centerHandler;
    
    @Autowired
    private LoginHandler loginHandler;
    
    @Autowired
    private ICacheManager cacheManager;
    
    /**
     * 客户端连接状态,KEY:SIM; VALUE:CHANNELID
     */
    private ConcurrentHashMap<String, ChannelId> channelConnections = new ConcurrentHashMap<String, ChannelId>();
    
    /**
     * 客户端连接关系; KEY:channelId,VALUE:sim
     */
    private ConcurrentHashMap<String, String> channels = new ConcurrentHashMap<String, String>();

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
                    //超时处理
                    ch.pipeline().addLast("readTimeoutHandler",new ReadTimeoutHandler(30, TimeUnit.SECONDS));
                    ch.pipeline().addLast(new JT808Server.ConnectionHandler());
                    ch.pipeline().addLast(loginHandler)
                        .addLast(new HeartbeatHandler())
                        .addLast(centerHandler);
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
            logger.info("客户端与服务器断开连接");
            String channelId = ctx.channel().id().asShortText();
            if(channels.contains(channelId)){
            	setOffline(channelId);
            }
            allChannels.remove(ctx.channel());
            super.channelInactive(ctx);
        }

        /* (non-Javadoc)
         * @see io.netty.channel.ChannelHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
        	// 心跳超时断开连接
        	if(cause instanceof ReadTimeoutException){
        		String channelId = ctx.channel().id().asShortText();
                if(channels.contains(channelId)){
                	setOffline(channelId);
                }	
        	}
            ctx.close();
            cause.printStackTrace();
        }
        
        /**
         * 设置车辆终端离线
         * @param channelId
         */
        private void setOffline(String channelId){
        	String sim = channels.get(channelId);
        	// 设置车辆终端为离线状态
        	VehicleVo vehicle = cacheManager.findVehicleBySim(sim);
        	vehicle.setTerminalStatus(JT808Constants.VEHICLE_TERMINAL_ONLINE_OFFLINE);
        	cacheManager.updateVehicle(vehicle);
        	channels.remove(channelId);
        	channelConnections.remove(sim);
        }
    }
    
    
    /**
     * 根据channelId查询channel
     * @param channelId
     * @return
     */
    public Channel getChannel(String simNo){
        ChannelId channelId = this.channelConnections.get(simNo);
        
        return allChannels.find(channelId);
    }

    /**
     * 判断是否已经连接
     * @param simNo
     * @return
     */
    public boolean isConnectioned(String simNo) {
        return this.channelConnections.containsKey(simNo);
    }
    

    /**
     * @param channelConnections the channelConnections to set
     */
    public void setChannel(String simNo,ChannelId channelId) {
        this.channelConnections.put(simNo, channelId);
        this.channels.put(channelId.asShortText(), simNo);
    }
}
