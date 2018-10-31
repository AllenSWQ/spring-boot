package com.allen.redis;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class RedisClient {
	public static JedisPool jedisPool;
	private static Log log = LogFactory.getLog(RedisClient.class);
	
	/**
	 * redis IP
	 */
	private static String redisIp = "";
	
	/**
	 * 最大链接数
	 */
	private static int maxActive = 3000;

	/**
	 * 最大空闲连结数
	 */
	private static int maxIdle = 20;

	/**
	 * 超时时间
	 */
	private static int maxWait = 3000;

	/**
	 * 端口
	 */
	private static int port = 6380;
	
	/**
	 * 密码
	 */
	private static String password = "";
	
	/**
	 * 初始化参数
	 */
	static {
		Properties props = new Properties();
		try{
			InputStream is = new BufferedInputStream(new FileInputStream("/data01/apps/config/face/redis.properties"));
			props.load(is);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			log.error("FileNotFoundException:" + e);
		} catch (Exception e) {
			// TODO: handle exception
			log.error(e);
		}
		redisIp = props.getProperty("redisIp");
		maxActive = Integer.parseInt(props.getProperty("maxActive"));
		maxIdle = Integer.parseInt(props.getProperty("maxIdle"));
		maxWait = Integer.parseInt(props.getProperty("maxWait"));
		port = Integer.parseInt(props.getProperty("port"));
		password = props.getProperty("password");
	}

	private static RedisClient redisInstance = null;

	public synchronized static RedisClient getInstance() {
		if (null == redisInstance) {
			redisInstance = new RedisClient(redisIp, maxActive,
					maxIdle, maxWait, port, password);
		}
		return redisInstance;
	}

	/**
	 * 初始Redis线程池
	 * 
	 * @param ip
	 * @param maxActive
	 * @param maxIdle
	 * @param maxWait
	 * @param port
	 */
	public RedisClient(String ip, int maxActive, int maxIdle, int maxWait,
			int port, String password) {
		JedisPoolConfig config = new JedisPoolConfig();
		//控制一个pool可分配多少个jedis实例,-1代表无限制
		config.setMaxTotal(maxActive);
		//控制一个pool最多有多少个状态为idle的jedis实例
		config.setMaxIdle(maxIdle);
		//borrow一个jedis实例时，最大的等待时间
		config.setMaxWaitMillis(maxWait);
		//在获取连接的时候检查有效性, 默认false
        config.setTestOnBorrow(true);
        //在归还连接的时候检查有效性，默认false
        config.setTestOnReturn(true);
		jedisPool = new JedisPool(config, ip, port, maxWait, password);
	}

	public static boolean lpush(String key,String strings) 
    		throws Exception {
        Jedis jedis = null;
        try {
            //jedis = jedisPool.getResource();
            jedis = getJedis();
        	jedis.lpush(key,strings);
            return true;
        } catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			jedisPool.returnResource(jedis);
		}
    }
	/**
	 * 向缓存中设置字符串内容
	 * 
	 * @param key
	 *            key
	 * @param value
	 *            value
	 * @param seconds
	 * @return
	 * @throws Exception
	 */
	public static boolean setex(String key, int seconds, String value)
			throws Exception {
		Jedis jedis = null;
		try {
			//jedis = jedisPool.getResource();
            jedis = getJedis();
			if (seconds == 0) {
				jedis.set(key, value);
			} else {
				jedis.setex(key, seconds, value);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 根据key 获取内容
	 * 
	 * @param key
	 * @return
	 */
	public static Object get(String key) {
		Jedis jedis = null;
		log.info("key:" + key);
		try {
			//jedis = jedisPool.getResource();
            jedis = getJedis();
			Object value = jedis.get(key);
			return value;
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 判断该key是否存在
	 * 
	 * @param key
	 * @return
	 */
	public static boolean exists(String key) {
		Jedis jedis = null;
		try {
			//jedis = jedisPool.getResource();
            jedis = getJedis();
			return jedis.exists(key);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 向缓存中设置对象
	 * 
	 * @param key
	 * @param seconds
	 * @param value
	 * @return
	 */
	public static boolean setClass(String key, int seconds, Object value) {
		Jedis jedis = null;
		try {
			String objectJson = JSON.toJSONString(value);
			//jedis = jedisPool.getResource();
            jedis = getJedis();
			if (seconds == 0) {
				jedis.set(key, objectJson);
			} else {
				jedis.setex(key, seconds, objectJson);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 指定对象的数值+1
	 * 
	 * @param key
	 * @return
	 */
	public static long incr(String key, int add) {
		Jedis jedis = null;
		try {
			//jedis = jedisPool.getResource();
            jedis = getJedis();
			return jedis.incr(key);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 指定对象的数值-1
	 * 
	 * @param key
	 * @return
	 */
	public static long decr(String key, int add) {
		Jedis jedis = null;
		try {
			//jedis = jedisPool.getResource();
            jedis = getJedis();
			return jedis.decr(key);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 删除缓存中得对象，根据key
	 * 
	 * @param key
	 * @return
	 */
	public static boolean del(String key) {
		Jedis jedis = null;
		try {
			//jedis = jedisPool.getResource();
            jedis = getJedis();
			jedis.del(key);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 根据key 获取对象
	 * 
	 * @param key
	 * @return
	 */
	public static <T> T getClass(String key, Class<T> clazz) {
		Jedis jedis = null;
		try {
			//jedis = jedisPool.getResource();
            jedis = getJedis();
			String value = jedis.get(key);
			return JSON.parseObject(value, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 向缓存中设置集合
	 * 
	 * @param key
	 * @param seconds
	 * @param list
	 * @return
	 */
	public static <T> boolean setList(String key, int seconds, List<T> list) {
		Jedis jedis = null;
		try {
			String objectJson = JSON.toJSONString(list);
			//jedis = jedisPool.getResource();
            jedis = getJedis();
			jedis.setex(key, seconds, objectJson);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 根据key 获取List对象
	 * 
	 * @param key
	 * @param t
	 * @return
	 */
	public static <T> T getList(String key, TypeReference<T> t) {
		Jedis jedis = null;
		try {
			//jedis = jedisPool.getResource();
            jedis = getJedis();
			String value = jedis.get(key);
			return JSON.parseObject(value, t);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 获取key总数
	 * 
	 * @return
	 */
	public static long getDBSize() {
		Jedis jedis = null;
		try {
			//jedis = jedisPool.getResource();
            jedis = getJedis();
			return jedis.dbSize();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 查询redis的info信息
	 * 
	 * @return
	 */
	public static String getInfo() {
		Jedis jedis = null;
		try {
			//jedis = jedisPool.getResource();
            jedis = getJedis();
			return jedis.info();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 获取jedis实例
	 * 若网络超时则尝试3次
	 * @return
	 */
	public synchronized static Jedis getJedis(){
		int timeoutCount = 0;
		Jedis jedis = null;
		while (true)
		{
			try {
				jedis = jedisPool.getResource();
				return jedis;
			} catch (Exception e) {
				if (e instanceof JedisConnectionException
						|| e instanceof SocketTimeoutException) {
					timeoutCount++;
					log.debug("getJedis timeoutCount = " + timeoutCount);
					if (timeoutCount > 3) {
						log.error("getJedis timeoutCount over 3 times");
						break;
					}
				} else {
					log.error("getJedis error:" + e);
					break;
				}
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
	}

}
