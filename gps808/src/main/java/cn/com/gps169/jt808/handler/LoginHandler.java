package cn.com.gps169.jt808.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import cn.com.gps169.common.cache.ICacheManager;
import cn.com.gps169.common.model.VehicleVo;
import cn.com.gps169.jt808.protocol.EMsgAck;
import cn.com.gps169.jt808.protocol.Message;
import cn.com.gps169.jt808.protocol.MessageHead;
import cn.com.gps169.jt808.protocol.impl.JT0102;
import cn.com.gps169.jt808.protocol.impl.JT8001;
import cn.com.gps169.jt808.server.JT808Server;
import cn.com.gps169.jt808.tool.JT808Constants;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * @author tianfei
 *
 */
@Component
@Sharable
public class LoginHandler extends ChannelHandlerAdapter {
    
    private final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    
    @Autowired
    private ICacheManager cacheManager;
    
    @Autowired
    private JT808Server jt808Server;

    /* (non-Javadoc)
     * @see io.netty.channel.ChannelHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj)
            throws Exception {
        Message msg = (Message) obj;
        //终端鉴权验证
        VehicleVo vehicle = cacheManager.findVehicleBySim(msg.getSimNo());
        if(msg.getHead().getMessageId() == 0x0102){
            byte optResult = 0;
            JT0102 body = (JT0102) msg.getBody();
            String code = body.getAuthorCode();
            //判断终端鉴权码是否正确 
            if(vehicle == null) {
            	logger.error("终端鉴权失败，终端不存在 " +msg.getSimNo());
            	optResult = EMsgAck.FAILURE.value();
            } else if(!JT808Constants.AUTHENTICATION_CODE.equals(code)){
                logger.error("终端鉴权失败，鉴权码错误："+msg.getSimNo());
                optResult = EMsgAck.FAILURE.value();
            } else {
                //设置连接鉴权成功
                logger.info("终端鉴权成功，sim:"+msg.getSimNo());
                // 设置车辆终端上线
                vehicle.setTerminalStatus(JT808Constants.VEHICLE_TERMINAL_STATUS_ONLINE);
                cacheManager.updateVehicle(vehicle);
                //保存连接终端与SIM卡连接关系
                jt808Server.setChannel(vehicle.getSimNo(), ctx.channel().id());
            }
            //消息回复
            MessageHead resHead = msg.getHead();
            JT8001 rbody = new JT8001(resHead.getFlowNo(),msg.getHead().getMessageId(), optResult);
            resHead.setMessageId(0x8001);
            Message response = new Message(resHead,rbody);
            ctx.write(response);
        } else if(vehicle.getTerminalStatus() != JT808Constants.VEHICLE_TERMINAL_STATUS_ONLINE 
                && msg.getHead().getMessageId() != 0x0100) {
            //终端注册
            logger.error("终端未鉴权断开连接，SIM："+msg.getSimNo());
            ctx.close();
        } else {
            ctx.fireChannelRead(obj);
        }
    }
    
}
