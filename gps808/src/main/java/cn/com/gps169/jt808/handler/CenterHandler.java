package cn.com.gps169.jt808.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.gps169.jt808.proc.Proc;
import cn.com.gps169.jt808.protocol.EMsgAck;
import cn.com.gps169.jt808.protocol.Message;
import cn.com.gps169.jt808.protocol.MessageHead;
import cn.com.gps169.jt808.protocol.impl.JT8001;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * @author tianfei
 *
 */
@Component
@Sharable
public class CenterHandler extends ChannelHandlerAdapter {
    
    private final Logger logger = LoggerFactory.getLogger(CenterHandler.class);
    
    private Map<String, Proc> codeHandler = new HashMap<String, Proc>();
    
    /**
     * 平台通用响应列表
     */
    private List<String> platformCommReponse = new ArrayList<String>();

    /* (non-Javadoc)
     * @see io.netty.channel.ChannelHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj)
            throws Exception {
        Message msg = (Message) obj;
//        //判断终端是否存在
//        Terminal tmnl = terminalCacheManager.getTerminalBySimNo(msg.getSimNo());
//        if(tmnl == null){
//            logger.error("sim卡号："+msg.getSimNo()+"的终端未在该平台注册！");
//            return;
//        }
        //调用 消息处理器
        Proc proc =  codeHandler.get(msg.getMessageID());
        if(proc == null){
            logger.info(String.format("没有相应的处理器或平台不需要处理，消息ID:%s;内容：",msg.getMessageID(),msg.toString()));
            return;
        }
        //平台通用响应
        if(platformCommReponse.contains(msg.getMessageID())){
            //回复消息
            JT8001 rbody = new JT8001(msg.getHead().getFlowNo(), msg.getHead().getMessageId(), EMsgAck.SUCESS.value());
            MessageHead head = msg.getHead();
            head.setMessageId(0x8001);
            Message response = new Message(head,rbody);
            ctx.write(response);
        }
        //消息处理器调用
        proc.proc(msg);
    }

    /**
     * @param codeHandler the codeHandler to set
     */
    public void setCodeHandler(Map<String, Proc> codeHandler) {
        this.codeHandler = codeHandler;
    }

    /**
     * @param platformCommReponse the platformCommReponse to set
     */
    public void setPlatformCommReponse(List<String> platformCommReponse) {
        this.platformCommReponse = platformCommReponse;
    }
}
