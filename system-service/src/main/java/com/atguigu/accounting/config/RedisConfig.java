package com.atguigu.accounting.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 7/4/2023 10:43 PM
 */
@Configuration
//InitializingBean: springbean添加生命周期方法
public class RedisConfig implements InitializingBean {
    @Autowired
    RedisTemplate redisTemplate;

    //afterPropertiesSet:当前对象初始化属性值以后调用
    @Override
    public void afterPropertiesSet() throws Exception {
        //key使用字符串来序列化
        this.redisTemplate.setKeySerializer(new StringRedisSerializer());
        //value使用json序列化
        this.redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    }
    //创建缓存管理器
    @Bean
    public RedisCacheManager redisCacheManager(LettuceConnectionFactory connectionFactory){
        //使用默认配置+自定义的配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(100)) //配置缓存的过期时间
                //配置key的字符串序列化器
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                //配置value的json序列化器
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
                //默认是缓存空值的,避免出现缓存击穿,所以这个地方不用配置了
        //创建redis缓存管理器并return
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory).cacheDefaults(config).build();
        return redisCacheManager;
    }
}
