package cn.com.gps169.common.cache.impl;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.gps169.common.cache.IDataAcquireCacheManager;
import cn.com.gps169.common.tool.ShardedJedisPoolFactory;
import redis.clients.jedis.ShardedJedis;

@Component
public class DataAcquireRedisImpl implements IDataAcquireCacheManager {
	
	private static Logger logger = LoggerFactory.getLogger(DataAcquireRedisImpl.class);

	// 在线状态
	public static final String INTERACT_ONLINE_KEY_PREFIX = "jt808:interact:online:";

	// 行驶状态
	private static final String INTERACT_RUNNING_STATUS_KEY_PREFIX = "jt808:interact:running_status:";

	// 在线状态以及行驶状态的时间控制,多于60秒不上数据就定为离线和停止
	private static final int JEDIS_ONLINE_EXPIRED_SECONDS = 60;
	// 最新的位置信息前缀
	private static final String POSITION_GPS_PREFIX = "jt808:data:position:gps:";

	@Override
	public void setIsOnline(Integer terminalId, Boolean isOnline) {
		ShardedJedis jedis = null;
		try {
			jedis = ShardedJedisPoolFactory.getResource();
			String key = INTERACT_ONLINE_KEY_PREFIX + terminalId;
			jedis.setex(key, JEDIS_ONLINE_EXPIRED_SECONDS,
					String.valueOf(isOnline));
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(jedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(jedis);
		}
	}

	/**
	 * 
	 * @param vehicleId
	 * @param status
	 */
	@Override
	public void setRunningStatus(Integer vehicleId, String status) {
		ShardedJedis jedis = null;
		try {
			jedis = ShardedJedisPoolFactory.getResource();
			jedis.setex(INTERACT_RUNNING_STATUS_KEY_PREFIX + vehicleId,
					JEDIS_ONLINE_EXPIRED_SECONDS, status);
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(jedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(jedis);
		}
	}

	@Override
	public JSONObject getGps(int vehicleId) {
		ShardedJedis shardedJedis = null;
		JSONObject gps = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = POSITION_GPS_PREFIX + vehicleId;
			String value = shardedJedis.get(key);
			if (!StringUtils.isBlank(value)) {
				try {
					gps = JSONObject.fromObject(value);
				} catch (JSONException e) {
					logger.warn("GPS JSON对象解析失败" + e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}
		
		return gps;
	}
	
	@Override
	public void setGps(JSONObject gpsInfo) {
		if (null == gpsInfo) {
			return;
		}
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = POSITION_GPS_PREFIX + gpsInfo.optInt("vid");
			shardedJedis.set(key, gpsInfo.toString());
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}
	}

}
