package cn.com.gps169.jt808.handler;

import java.util.List;

import cn.com.gps169.jt808.protocol.Message;
import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * @author tianfei
 *
 */
public class DecodeMessageClientHandler extends ByteToMessageDecoder {
    
    private byte DELIMITER = 0x7e;
    private int count = 0;

    /* (non-Javadoc)
     * @see io.netty.handler.codec.ByteToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println(ByteBufUtil.hexDump(in));
        //解决TCP粘包、拆包
        if(in.readableBytes() <= 0){
            return;
        }
        in.markReaderIndex();
        int length = 0;
        if(in.readByte() != DELIMITER || (length = in.bytesBefore(DELIMITER)) == -1){
            in.resetReaderIndex();
            return;
        }
        in.resetReaderIndex();
        // 解析消息
        ByteBuf message = Unpooled.buffer(length+2);
        in.readBytes(message);
        out.add(parseMessage(message));
    }
    
    /**
     * 解析消息体
     * @param buf
     * @return
     */
    private Message parseMessage(ByteBuf buf){
        Message msg = new Message();
        
        return msg;
    }

}
