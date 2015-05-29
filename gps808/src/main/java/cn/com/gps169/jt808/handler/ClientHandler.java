/**
 * 
 * tianfei
 * 2015年5月29日下午3:05:28
 */
package cn.com.gps169.jt808.handler;

import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author tianfei
 *
 */
public class ClientHandler extends ChannelHandlerAdapter {
    
    private int count = 0;
    private byte DELIMITER = 0x7e;

    /* (non-Javadoc)
     * @see io.netty.channel.ChannelHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        // TODO Auto-generated method stub
        super.exceptionCaught(ctx, cause);
    }

    /* (non-Javadoc)
     * @see io.netty.channel.ChannelHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf buf = null;
        for(int i = 0; i < 10; i++){
            buf = Unpooled.buffer(10);
            buf.writeByte(0x7e);
            buf.writeInt(i);
            buf.writeInt(i);
            buf.writeByte(0x7e);
            ctx.writeAndFlush(buf);
        }
    }

}
