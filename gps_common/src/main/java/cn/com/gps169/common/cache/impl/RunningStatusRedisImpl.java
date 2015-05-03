package cn.com.gps169.common.cache.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.gps169.common.cache.IRunningStatusCacheManager;
import cn.com.gps169.common.model.RunningState;
import cn.com.gps169.common.tool.DateUtil;
import cn.com.gps169.common.tool.JsonPluginsUtil;
import cn.com.gps169.common.tool.ShardedJedisPoolFactory;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Service
public class RunningStatusRedisImpl implements IRunningStatusCacheManager {

	private static Logger logger = LoggerFactory.getLogger(RunningStatusRedisImpl.class);

	/**
	 * running_state缓存键值前缀
	 */
	public static final String RUNNING_STATE_CACHE_KEY_PREFIX = "jt808:v.rs.";

	@Override
	public RunningState findLatestRunningState(int vehicleId,Date date) {
		String key = getKey(vehicleId, date);
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			List<String> srcList = shardedJedis.lrange(key, -1, -1);
			if (srcList == null || srcList.size() == 0) {
				return null;
			}
			RunningState res = JsonPluginsUtil.jsonToBean(srcList.get(0), RunningState.class);
			return res;
		} catch (JedisConnectionException e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
			shardedJedis = null;
			String errorMsg = "查询车辆最新running_state数据时无法链接到Redis服务器！vehicleId="
					+ vehicleId;
			logger.error(errorMsg, e);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}

		return null;
	}
	
	

	@Override
	public void saveRunningState(RunningState s) {
		if(s == null){
			return;
		}
		String key = getKey(s.getVid(), s.getReceivedTime());
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			shardedJedis.rpush(key, JsonPluginsUtil.beanToJson(s));
		} catch (JedisConnectionException e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
			shardedJedis = null;
			String errorMsg = "保存RunningState数据时无法链接到Redis服务器！"
					+ s.toString();
			logger.error(errorMsg, e);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}
	}
	
	private String getKey(int vehicleId,Date date){
		return RUNNING_STATE_CACHE_KEY_PREFIX + vehicleId + "."+DateUtil.DATEFORMATER().format(date);
	}



	@Override
	public LinkedList<RunningState> findRunningStates(int vehicleId, Date date) {
		String key = getKey(vehicleId, date);
		ShardedJedis shardedJedis = null;
		try{
			shardedJedis = ShardedJedisPoolFactory.getResource();
			List<String> list = shardedJedis.lrange(key, 0, -1);
			LinkedList<RunningState> result = new LinkedList<RunningState>();
			for(String str : list){
				result.add((RunningState) JSONObject.toBean(JSONObject.fromObject(str), RunningState.class));
			}
			return result;
		} catch (JedisConnectionException e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
			shardedJedis = null;
			String errorMsg = "查询车辆最新running_state数据时无法链接到Redis服务器！vehicleId=" + vehicleId;
			logger.error(errorMsg, e);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}
		
		return null;
	}

}
