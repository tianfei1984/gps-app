package cn.com.gps169.common.cache.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sun.jersey.core.util.Base64;

import cn.com.gps169.common.cache.IVehicleCacheManager;
import cn.com.gps169.common.model.VehicleVo;
import cn.com.gps169.common.pb.VehiclePb;
import cn.com.gps169.common.tool.ShardedJedisPoolFactory;
import cn.com.gps169.db.dao.UserVehicleMapper;
import cn.com.gps169.db.dao.VehicleMapper;
import cn.com.gps169.db.model.UserVehicle;
import cn.com.gps169.db.model.UserVehicleExample;
import cn.com.gps169.db.model.Vehicle;
import cn.com.gps169.db.model.VehicleExample;
import redis.clients.jedis.ShardedJedis;

@Component
public class VehicleRedisImpl implements IVehicleCacheManager {
	private static transient final Logger LOGGER = LoggerFactory
			.getLogger(VehicleRedisImpl.class);
	private static final String VEHICLE_SIM_NO_CACHE_PREFIX = "con:gps808:app:vehicle:sim:";
	private static final String VEHICLE_VID_CACHE_PREFIX = "con:gps808:app:vehicle:vid:";
	private static final String VEHICLE_PLATE_NO_CACHE_PREFIX = "con:gps808:app:vehicle:plateno:";
	private static final String USER_VEHICLE_CACHE_PREFIX = "con:gps808:app:user:vehicle:userId:";

	@Autowired
	private VehicleMapper vehicleMapper;
	
	@Autowired
	private UserVehicleMapper userVehicleMapper;
	
	@Override
	public void initCache() {
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			// 缓存用户与车辆关系
			UserVehicleExample uExample = new UserVehicleExample();
			List<UserVehicle> uvs = userVehicleMapper.selectByExample(uExample);
			for(UserVehicle uv : uvs){
			    
			}
			// 缓存车辆信息
			VehicleExample vExample = new VehicleExample();
			List<Vehicle> vehicles = vehicleMapper.selectByExample(vExample);
			VehicleVo vo = null;
			for(Vehicle v : vehicles){
			    vo = new VehicleVo();
			    BeanUtils.copyProperties(vo, v);
			    addVehicle(vo);
			}
			LOGGER.info("Vehicle缓存初始化执行完成,共初始化个车辆信息到缓存中!");
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
			LOGGER.error("初始化车辆缓存失败",e);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}
	}

	@Override
	public VehicleVo findVehicleById(int vehicleId) {
		Vehicle vehicle = null;
		ShardedJedis shardedJedis = null;
		VehicleVo vo = new VehicleVo(); 
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = VEHICLE_VID_CACHE_PREFIX + vehicleId;
			if (shardedJedis.exists(key)) {
				String value = shardedJedis.get(key);
				key = VEHICLE_SIM_NO_CACHE_PREFIX + value;
				return convert2Veh(shardedJedis.get(key));
			} else {
				VehicleExample example = new VehicleExample();
				example.or().andVehicleIdEqualTo(vehicleId);
				List<Vehicle> list = vehicleMapper.selectByExample(example);
				if (list == null || list.isEmpty()) {
					return null;
				} else {
					vehicle = list.get(0);
				}
				//缓存车辆信息
		        BeanUtils.copyProperties(vo, vehicle);
				addVehicle(vo);
				//缓存用户与车辆关系 
				addUserVehicle(vo.getVehicleId());
			}
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}

		return vo;
	}

	@Override
	public VehicleVo addVehicle(VehicleVo v) {
		if (v.getVehicleId() == null) {
			LOGGER.error("增加车辆信息到缓存中时车辆id不能为空！");
		}
		String str = convert2Pb(v);
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			shardedJedis.set(VEHICLE_SIM_NO_CACHE_PREFIX + v.getSimNo(), str);
			shardedJedis.set(VEHICLE_VID_CACHE_PREFIX + v.getVehicleId(), v.getSimNo());
			shardedJedis.set(VEHICLE_PLATE_NO_CACHE_PREFIX + v.getPlateNo(), v.getSimNo());
			if(v.getUserId() > 0){
			    //缓存用户与车辆关系
			    shardedJedis.sadd(USER_VEHICLE_CACHE_PREFIX + v.getUserId(), String.valueOf(v.getVehicleId()));
			}
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}

		return v;
	}

	@Override
	public VehicleVo findVehicleByPlate(String licensePlate) {
		VehicleVo vehicle = null;
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = VEHICLE_PLATE_NO_CACHE_PREFIX + licensePlate;
			if (shardedJedis.exists(key)) {
				String sim = shardedJedis.get(key);
				key = VEHICLE_SIM_NO_CACHE_PREFIX + sim;
				vehicle = convert2Veh(shardedJedis.get(key));
			} else {
			    //TODO:
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
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}

		return ids;
	}
	
	/**
	 * 将车辆实体转负成车辆PB实体
	 * @param v
	 * @return
	 */
	private String convert2Pb(VehicleVo v){
	    VehiclePb.Vehicle.Builder vehiclePb = VehiclePb.Vehicle.newBuilder();
	    
	    return new String(Base64.encode(vehiclePb.build().toByteArray()));
	}
	
	/**
	 * 将车辆PB实体解析成车辆实体
	 * @param vehicleVo
	 * @return
	 */
	private VehicleVo convert2Veh(String str){
	    try {
            VehiclePb.Vehicle vehicle = VehiclePb.Vehicle.parseFrom(Base64.decode(str.getBytes()));
        } catch (InvalidProtocolBufferException e) {
            return null;
        }
	    VehicleVo v = new VehicleVo();
	    
	    return v;
	}
	
	private void addUserVehicle(int vehicle){
	    //根据车辆ID查询所属用户集合
	    UserVehicleExample example = new UserVehicleExample();
	    example.or().andVehicleIdEqualTo(vehicle);
	    List<UserVehicle> list = userVehicleMapper.selectByExample(example);
	    if(list.isEmpty()){
	        return;
	    }
        for(UserVehicle uv : list){
            // 缓存用户与车辆关系
            addUserVehicle(uv.getUserId(), uv.getVehicleId());
        }
	}
	
	/**
	 * 缓存用户与车辆关系 
	 * @param userId
	 * @param vehicleId
	 */
	private void addUserVehicle(int userId,int vehicleId) {
	    ShardedJedis shardedJedis = null;
        try {
            shardedJedis = ShardedJedisPoolFactory.getResource();
            // 缓存用户与车辆关系
            shardedJedis.sadd(USER_VEHICLE_CACHE_PREFIX +userId, String.valueOf(vehicleId));
        } catch (Exception e) {
            ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
        } finally {
            ShardedJedisPoolFactory.returnResource(shardedJedis);
        }
	}

    /* (non-Javadoc)
     * @see cn.com.gps169.common.cache.IVehicleCacheManager#findVehicleBySim(java.lang.String)
     */
    @Override
    public VehicleVo findVehicleBySim(String simnNo) {
        // TODO Auto-generated method stub
        return null;
    }
	
}
