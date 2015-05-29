/**
 * 
 * tianfei
 * 2015年5月29日下午3:00:39
 */
package cn.com.gps169.jt808.server;

import java.net.InetSocketAddress;

import cn.com.gps169.jt808.handler.ClientHandler;
import cn.com.gps169.jt808.handler.DecodeMessageClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author tianfei
 *
 */
public class Client {

    /**
     * @param args
     */
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).
            handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new DecodeMessageClientHandler());
                    ch.pipeline().addLast(new ClientHandler());
                    
                }
            });
        try {
            ChannelFuture f = bootstrap.connect(new InetSocketAddress("127.0.0.1", 9991)).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            
        }
    }

}
