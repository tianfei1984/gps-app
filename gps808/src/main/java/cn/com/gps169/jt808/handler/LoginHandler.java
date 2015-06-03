package cn.com.gps169.jt808.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.gps169.jt808.protocol.EMsgAck;
import cn.com.gps169.jt808.protocol.Message;
import cn.com.gps169.jt808.protocol.MessageHead;
import cn.com.gps169.jt808.protocol.impl.JT0102;
import cn.com.gps169.jt808.protocol.impl.JT8001;
import cn.com.gps169.jt808.tool.JT808Constants;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author tianfei
 *
 */
public class LoginHandler extends ChannelHandlerAdapter {
    
    private final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    //鉴权标识
    private volatile boolean isAuthen = false;

    /* (non-Javadoc)
     * @see io.netty.channel.ChannelHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj)
            throws Exception {
        Message msg = (Message) obj;
        //终端鉴权验证
        if(msg.getHead().getMessageId() == 0x0102){
            byte optResult = 0;
            JT0102 body = (JT0102) msg.getBody();
            String code = body.getAuthorCode();
            //判断终端鉴权码是否正确 
            if(!JT808Constants.AUTHENTICATION_CODE.equals(code)){
                logger.error("终端鉴权失败，"+msg.getSimNo());
                optResult = EMsgAck.FAILURE.value();
            } else {
                //设置连接鉴权成功
                logger.info("终端鉴权成功，sim:"+msg.getSimNo());
                isAuthen = true;
            }
            //消息回复
            MessageHead resHead = msg.getHead();
            JT8001 rbody = new JT8001(resHead.getFlowNo(),msg.getHead().getMessageId(), optResult);
            resHead.setMessageId(0x8001);
            Message response = new Message(resHead,rbody);
            ctx.write(response);
        } else if(!isAuthen && msg.getHead().getMessageId() != 0x0100) {
            //终端注册
            logger.error("终端未鉴权断开连接，SIM："+msg.getSimNo());
            ctx.close();
        } else {
            ctx.fireChannelRead(obj);
        }
    }
    
}
