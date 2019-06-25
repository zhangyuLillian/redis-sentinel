package com.lillian.redissentinel.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * 
 * redis缓存服务
 *
 */
@Component
@Slf4j
public class RedisHelper {
	
	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	
	private static String redisCode = "utf-8";

	/**
	* 通过key删除
	* @param keys
	*/
	public long del(final String... keys) {
		log.info("function del()'s params is:{}", keys);
		return (long) redisTemplate.execute(new RedisCallback() {
			
			@Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
				long result = 0;
				for (int i = 0; i < keys.length; i++) {
					result = connection.del(keys[i].getBytes());
					log.info("redis's function del() result:{}", result);
				}
				return result;
			}
		});
	}

	/**
	 * 添加key value 并且设置存活时间(byte)
	 * @param key
	 * @param value
	 * @param liveTime
	 */
	public void set(final byte[] key, final byte[] value, final long liveTime) {
		redisTemplate.execute(new RedisCallback() {
			
			@Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
				connection.set(key, value);
				if (liveTime > 0) {
					connection.expire(key, liveTime);
				}
				return 1L;
			}
		});
	}

	/**
	 * 添加key value 并且设置存活时间
	 * @param key
	 * @param value
	 * @param liveTime
	 *            单位秒
	 */
	public void set(String key, String value, long liveTime) {
		this.set(key.getBytes(), value.getBytes(), liveTime);
	}

	/**
	 * 添加key value
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		this.set(key, value, 0L);
	}

	/**
	 * 添加key value (字节)(序列化)
	 * @param key
	 * @param value
	 */
	public void set(byte[] key, byte[] value) {
		this.set(key, value, 0L);
	}

	/**
	 * 排序集合添加
	 * @param key
	 * @param obj
	 * @param score
	 */
	public boolean zadd(final String key, final Object obj, final double score) {
		log.info("function zadd()' params is key:{},obj:{}, score:{}", key, obj, score);
		boolean issuccess = redisTemplate.opsForZSet().add(key, obj, score);
		log.info("有序集合添加结果:[{}]", issuccess);
		return issuccess;
//		
//		return redisTemplate.execute(new RedisCallback<Boolean>() {
//			
//			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
//				try {
//					String val = (String)obj;
//					return connection.zAdd(key.getBytes(),score,val.getBytes() );
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				return false;
//			}
//		});
	}
	
	public long remove(final String key, final Object obj) {
		log.info("function zadd()' params is key:{},obj:{}, score:{}", key, obj);
        long issuccess = redisTemplate.opsForZSet().remove(key, obj);
        log.info("有序集合添加结果:[{}]", issuccess);
        return issuccess;
    }
	 

	/**
	 * @param key
	 * @param obj
	 * @param score
	 * @return
	 */
	    public boolean zaddByte(final String key, final Object obj, final double score){
	     
	        return redisTemplate.execute(new RedisCallback<Boolean>() {
	              
	              @Override
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
	                  try {
	                      String val = Base64.encodeBase64String(JSONObject.toJSONString(obj).getBytes());
	                      return connection.zAdd(key.getBytes(),score,val.getBytes() );
	    
	                  } catch (Exception e) {
	                	  log.error("e[{}]",e);
	                  }
	                return false;
	              }
	          });
	    }
	
	    public Long removeByte(final String key, final Object obj){
	         
            return redisTemplate.execute(new RedisCallback<Long>() {
                  
                  @Override
                public Long doInRedis(RedisConnection connection) throws DataAccessException {
                      try {
                          String val = Base64.encodeBase64String(JSONObject.toJSONString(obj).getBytes());
                          return connection.zRem(key.getBytes(),val.getBytes() );
        
                      } catch (Exception e) {
                    	  log.error("e[{}]",e);
                      }
                    return Long.valueOf(0);
                  }
              });
        }
	/**
	 * score 获取部分
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Set<Object> zrangebyscore(String key, double min, double max) {
		return redisTemplate.opsForZSet().rangeByScore(key, min, max);
	}

	
	public Set<Object> zrange(String key, int start, int end) {
		return redisTemplate.opsForZSet().range(key, start, end);
	}

	/**
	 * 获取redis value (String)
	 * @param key
	 * @return
	 */
	public String get(final String key) {
		return redisTemplate.execute(new RedisCallback<String>() {
			
			@Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
				try {
					byte[] bytes = connection.get(key.getBytes());
					if (bytes == null) {
						return null;
					} else {
						return new String(connection.get(key.getBytes()), redisCode);
					}

				} catch (UnsupportedEncodingException e) {
					log.info("hash-redis's function rpop() has exception:{}", e.getMessage());
				}
				return "";
			}
		});
	}


	/**
	 * 检查key是否已经存在
	 * @param key
	 * @return
	 */
	public boolean exists(final String key) {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			
			@Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.exists(key.getBytes());
			}
		});
	}

	/**
	 * 清空redis 所有数据
	 * @return
	 */
	public String flushDB() {
		return redisTemplate.execute(new RedisCallback<String>() {
			
			@Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
				connection.flushDb();
				return "ok";
			}
		});
	}

	/**
	 * 查看redis里有多少数据
	 */
	public long dbSize() {
		return redisTemplate.execute(new RedisCallback<Long>() {
			
			@Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.dbSize();
			}
		});
	}

	/**
	 * 检查是否连接成功
	 * 
	 * @return
	 */
	public String ping() {
		return redisTemplate.execute(new RedisCallback<String>() {
			
			@Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.ping();
			}
		});
	}



	
	
	
	
	
	/**
	 * hash数据类型field值获取
	 * @param key
	 * @param field
	 * @return
	 */
	public String hget(final String key,final String field) {
		return redisTemplate.execute(new RedisCallback<String>() {
			
			@Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
				try {
					byte[] val =  connection.hGet(key.getBytes(), field.getBytes());
					if(val == null) {
						return null;
					}
					return new String(val, redisCode);
				} catch (UnsupportedEncodingException e) {
					log.info("hash-redis's function rpop() has exception:{}", e.getMessage());
				}
				return null;
			}
		});
	}
	
	/**
	 * hash数据类型键中所有field值获取
	 * @param key
	 * @return
	 */
	public Map<String,String> hget(final String key) {
		return redisTemplate.execute(new RedisCallback<Map<String,String>>() {
			
			@Override
            public Map<String,String> doInRedis(RedisConnection connection) throws DataAccessException {
				try {
					Map<byte[],byte[]> valMap=  connection.hGetAll(key.getBytes());
					if(valMap == null) {
						return null;
					}
					String result = "";
					Map<String,String> resultMap = new HashMap<String,String>();
					for(byte[] key : valMap.keySet()){
						result = new String(valMap.get(key),redisCode);
						resultMap.put(new String(key,redisCode), result);
					}
					return resultMap;
				} catch (UnsupportedEncodingException e) {
					log.info("hash-redis's function rpop() has exception:{}", e.getMessage());
				}
				return null;
			}
		});
	}
	/**
	 * hash数据类型field值添加
	 * @param key
	 * @param field
	 * @param val
	 * @return
	 */
	public Boolean hset(final String key, final String field,final String val) {
		log.info("hset()'s function param:key:{},field:{},val:{}", key, field, val);
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			
			@Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				boolean result = connection.hSet(key.getBytes(), field.getBytes(),val.getBytes());
				log.info("hash-redis's function hset() result:{}", result);
				return result;
			}
		});
	}
	
	/**
	 * 删除对应的对象
	 * @param key
	 * @param field
	 */
	public void hdel(final String key, final String field) {
		log.info("hdel()'s function param:key:{},field:{}", key, field);
		redisTemplate.execute(new RedisCallback<Long>() {
			
			@Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
				Long result = connection.hDel(key.getBytes(), field.getBytes());
				log.info("hash-redis's function hdel() result:{}", result);
				return result;
			}
		});
	}
	
	/**
	 * 集合里面添加元素
	 * @param key
	 * @param obj
	 * @return
	 */
	public long sadd(String key, Object obj) {
		long size = redisTemplate.opsForSet().add(key, obj);
		log.info("集合添加结果:[{}]", size);
		return size;
	}
	
	
	/**
	 * 从队列中获取
	 * @param key
	 * @return
	 */
	public String lpop(final String key){
		log.info("lpop()'s function param:key:{}", key);
		return redisTemplate.execute(new RedisCallback<String>() {
			
			@Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
				byte[] result = connection.lPop(key.getBytes());
				log.info("hash-redis's function lpop() result:{}", result);
				try {
					if(result == null){
						return null;
					}
					return new String(result, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					log.info("hash-redis's function lpop() has exception:{}", e.getMessage());
					return null;
				}
			}
		});
	}
	
	/**
	 * @param key
	 * @return
	 */
	public String rpop(final String key){
		log.info("rpop()'s function param:key:{}", key);
		return redisTemplate.execute(new RedisCallback<String>() {
			
			@Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
				byte[] result = connection.rPop(key.getBytes());
				log.info("hash-redis's function rpop() result:{}", result);
				try {
					if(result == null){
						return null;
					}
					return new String(result, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					log.info("hash-redis's function rpop() has exception:{}", e.getMessage());
					return null;
				}
			}
		});
	}
	
	/**
	 * 查询zset集合中member的权重
	 * @param key
	 * @param member
	 * @return
	 */
	public Double zscore(final String key, final String member) {
		log.info("zscore()'s function param:key:{}, member:{}", key, member);
		return redisTemplate.execute(new RedisCallback<Double>() {
			
			@Override
            public Double doInRedis(RedisConnection connection) throws DataAccessException {
				Double result = connection.zScore(key.getBytes(), member.getBytes());
				log.info("zset-redis's function zscore() result:{}", result);
				return result;
			}
		});
	}
	
	/**
	 * 设置过期时间
	 * @param key
	 */
	public void expire(final String key, final long time){
		log.info("expire()'s function param:key:{}, time:{}", key, time);
		redisTemplate.execute(new RedisCallback<Boolean>() {
			
			@Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				boolean result = connection.expire(key.getBytes(), time);
				log.info("function expire()'s result is:{}", result);
				return result;
			}
		});
	}

	/**
	 * 锁1秒
	 * @param key
	 * @param value
	 * @param lockTime 单位秒
	 * @return
	 */
	public boolean setNX(String key, String value,long lockTime) {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
				byte[] keyByte = serializer.serialize(key);
				if (connection.setNX(keyByte,serializer.serialize(value))) {
					//设置失效时间，防止死锁
					connection.expire(keyByte, lockTime);
					return true;
				}
				return false;
			}
		});
	}

}
