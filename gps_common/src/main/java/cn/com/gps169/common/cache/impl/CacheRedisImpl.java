package cn.com.gps169.common.cache.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sun.jersey.core.util.Base64;

import cn.com.gps169.common.cache.ICacheManager;
import cn.com.gps169.common.model.GpsInfo;
import cn.com.gps169.common.model.VehicleVo;
import cn.com.gps169.common.pb.VehiclePb;
import cn.com.gps169.common.tool.DateUtil;
import cn.com.gps169.common.tool.ShardedJedisPoolFactory;
import cn.com.gps169.db.dao.UserVehicleMapper;
import cn.com.gps169.db.dao.VehicleMapper;
import cn.com.gps169.db.model.UserVehicle;
import cn.com.gps169.db.model.UserVehicleExample;
import cn.com.gps169.db.model.Vehicle;
import cn.com.gps169.db.model.VehicleExample;
import redis.clients.jedis.ShardedJedis;

/**
 * redis缓存实现类
 * @author tianfei
 *
 */
@Component
public class CacheRedisImpl implements ICacheManager {
	private static transient final Logger LOGGER = LoggerFactory.getLogger(CacheRedisImpl.class);
	//车辆缓存KEY值；KEY:SIM; VALUE:车辆信息（PB）
	private static final String VEHICLE_SIM_NO_CACHE_PREFIX = "com:gps808:app:vehicle:sim:";
	//车辆ID与SIM对应关系； KEY:VID; VALUE:SIM
	private static final String VEHICLE_VID_CACHE_PREFIX = "com:gps808:app:vehicle:vid:";
	//车牌号与SIM对应关系； KEY:PLATENO; VALUE:SIM
	private static final String VEHICLE_PLATE_NO_CACHE_PREFIX = "com:gps808:app:vehicle:plateno:";
	//用户与车辆关系 ； KEY:USERID; VALUE:VEHICLE(set)
	private static final String USER_VEHICLE_CACHE_PREFIX = "com:gps808:app:user:vehicle:userId:";
	//车辆GPS信息，KEY:SIM；Value:gpsinfo
	private static final String VEHICLE_GPS_CACHE_PREFIX = "com:gps808:app:gps:sim:";
	//缓存标识符
	private static final String CACHE_FLAG = "com:gps808:app:cache:flag";
	//车辆轨迹
	private static final String VEHICLE_TRACK_CACHE_PREFIX = "com:gps808:app:track:";

	@Autowired
	private VehicleMapper vehicleMapper;
	
	@Autowired
	private UserVehicleMapper userVehicleMapper;
	
	@Override
	public void initCache() {
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			//判断是否初始化缓存
			if(shardedJedis.exists(CACHE_FLAG) && "true".equals(shardedJedis.get(CACHE_FLAG))){
				return;
			}
			// 缓存用户与车辆关系
			UserVehicleExample uExample = new UserVehicleExample();
			List<UserVehicle> uvs = userVehicleMapper.selectByExample(uExample);
			for(UserVehicle uv : uvs){
			    addUserVehicle(uv.getUserId(), uv.getVehicleId());
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
			//设置缓存初始化标识位
			shardedJedis.set(CACHE_FLAG, "true");
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
		ShardedJedis shardedJedis = null;
		VehicleVo vehicleVo = new VehicleVo(); 
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = VEHICLE_VID_CACHE_PREFIX + vehicleId;
			if (shardedJedis.exists(key)) {
				String value = shardedJedis.get(key);
				key = VEHICLE_SIM_NO_CACHE_PREFIX + value;
				vehicleVo = decodeVehicle(shardedJedis.get(key));
			} else {
				Vehicle vehicle = null;
				VehicleExample example = new VehicleExample();
				example.or().andVehicleIdEqualTo(vehicleId);
				List<Vehicle> list = vehicleMapper.selectByExample(example);
				if (list == null || list.isEmpty()) {
					return null;
				} else {
					vehicle = list.get(0);
				}
				//缓存车辆信息
				BeanUtils.copyProperties(vehicleVo, vehicle);
				addVehicle(vehicleVo);
				//缓存用户与车辆关系 
				addUserVehicle(vehicleVo.getVehicleId());
			}
		} catch (Exception e) {
		    LOGGER.error("查询车辆缓存异常，异常信息："+e.getMessage());
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}

		return vehicleVo;
	}

	@Override
	public VehicleVo addVehicle(VehicleVo v) {
		if (v.getVehicleId() == null) {
			LOGGER.error("增加车辆信息到缓存中时车辆id不能为空！");
		}
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			shardedJedis.set(VEHICLE_SIM_NO_CACHE_PREFIX + v.getSimNo(), encodeVehicle(v));
			shardedJedis.set(VEHICLE_VID_CACHE_PREFIX + v.getVehicleId(), v.getSimNo());
			shardedJedis.set(VEHICLE_PLATE_NO_CACHE_PREFIX + v.getPlateNo(), v.getSimNo());
			if(v.getUserId() > 0){
			    //缓存用户与车辆关系
			    shardedJedis.sadd(USER_VEHICLE_CACHE_PREFIX + v.getUserId(), String.valueOf(v.getVehicleId()));
			}
		} catch (Exception e) {
		    LOGGER.error("增加车辆缓存异常，异常信息："+e.getMessage());
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}

		return v;
	}

	@Override
	public VehicleVo findVehicleByPlate(String licensePlate) {
		VehicleVo vehicleVo = new VehicleVo();
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = VEHICLE_PLATE_NO_CACHE_PREFIX + licensePlate;
			if (shardedJedis.exists(key)) {
				String sim = shardedJedis.get(key);
				key = VEHICLE_SIM_NO_CACHE_PREFIX + sim;
				vehicleVo = decodeVehicle(shardedJedis.get(key));
			} else {
				Vehicle vehicle = null;
				VehicleExample example = new VehicleExample();
				example.or().andPlateNoEqualTo(licensePlate);
				List<Vehicle> list = vehicleMapper.selectByExample(example);
				if (list == null || list.isEmpty()) {
					return null;
				} else {
					vehicle = list.get(0);
				}
				//缓存车辆信息
				BeanUtils.copyProperties(vehicleVo, vehicle);
				addVehicle(vehicleVo);
				//缓存用户与车辆关系 
				addUserVehicle(vehicleVo.getVehicleId());
			}
		} catch (Exception e) {
		    LOGGER.error("查询车辆缓存异常，异常信息："+e.getMessage());
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}

		return vehicleVo;
	}

	/**
	 * 将车辆实体转负成车辆PB实体
	 * @param v
	 * @return
	 */
	private String encodeVehicle(VehicleVo v){
	    VehiclePb.Vehicle.Builder vehiclePb = VehiclePb.Vehicle.newBuilder();
	    vehiclePb.setVehicleId(v.getVehicleId());
	    vehiclePb.setPlateNo(v.getPlateNo());
	    vehiclePb.setVin(v.getVin());
	    vehiclePb.setEin(v.getEin());
	    vehiclePb.setSimNo(v.getSimNo());
	    vehiclePb.setTerminalNo(v.getTerminalNo());
	    vehiclePb.setTerminalFlag(v.getTerminalFlag());
	    vehiclePb.setTerminalStatus(v.getTerminalStatus());
	    vehiclePb.setServiceEndTime(DateUtil.TIMEFORMATER1().format(v.getServiceEndTime()));
	    vehiclePb.setServiceStartTime(DateUtil.TIMEFORMATER1().format(v.getServiceStartTime()));
	    vehiclePb.setVehicleStatus(v.getVehicleStatus());
	    vehiclePb.setCreatedTime(DateUtil.TIMEFORMATER1().format(v.getCreatedTime()));
	    vehiclePb.setMovingStatus(v.getMovingStatus());
	    vehiclePb.setFleeStatus(v.getMovingStatus());
	    
	    return new String(Base64.encode(vehiclePb.build().toByteArray()));
	}
	
	/**
	 * 将车辆PB实体解析成车辆实体
	 * @param vehicleVo
	 * @return
	 */
	private VehicleVo decodeVehicle(String str){
		VehicleVo vehicleVo = new VehicleVo();
	    try {
            VehiclePb.Vehicle vehiclePb = VehiclePb.Vehicle.parseFrom(Base64.decode(str.getBytes()));
            vehicleVo.setVehicleId(vehiclePb.getVehicleId());
            vehicleVo.setPlateNo(vehiclePb.getPlateNo());
            vehicleVo.setVin(vehiclePb.getVin());
            vehicleVo.setEin(vehiclePb.getEin());
            vehicleVo.setSimNo(vehiclePb.getSimNo());
            vehicleVo.setTerminalNo(vehiclePb.getTerminalNo());
            vehicleVo.setTerminalFlag((byte)vehiclePb.getTerminalFlag());
            vehicleVo.setTerminalStatus((byte)vehiclePb.getTerminalStatus());
            vehicleVo.setServiceEndTime(DateUtil.TIMEFORMATER1().parse(vehiclePb.getServiceEndTime()));
            vehicleVo.setServiceStartTime(DateUtil.TIMEFORMATER1().parse(vehiclePb.getServiceStartTime()));
            vehicleVo.setVehicleStatus((byte)vehiclePb.getVehicleStatus());
            vehicleVo.setCreatedTime(DateUtil.TIMEFORMATER1().parse(vehiclePb.getCreatedTime()));
            vehicleVo.setMovingStatus((byte) vehiclePb.getMovingStatus());
        } catch (Exception e) {
        	LOGGER.error("解析车辆信息失败，错误信息："+e.getMessage());
        	return null;
		}
	    
	    return vehicleVo;
	}
	
	/**
	 * 编码GPS信息
	 * @param gps
	 * @return
	 */
	private String encodeGps(GpsInfo gps){
	    //位置信息
        VehiclePb.Gps.Builder location = VehiclePb.Gps.newBuilder();
        location.setLongitude(gps.getLongitude());
        location.setLatitude(gps.getLatitude());
        location.setLocation(gps.getLocation());
        location.setAltitude(gps.getAltitude());
        location.setSpeed(gps.getSpeed());
        location.setSendTime(gps.getSendTime());
        
	    return new String(Base64.encode(location.build().toByteArray()));
	}
	
	/**
	 * 解析GPS信息
	 * @param str
	 * @return
	 */
	private GpsInfo decodeGps(String str) {
        try {
            VehiclePb.Gps gpsPb = VehiclePb.Gps.parseFrom(Base64.decode(str.getBytes()));
            GpsInfo gpsInfo = new GpsInfo();
            gpsInfo.setLongitude(gpsPb.getLongitude());
            gpsInfo.setLatitude(gpsPb.getLatitude());
            gpsInfo.setLocation(gpsPb.getLocation());
            gpsInfo.setAltitude(gpsPb.getAltitude());
            gpsInfo.setSpeed(gpsPb.getSpeed());
            gpsInfo.setSendTime(gpsPb.getSendTime());
            return gpsInfo;
        } catch (InvalidProtocolBufferException e) {
            LOGGER.error("解析车辆位置信息异常，异常信息:"+e.getMessage());
        }
	    
	    return null;
	}
	
	/**
	 * 根据车辆ID更新用户与车辆关系缓存
	 * @param vehicle
	 */
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
            LOGGER.error("增加用户、车辆缓存异常，异常信息："+e.getMessage());
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
    	ShardedJedis shardedJedis = null;
		VehicleVo vehicleVo = new VehicleVo(); 
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = VEHICLE_SIM_NO_CACHE_PREFIX + simnNo;
			if (shardedJedis.exists(key)) {
				return decodeVehicle(shardedJedis.get(key));
			} else {
				Vehicle vehicle = null;
				VehicleExample example = new VehicleExample();
				example.or().andSimNoEqualTo(simnNo);
				List<Vehicle> list = vehicleMapper.selectByExample(example);
				if (list == null || list.isEmpty()) {
					return null;
				} else {
					vehicle = list.get(0);
				}
				//缓存车辆信息
				BeanUtils.copyProperties(vehicleVo, vehicle);
				addVehicle(vehicleVo);
				//缓存用户与车辆关系 
				addUserVehicle(vehicleVo.getVehicleId());
			}
		} catch (Exception e) {
		    LOGGER.error("查询车辆缓存异常，异常信息："+e.getMessage());
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}

		return vehicleVo;
    }

	@Override
	public List<VehicleVo> findVehiclesByUserId(int userId) {
		List<VehicleVo> list = new ArrayList<VehicleVo>();
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = USER_VEHICLE_CACHE_PREFIX + userId;
			Set<String> set = shardedJedis.smembers(key);
			String sim;
			for(String vehicleId : set){
				sim = shardedJedis.get(VEHICLE_VID_CACHE_PREFIX + vehicleId);
				list.add(decodeVehicle(shardedJedis.get(VEHICLE_SIM_NO_CACHE_PREFIX + sim)));
			}
		} catch (Exception e) {
		    LOGGER.error("增加用户车辆缓存异常，异常信息："+e.getMessage());
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}
		return list;
	}

	@Override
	public VehicleVo updateVehicle(VehicleVo vehicleVo) {
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = VEHICLE_SIM_NO_CACHE_PREFIX + vehicleVo.getSimNo();
			VehicleVo rVehicleVo = decodeVehicle(shardedJedis.get(key));
			BeanUtils.copyProperties(rVehicleVo, vehicleVo);
			//更新
			shardedJedis.set(key, encodeVehicle(rVehicleVo));
		} catch (Exception e) {
		    LOGGER.error("更新车辆缓存异常，异常信息："+e.getMessage());
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}
		
		return vehicleVo;
	}

    /* (non-Javadoc)
     * @see cn.com.gps169.common.cache.ICacheManager#findGpsInfoBySim(java.lang.String)
     */
    @Override
    public GpsInfo findGpsInfoBySim(String simNo) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = ShardedJedisPoolFactory.getResource();
            String key = VEHICLE_GPS_CACHE_PREFIX + simNo;
            if(shardedJedis.exists(key)){
                return decodeGps(shardedJedis.get(key));
            }
        } catch (Exception e) {
            LOGGER.error("增加车辆位置缓存异常，异常信息："+e.getMessage());
            ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
        } finally {
            ShardedJedisPoolFactory.returnResource(shardedJedis);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see cn.com.gps169.common.cache.ICacheManager#addGpsInfo(cn.com.gps169.common.model.GpsInfo)
     */
    @Override
    public GpsInfo addGpsInfo(GpsInfo gpsInfo) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = ShardedJedisPoolFactory.getResource();
            String key = VEHICLE_GPS_CACHE_PREFIX + gpsInfo.getSimNo();
            shardedJedis.set(key, encodeGps(gpsInfo));
        } catch (Exception e) {
            LOGGER.error("增加车辆位置缓存异常，异常信息："+e.getMessage());
            ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
        } finally {
            ShardedJedisPoolFactory.returnResource(shardedJedis);
        }
        return gpsInfo;
    }

	@Override
	public void addVehicleTrack(GpsInfo gpsInfo) {
		ShardedJedis shardedJedis = null;
		try{
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = VEHICLE_TRACK_CACHE_PREFIX + gpsInfo.getSimNo() +":" + DateUtil.DATEFORMATER().parse(gpsInfo.getSendTime()).getTime();
			shardedJedis.append(key, encodeGps(gpsInfo));
		} catch (Exception e) {
            LOGGER.error("增加车辆轨迹点缓存异常，异常信息："+e.getMessage());
            ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
        } finally {
            ShardedJedisPoolFactory.returnResource(shardedJedis);
        }
		
	}

	@Override
	public List<GpsInfo> findVehicleTrip(int simNo, long recvDay) {
		List<GpsInfo> list = new LinkedList<GpsInfo>();
		ShardedJedis shardedJedis = null;
		try{
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = VEHICLE_TRACK_CACHE_PREFIX + simNo +":" + recvDay;
			Set<String> set = shardedJedis.smembers(key);
			for(String str : set){
				list.add(decodeGps(str));
			}
		}  catch (Exception e) {
            LOGGER.error("查询车辆轨迹点缓存异常，异常信息："+e.getMessage());
            ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
        } finally {
            ShardedJedisPoolFactory.returnResource(shardedJedis);
        }
		
		return list;
	}

	@Override
	public void deleteVehicleTrip(int simNo, long recvDay) {
		ShardedJedis shardedJedis = null;
		try{
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = VEHICLE_TRACK_CACHE_PREFIX + simNo +":" + recvDay;
			shardedJedis.del(key);
		} catch (Exception e) {
            LOGGER.error("删除车辆轨迹点缓存异常，异常信息："+e.getMessage());
            ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
        } finally {
            ShardedJedisPoolFactory.returnResource(shardedJedis);
        }
	}
	
}
