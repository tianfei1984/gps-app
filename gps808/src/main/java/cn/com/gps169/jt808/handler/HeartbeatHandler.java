package cn.com.gps169.jt808.handler;

import cn.com.gps169.jt808.protocol.EMsgAck;
import cn.com.gps169.jt808.protocol.Message;
import cn.com.gps169.jt808.protocol.MessageHead;
import cn.com.gps169.jt808.protocol.impl.JT8001;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 心跳处理器
 * @author tianfei
 *
 */
public class HeartbeatHandler extends ChannelHandlerAdapter {

    /* (non-Javadoc)
     * @see io.netty.channel.ChannelHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj)
            throws Exception {
        Message msg = (Message) obj;
        // 心跳包处理
        if(msg.getHead().getMessageId() == 0x0002){
            //响应心跳包 
            JT8001 rBody = new JT8001(msg.getHead().getFlowNo(), msg.getHead().getMessageId(), EMsgAck.SUCESS.value());
            MessageHead rHead = msg.getHead();
            rHead.setMessageId(0x8001);
            Message response = new Message(rHead,rBody);
            ctx.write(response);
        } else {
            ctx.fireChannelRead(obj);
        }
    }
}
