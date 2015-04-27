package cn.com.gps169.common.cache.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.gps169.common.cache.IVehicleCacheManager;
import cn.com.gps169.common.tool.ShardedJedisPoolFactory;
import cn.com.gps169.db.dao.VehicleMapper;
import cn.com.gps169.db.model.Vehicle;
import cn.com.gps169.db.model.VehicleExample;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

@Component
public class VehicleRedisImpl implements IVehicleCacheManager {
	private static transient final Logger LOGGER = LoggerFactory
			.getLogger(VehicleRedisImpl.class);
	private static final String VEHICLE_VID_VEHICLE_CACHE_PREFIX = "jt808:buzi:vehicle:vid:vehicle:";
	private static final String VEHICLE_LICENSE_PLATE_CACHE_PREFIX = "jt808:buzi:vehicle:plate:vehicle:";
	private static final String VEHICLE_CACHE_STATUS = "jt808:buzi:vehicle:cache:status";

	@Autowired
	private VehicleMapper vehicleMapper;

	@Override
	public void initCache() {
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String status = shardedJedis.get(VEHICLE_CACHE_STATUS);
//			if (StringUtils.isBlank(status) || status.equals("0")) {
				// 车辆信息
				VehicleExample example = new VehicleExample();
				example.or().andStatusEqualTo(1);			//正常车辆
				List<Vehicle> vehicles = vehicleMapper.selectByExample(example);
				for (Vehicle v : vehicles) {
					JSONObject jsonObj = JSONObject.fromObject(v);
					shardedJedis.set(VEHICLE_VID_VEHICLE_CACHE_PREFIX + v.getVehicleId(), jsonObj.toString());
					shardedJedis.set(VEHICLE_LICENSE_PLATE_CACHE_PREFIX + v.getLicensePlate(), jsonObj.toString());
				}
				shardedJedis.set(VEHICLE_CACHE_STATUS, "1");
				LOGGER.info("Vehicle缓存初始化执行完成,共初始化" + vehicles.size()
						+ "个车辆信息到缓存中!");
//			}
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
			LOGGER.error("初始化车辆缓存失败",e);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}
	}

	@Override
	public Vehicle findVehicleById(int vehicleId) {
		Vehicle vehicle = null;
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = VEHICLE_VID_VEHICLE_CACHE_PREFIX + vehicleId;
			if (shardedJedis.exists(key)) {
				String value = shardedJedis.get(key);
				JSONObject obj = JSONObject.fromObject(value);
				vehicle = (Vehicle) JSONObject.toBean(obj, Vehicle.class);
			} else {
				VehicleExample example = new VehicleExample();
				example.or().andVehicleIdEqualTo(vehicleId);
				List<Vehicle> list = vehicleMapper.selectByExample(example);
				if (list == null || list.isEmpty()) {
					return vehicle;
				} else {
					vehicle = list.get(0);
				}
				addVehicle(vehicle);
			}
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}

		return vehicle;
	}

	@Override
	public Vehicle addVehicle(Vehicle v) {
		if (v.getVehicleId() == null) {
			LOGGER.error("增加车辆信息到缓存中时车辆id不能为空！");
		}

		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();

			JSONObject jsonObj = JSONObject.fromObject(v);
			shardedJedis.set(VEHICLE_VID_VEHICLE_CACHE_PREFIX + v.getVehicleId(),jsonObj.toString());
			shardedJedis.set(VEHICLE_LICENSE_PLATE_CACHE_PREFIX + v.getLicensePlate(), jsonObj.toString());
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}

		return v;
	}

	@Override
	public Vehicle findVehicleByPlate(String licensePlate) {
		Vehicle vehicle = null;
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = VEHICLE_LICENSE_PLATE_CACHE_PREFIX + licensePlate;
			if (shardedJedis.exists(key)) {
				String value = shardedJedis.get(key);
				JSONObject obj = JSONObject.fromObject(value);
				vehicle = (Vehicle) JSONObject.toBean(obj, Vehicle.class);
			} else {
				VehicleExample example = new VehicleExample();
				example.or().andLicensePlateEqualTo(licensePlate);
				List<Vehicle> list = vehicleMapper.selectByExample(example);
				if (list == null || list.isEmpty()) {
					return vehicle;
				} else {
					vehicle = list.get(0);
				}
				addVehicle(vehicle);
			}
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}

		return vehicle;
	}

	@Override
	public List<Integer> findAllVehicleIds() {
		List<Integer> ids = new ArrayList<Integer>();
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			for (Jedis jedis : shardedJedis.getAllShards()) {
				Set<String> keys = jedis.keys(VEHICLE_VID_VEHICLE_CACHE_PREFIX
						+ "*");
				for (String key : keys) {
					if (!ids.contains(Integer.valueOf(key
							.substring(VEHICLE_VID_VEHICLE_CACHE_PREFIX
									.length())))) {
						ids.add(Integer.valueOf(key
								.substring(VEHICLE_VID_VEHICLE_CACHE_PREFIX
										.length())));
					}
				}
			}
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}

		return ids;
	}
}
