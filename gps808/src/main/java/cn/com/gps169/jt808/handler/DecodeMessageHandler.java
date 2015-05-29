package cn.com.gps169.jt808.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.gps169.jt808.protocol.Message;
import cn.com.gps169.jt808.protocol.MessageBody;
import cn.com.gps169.jt808.protocol.MessageHead;
import static cn.com.gps169.jt808.tool.JT808Constants.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * @author tianfei
 *
 */
public class DecodeMessageHandler extends ByteToMessageDecoder {
    
    private transient static Logger logger = LoggerFactory.getLogger(DecodeMessageHandler.class); 
    
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
        //消息还原
        byte[] b = buf.array();
        ByteBuf msgBuf = Unpooled.buffer(b.length-2);
        msgBuf.markReaderIndex();
        for(int i = 1;i<b.length-1;i++){
            if(b[i] == PROTOCOL_0x7E){
                continue;
            }
            if(b[i] == PROTOCOL_0x7D){
                ++i;
                if(b[i] == PROTOCOL_0x02){
                    msgBuf.writeByte(PROTOCOL_0x7E);
                } else if(b[i] == PROTOCOL_0x01){
                    msgBuf.writeByte(PROTOCOL_0x7D);
                }
                continue;
            }
            msgBuf.writeByte(b[i]);
        }
        msgBuf.resetReaderIndex();
        // 解析消息头
        MessageHead head = parseHead(msgBuf.duplicate());
        // 验证消息校验码
        byte validCode = msgBuf.readByte();
        for(int i = 1; i < head.getMsgOffer(); i++){
            validCode ^= msgBuf.readByte();
        }
        if(validCode != msgBuf.readByte()){
            logger.error("消息解析失败，校验码不正确");
            return null;
        }
        
        return msg;
    }
    
    /**
     * 解析消息头
     * @param buf
     * @return
     */
    private MessageHead parseHead(ByteBuf buf){
        MessageHead head = new MessageHead();
        head.setMessageId(buf.readUnsignedShort());//消息ID
        //解析消息体属性
        String str = String.format("%016d", Integer.parseInt(Integer.toBinaryString(buf.readUnsignedShort())));
        head.setSubpackage(Byte.valueOf(str.substring(2,3),2));
        head.setEncryption(Byte.valueOf(str.substring(3,6),2));
        head.setBodyLength(Short.valueOf(str.substring(6),2));
        // SIM卡号
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<6;i++){
            sb.append(String.format("%02X",buf.readUnsignedByte()));
        }
        head.setSimNo(sb.toString());
        //流水号
        head.setFlowNo(buf.readUnsignedShort());
        //消息体内容
        if(head.getBodyLength() > 0){
            ByteBuf bodyBuf = Unpooled.buffer(head.getBodyLength());
            buf.readBytes(bodyBuf);
            head.setBody(bodyBuf.nioBuffer());
        }
        
        return head;
    }
    
    /**
     * 解析消息体
     * @return
     */
    private MessageBody parseBody(){
        return null;
    }

}
