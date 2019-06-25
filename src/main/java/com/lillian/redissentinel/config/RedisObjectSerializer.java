/**
 * transfer Inc.
 * Copyright (c) 2012-2016 All Rights Reserved.
 */
package com.lillian.redissentinel.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * 总体说明
 *
 * <p>具体说明</p>
 *
 * @author linglin.wang
 * @version $Id: RedisObjectSerializer.java,v 0.1 2016年12月30日 下午2:05:52  linglin.wang Exp $
 */
public class RedisObjectSerializer implements RedisSerializer<Object> {

    private Converter<Object, byte[]> serializer = new SerializingConverter();
    private Converter<byte[], Object> deserializer = new DeserializingConverter();

    static final byte[] EMPTY_ARRAY = new byte[0];

    /**
     * @see RedisSerializer#deserialize(byte[])
     */
    public Object deserialize(byte[] bytes) {
      if (isEmpty(bytes)) {
        return null;
      }

      try {
        return deserializer.convert(bytes);
      } catch (Exception ex) {
        throw new SerializationException("Cannot deserialize", ex);
      }
    }

    /**
     * @see RedisSerializer#serialize(Object)
     */
    public byte[] serialize(Object object) {
      if (object == null) {
        return EMPTY_ARRAY;
      }

      try {
        return serializer.convert(object);
      } catch (Exception ex) {
        return EMPTY_ARRAY;
      }
    }

    private boolean isEmpty(byte[] data) {
      return (data == null || data.length == 0);
    }

}
