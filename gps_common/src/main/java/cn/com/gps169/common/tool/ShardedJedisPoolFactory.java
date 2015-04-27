package cn.com.gps169.common.tool;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * redis工具类
 * @author tianfei
 *
 */
public class ShardedJedisPoolFactory {
	 private static transient final Logger LOGGER = LoggerFactory.getLogger(ShardedJedisPoolFactory.class);

	    private static Properties properties = new Properties();
	    private static ShardedJedisPool shardedJedisPool;

	    static {
	        try {
	            try {
	                InputStream inputStream = ConfigUtil.getConfigReader().getResourceAsStream("redis.cfg");
	                properties.load(inputStream);
	            } catch (Exception e) {
	                LOGGER.error(String.format(
	                        "Redis配置文件redis.cfg读取失败：%s", e.getMessage()));
	                throw new Exception(String.format(
	                        "Redis配置文件redis.cfg读取失败：%s", e.getMessage()));
	            }

	            String hosts = properties.getProperty("redis.hosts");
	            if (StringUtils.isBlank(hosts)) {
	                LOGGER.error("error.internal", String.format(
	                        "Redis配置文件redis.cfg解析失败：%s", "hosts服务器列表不能为空"));
	            }

	            JedisPoolConfig jConf = new JedisPoolConfig();
	            jConf.setMaxIdle(500);
	            jConf.setMaxTotal(5000);

	            List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
	            JedisShardInfo js = null;
	            String[] hostList = hosts.trim().split(";");
	            for (String h : hostList) {
	                String[] pair = h.split(":");
	                String hostname = pair[0];
	                Integer port = Integer.parseInt(pair[1]);
	                js = new JedisShardInfo(hostname, port,10000);
	                if(pair.length > 2){
	                    js.setPassword(pair[2]);
	                }
	                shards.add(js);
	            }

	            shardedJedisPool = new ShardedJedisPool(jConf, shards);
	        } catch (Exception e) {
	            LOGGER.error(String.format("ShardedJedisPool连接池初始化失败：%s", e.getMessage()), e);
	        }
	    }

	    /**
	     * 根据数据源名称获取链接对象
	     * 
	     * @param dataSourceName
	     * @return
	     * @throws InternalErrorException
	     */
	    public static ShardedJedis getResource() {
	        ShardedJedis jedis = null;
	        if (shardedJedisPool != null) {
	            int tryCount = 0;
	            int maxTryCount = 3;
	            while (tryCount < maxTryCount) {
	                try {
	                    tryCount++;
	                    jedis = shardedJedisPool.getResource();
	                    break;
	                } catch (JedisConnectionException e) {
	                    shardedJedisPool.returnBrokenResource(jedis);
	                    if (tryCount == maxTryCount) {
	                        LOGGER.warn(e.getMessage(), e);
	                        throw e;
	                    } else {
	                        continue;
	                    }
	                }
	            }
	        } else {
	            LOGGER.error("500", "从连接池中获取链接对象失败，连接池未初始化");
	        }

	        return jedis;
	    }

	    /**
	     * 回收链接对象
	     * 
	     * @param dataSourceName
	     * @param conn
	     */
	    public static void returnResource(ShardedJedis conn) {
	        if (conn == null) {
	            return;
	        }

	        if (shardedJedisPool != null) {
	            try {
	                shardedJedisPool.returnResource(conn);
	            } catch (Exception e) {
	                LOGGER.info("This jedis alread removed!");
	            }
	        }
	    }

	    public static void returnBrokenResource(ShardedJedis conn) {
	        if (conn == null) {
	            return;
	        }

	        if (shardedJedisPool != null) {
	            shardedJedisPool.returnBrokenResource(conn);
	        }
	    }
}
