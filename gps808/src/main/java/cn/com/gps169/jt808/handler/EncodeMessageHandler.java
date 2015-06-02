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
import io.netty.util.ReferenceCountUtil;

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
        out.writeByte(JT808Constants.PROTOCOL_0x7E);
        while(temp.isReadable()){
            byte  b = temp.readByte();
            if(b == JT808Constants.PROTOCOL_0x7E){
                out.writeByte(JT808Constants.PROTOCOL_0x7D);
                out.writeByte(JT808Constants.PROTOCOL_0x02);
            } else if(b == JT808Constants.PROTOCOL_0x7D){
                out.writeByte(JT808Constants.PROTOCOL_0x7D);
                out.writeByte(JT808Constants.PROTOCOL_0x01);
            } else {
                out.writeByte(b);
            }
        }
        out.writeByte(JT808Constants.PROTOCOL_0x7E);
        logger.info(ByteBufUtil.hexDump(out));
        ReferenceCountUtil.retain(out);
        out.discardReadBytes();
        ctx.writeAndFlush(out);
    }

}
