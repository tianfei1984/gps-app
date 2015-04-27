package cn.com.gps169.common.cache.impl;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.gps169.common.cache.ITerminalCacheManager;
import cn.com.gps169.common.tool.ShardedJedisPoolFactory;
import cn.com.gps169.db.dao.TerminalMapper;
import cn.com.gps169.db.model.Terminal;
import cn.com.gps169.db.model.TerminalExample;
import redis.clients.jedis.ShardedJedis;

@Component
public class TerminalRedisImpl implements ITerminalCacheManager {

	private static transient final Logger LOGGER = LoggerFactory.getLogger(TerminalRedisImpl.class);

	@Autowired
	private TerminalMapper terminalMapper;

	private static final String TERMINAL_IMEI_TERMINAL_CACHE_PREFIX = "jt808:buzi:terminal:imei:terminal:";
	private static final String TERMINAL_SIMNO_TERMINAL_CACHE_PREFIX = "jt808:buzi:terminal:simNo:terminal:";
	private static final String TERMINAL_TID_IMEI_CACHE_PREFIX = "jt808:buzi:terminal:tid:imei:";
	private static final String TERMINAL_CACHE_STATUS = "jt808:buzi:terminal:cache:status";

	/**
	 * 初始化终端数据进缓存
	 */
	public void initCache() {
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String status = shardedJedis.get(TERMINAL_CACHE_STATUS);
//			if (StringUtils.isBlank(status) || status.equals("0")) {
				// 查询终端信息
				TerminalExample example = new TerminalExample();
				example.or().andWorkingStatusEqualTo(1);	//设备工作状态
				List<Terminal> terminals = terminalMapper.selectByExample(example);
				for (Terminal t : terminals) {
					JSONObject jsonObj = JSONObject.fromObject(t);
					shardedJedis.set(TERMINAL_IMEI_TERMINAL_CACHE_PREFIX + t.getImei(),jsonObj.toString());
					shardedJedis.set(TERMINAL_SIMNO_TERMINAL_CACHE_PREFIX + t.getImsi(),jsonObj.toString());
					shardedJedis.set(TERMINAL_TID_IMEI_CACHE_PREFIX + t.getTerminalId(), t.getImei());
				}
				shardedJedis.set(TERMINAL_CACHE_STATUS, "1");
				LOGGER.info("Terminal缓存初始化执行完成,共初始化" + terminals.size()
						+ "个终端信息到缓存中!");
//			}
		} catch (Exception e) {
			LOGGER.error("Terminal缓存初始化错误", e);
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
			shardedJedis = null;
		} finally {
			if (shardedJedis != null) {
				ShardedJedisPoolFactory.returnResource(shardedJedis);
			}
		}
	}

	/**
	 * 根据SIM卡号查询终端缓存信息
	 * 
	 * @param simNo
	 * @return
	 */
	@Override
	public Terminal getTerminalBySimNo(String simNo) {
		Terminal terminal = null;
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			String key = TERMINAL_SIMNO_TERMINAL_CACHE_PREFIX + simNo;
			if (shardedJedis.exists(key)) {
				String value = shardedJedis.get(key);
				JSONObject obj = JSONObject.fromObject(value);
				terminal = (Terminal) JSONObject.toBean(obj, Terminal.class);
			} else {
				TerminalExample baseExample = new TerminalExample();
				baseExample.or().andImsiEqualTo(simNo);
				List<Terminal> terminals = terminalMapper
						.selectByExample(baseExample);
				if (terminals == null || terminals.size() == 0) {
					return null;
				}

				LOGGER.warn("终端缓存与数据库数据出现不一致，原因可能是业务系统未经过缓存接口直接操作数据库或是缓存设计有漏洞。"
						+ "simNo=" + simNo);

				addOrUpdateTerminal(terminals.get(0));
				terminal = terminals.get(0);
			}
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}
		return terminal;
	}

	@Override
	public Terminal addOrUpdateTerminal(Terminal t) {
		if (t == null || StringUtils.isEmpty(t.getImei())) {
			return null;
		}
		if (t.getTerminalId() == null) {
			// 保存终端信息
			TerminalExample baseExample = new TerminalExample();
			baseExample.createCriteria().andImeiEqualTo(t.getImei());
			List<Terminal> tList = terminalMapper.selectByExample(baseExample);
			if (tList == null || tList.size() == 0) {
				terminalMapper.insertSelective(t);
			} else {
				//终端已经存在
				return null;
			}
		} else {
			terminalMapper.updateByPrimaryKeySelective(t);
		}
		
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = ShardedJedisPoolFactory.getResource();
			JSONObject jsonObj = JSONObject.fromObject(t);
			shardedJedis.set(TERMINAL_IMEI_TERMINAL_CACHE_PREFIX + t.getImei(), jsonObj.toString());
			shardedJedis.set( TERMINAL_SIMNO_TERMINAL_CACHE_PREFIX + t.getImsi(), jsonObj.toString());
			shardedJedis.set(TERMINAL_TID_IMEI_CACHE_PREFIX + t.getTerminalId(), t.getImei());
		} catch (Exception e) {
			ShardedJedisPoolFactory.returnBrokenResource(shardedJedis);
		} finally {
			ShardedJedisPoolFactory.returnResource(shardedJedis);
		}

		return t;
	}

}
