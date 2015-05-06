package cn.com.gps169.common.cache.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.gps169.common.cache.ITmnlVehiCacheManager;
import cn.com.gps169.common.tool.ShardedJedisPoolFactory;
import cn.com.gps169.db.dao.TerminalVehicleMapper;
import cn.com.gps169.db.model.TerminalVehicle;
import cn.com.gps169.db.model.TerminalVehicleExample;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

@Component
public class TmnlVehiRedisImpl implements ITmnlVehiCacheManager {

	private static transient final Logger LOGGER = LoggerFactory.getLogger(TmnlVehiRedisImpl.class);

	private static final String TVR_TID_RELATION_CACHE_PREFIX = "jt808:buzi:tvr:tid:relation:";
	private static final String TVR_VID_TID_CACHE_PREFIX = "jt808:buzi:tvr:vid:tid:";
	private static final String TVR_CACHE_STATUS = "jt808:buzi:tvr:status";

	@Autowired
	private TerminalVehicleMapper terminalVehicleMapper;

	@Override
	public void initCache() {
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String status = shardedJedis.get(TVR_CACHE_STATUS);
//			if (StringUtils.isBlank(status) || status.equals("0")) {
				//查询车辆与终端绑定关系
				TerminalVehicleExample example = new TerminalVehicleExample();
				List<TerminalVehicle> relations = terminalVehicleMapper.selectByExample(example);
				for (TerminalVehicle r : relations) {
					JSONObject jsonObj = JSONObject.fromObject(r);
					shardedJedis.set(TVR_TID_RELATION_CACHE_PREFIX + r.getTerminalId(),
							jsonObj.toString());
					shardedJedis.set(TVR_VID_TID_CACHE_PREFIX + r.getVehicleId(),
							String.valueOf(r.getTerminalId()));
				}

				shardedJedis.set(TVR_CACHE_STATUS, "1");
				LOGGER.info("TerminalVehicleRelation缓存初始化执行完成,共初始化"
						+ relations.size() + "个终端车辆绑定信息到缓存中!");

//			}
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}
	}

	@Override
	public TerminalVehicle findCurBindRelationsByVehicleId(int vehicleId) {
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = TVR_VID_TID_CACHE_PREFIX + vehicleId;
			String tid = shardedJedis.get(key);
			if (!StringUtils.isBlank(tid)
					&& shardedJedis.exists(TVR_TID_RELATION_CACHE_PREFIX + tid)) {
				String value = shardedJedis.get(TVR_TID_RELATION_CACHE_PREFIX
						+ tid);
				JSONObject obj = JSONObject.fromObject(value);
				TerminalVehicle r = (TerminalVehicle) JSONObject.toBean(obj,
						TerminalVehicle.class);

				return r;
			} else {
				TerminalVehicleExample baseExample = new TerminalVehicleExample();
				baseExample.or().andVehicleIdEqualTo(vehicleId);
				List<TerminalVehicle> bindRelations = terminalVehicleMapper
						.selectByExample(baseExample);
				if (bindRelations == null || bindRelations.size() == 0) {
					return null;
				}
				addBindRelation(bindRelations.get(0));

				return bindRelations.get(0);
			}
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}

		return null;
	}

	@Override
	public TerminalVehicle findCurBindRelationsByTerminalId(int terminalId) {
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = TVR_TID_RELATION_CACHE_PREFIX + terminalId;
			if (shardedJedis.exists(key)) {
				String value = shardedJedis.get(key);
				JSONObject obj = JSONObject.fromObject(value);
				TerminalVehicle r = (TerminalVehicle) JSONObject.toBean(obj,
						TerminalVehicle.class);

				return r;
			} else {
				TerminalVehicleExample baseExample = new TerminalVehicleExample();
				baseExample.or().andTerminalIdEqualTo(terminalId);
				List<TerminalVehicle> bindRelations = terminalVehicleMapper
						.selectByExample(baseExample);
				if (bindRelations == null || bindRelations.size() == 0) {
					return null;
				}

				addBindRelation(bindRelations.get(0));

				return bindRelations.get(0);
			}
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}

		return null;
	}

	@Override
	public TerminalVehicle addBindRelation(TerminalVehicle r) {
		if (r.getTerminalId() == null || r.getVehicleId() == null) {
			LOGGER.error("向缓存中写入终端车辆绑定关系时发现terminalId或vehicleId为空！");
			return null;
		}

		if (r.getTerminalVehiclesId() == null) {
			// 查询出已经写入数据库的终端记录
			TerminalVehicleExample baseExample = new TerminalVehicleExample();
			baseExample.or().andTerminalIdEqualTo(r.getTerminalId())
					.andVehicleIdEqualTo(r.getVehicleId());
			List<TerminalVehicle> relations = terminalVehicleMapper.selectByExample(baseExample);
			if (relations == null || relations.size() == 0) {
				//保存至数据库
				terminalVehicleMapper.insertSelective(r);
			}
		}

		JSONObject jsonObj = JSONObject.fromObject(r);
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			shardedJedis.set(TVR_TID_RELATION_CACHE_PREFIX + r.getTerminalId(),
					jsonObj.toString());
			shardedJedis.set(TVR_VID_TID_CACHE_PREFIX + r.getVehicleId(),
					String.valueOf(r.getTerminalId()));
		} catch (Exception e) {
			LOGGER.error("增加车辆、终端绑定关系失败，错误信息："+e.getMessage());
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}

		return r;
	}

	@Override
	public void removeBindRelation(int vehicleId,int terminalId) {
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			Collection<Jedis> list = shardedJedis.getAllShards();
			for(Jedis j : list){
				j.del(TVR_TID_RELATION_CACHE_PREFIX + terminalId);
				j.del(TVR_VID_TID_CACHE_PREFIX + vehicleId);
			}
		} catch (Exception e) {
			LOGGER.error("删除车辆、终端绑定关系失败，异常信息："+e.getMessage());
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}
	}

}
