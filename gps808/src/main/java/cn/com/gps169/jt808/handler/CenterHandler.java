package cn.com.gps169.jt808.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.gps169.common.thread.MyThreadFactory;
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
    
    private static int nThreads = Runtime.getRuntime().availableProcessors() * 1;//处理的线程数
    private static int MAX_QUEUQ_SIZE = 100;
    
    // 线程池
    private ExecutorService executorService = new ThreadPoolExecutor(nThreads, nThreads, 1, TimeUnit.HOURS, new ArrayBlockingQueue<Runnable>(MAX_QUEUQ_SIZE),
            new MyThreadFactory("CenerHandler-Thread"),new ThreadPoolExecutor.CallerRunsPolicy());
    
    /* (non-Javadoc)
     * @see io.netty.channel.ChannelHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj)
            throws Exception {
        Message msg = (Message) obj;
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
            MessageHead head = (MessageHead) msg.getHead().clone();
            head.setMessageId(0x8001);
            Message response = new Message(head,rbody);
            ctx.write(response);
        }
        //线程池调用消息处理器
        executorService.execute(new ProcThread(msg));
    }
    
    /**
     * 消息处理器调用线程
     * @author tianfei
     *
     */
    private class ProcThread implements Runnable{
    	private Message msg;
    	
    	public ProcThread(Message msg){
    		this.msg = msg;
    	}

		@Override
		public void run() {
			//消息处理器调用
			Proc proc =  codeHandler.get(msg.getMessageID());
	        proc.proc(msg);
		}
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
