package cn.com.gps169.jt808.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.com.gps169.jt808.protocol.Message;
import cn.com.gps169.jt808.tool.JT808Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码处理器
 * @author tianfei
 *
 */
public class EncodeMessageHandler extends MessageToByteEncoder<Message> {
    
    private final Logger logger = LoggerFactory.getLogger(EncodeMessageHandler.class);

    /* (non-Javadoc)
     * @see io.netty.handler.codec.MessageToByteEncoder#encode(io.netty.channel.ChannelHandlerContext, java.lang.Object, io.netty.buffer.ByteBuf)
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out)
            throws Exception {
        ByteBuf temp = Unpooled.buffer(100);
        ByteBuf resp = Unpooled.buffer(100);
        //组装消息体
        temp.writeBytes(msg.getHead().getByteBuffer());
        temp.writeBytes(msg.getBody().encodeBody());
        temp.markReaderIndex();
        //校验码
        byte validCode = temp.readByte();
        while(temp.isReadable()){
            validCode ^= temp.readByte();
        }
        temp.writeByte(validCode);
        temp.resetReaderIndex();
        //编译
        resp.writeByte(JT808Constants.PROTOCOL_0x7E);
        while(temp.isReadable()){
            byte  b = temp.readByte();
            if(b == JT808Constants.PROTOCOL_0x7E){
                resp.writeByte(JT808Constants.PROTOCOL_0x7D);
                resp.writeByte(JT808Constants.PROTOCOL_0x02);
            } else if(b == JT808Constants.PROTOCOL_0x7D){
                resp.writeByte(JT808Constants.PROTOCOL_0x7D);
                resp.writeByte(JT808Constants.PROTOCOL_0x01);
            } else {
                resp.writeByte(b);
            }
        }
        resp.writeByte(JT808Constants.PROTOCOL_0x7E);
        logger.info("send msg >> " + ByteBufUtil.hexDump(resp));
        ctx.writeAndFlush(resp);
    }

}
