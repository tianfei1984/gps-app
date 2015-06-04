package cn.com.gps169.common.kafka;

/**
 * 提供消息操作的回调接口
 * 
 */
public interface MessageHandler {
    /**
     * 消息回调提供的调用方法
     * 
     * @param message
     */
    public void handle(String message);
}
