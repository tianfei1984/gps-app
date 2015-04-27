package cn.com.gps169.common.cache.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.gps169.common.cache.ITripCacheManager;
import cn.com.gps169.common.tool.ShardedJedisPoolFactory;
import redis.clients.jedis.ShardedJedis;
import net.sf.json.JSONObject;

@Component
public class TripRedisImpl implements ITripCacheManager {

	private static Logger logger = LoggerFactory.getLogger(TripRedisImpl.class);

	// 车辆的轨迹点信息前缀
	private static final String GPS_TRIP_GPS_PREFIX = "jt808:trip:gps:";

	// 车辆编号与终端编号的关系
	private static final String GPS_TRIP_TERMINAL_PREFIX = "jt808:trip:tid:";

	
	@Override
	public void pushGpsRecord(JSONObject gpsInfo) {
		//yyyymmdd
		int recvDay = Integer.parseInt(gpsInfo.getString("sendTime").substring(0,10).replace("-", ""));
		String key = getRedisKey(gpsInfo.optInt("vid"), recvDay);
		JSONObject record = JSONObject.fromObject(gpsInfo);
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			shardedJedis.append(key, record.toString() + ",");
			String temp = shardedJedis.get(GPS_TRIP_TERMINAL_PREFIX + gpsInfo.optInt("vid"));
			if (StringUtils.isNotBlank(temp) && !temp.equals(String.valueOf(gpsInfo.optInt("tid")))) {
				logger.warn(String.format("车辆[%d]对应的终端编号发生变化：[%s]->[%s]",
						gpsInfo.optInt("vid"), temp, gpsInfo.optInt("tid")));
				shardedJedis.set(GPS_TRIP_TERMINAL_PREFIX + gpsInfo.optInt("vid"),String.valueOf(gpsInfo.optInt("tid")));
			} else if (StringUtils.isBlank(temp)) {
				shardedJedis.set(GPS_TRIP_TERMINAL_PREFIX + gpsInfo.optInt("vid"),String.valueOf(gpsInfo.optInt("tid")));
			}

		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
			String errorMsg = String.format("保存车辆[%d]的轨迹数据%s失败,失败原因：%s",
					gpsInfo.optInt("vid"), record.toString(), e.getMessage());
			logger.error(errorMsg, e);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}
	}

	@Override
	public JSONObject getGpsTrip(int vehicleId, int recvDay) {
		JSONObject result = new JSONObject();
		String trip = "";
		int tid = 0;
		String key = getRedisKey(vehicleId, recvDay);
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			trip = shardedJedis.get(key);
			String strTid = shardedJedis.get(GPS_TRIP_TERMINAL_PREFIX + vehicleId);
			if (StringUtils.isNotBlank(strTid)) {
				tid = Integer.parseInt(strTid);
			}
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
			String errorMsg = String.format("获取车辆[%d]的轨迹数据失败,失败原因：%s",
					vehicleId, e.getMessage());
			logger.error(errorMsg, e);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}
		result.put("trip", trip != null ? trip : "");
		result.put("tid", tid);

		return result;
	}

	@Override
	public void deleteGpsTrip(int vehicleId, int recvDay) {
		String key = getRedisKey(vehicleId, recvDay);

		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			shardedJedis.del(key);
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
			String errorMsg = String.format(
					"删除日期为[%d]车辆编号为[%d]的轨迹数据失败,失败原因：%s", recvDay, vehicleId,
					e.getMessage());
			logger.error(errorMsg, e);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}
	}

	private String getRedisKey(int vehicleId, long recvDay) {
		String key = GPS_TRIP_GPS_PREFIX + recvDay + ":" + vehicleId;
		return key;
	}

}
