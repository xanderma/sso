package cn.supermartin.ssoserver.config;

import cn.supermartin.ssoserver.common.LocalTokenManager;
import cn.supermartin.ssoserver.common.RedisCache;
import cn.supermartin.ssoserver.common.RedisTokenManager;
import cn.supermartin.ssoserver.common.TokenManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author maxiaoding
 * @date 2017/11/16 下午3:28
 * @description:
 */
@Configuration
@ImportResource(locations={"classpath:dubbo-provider.xml"})
public class BeanConfig {
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        return new JedisPoolConfig();
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName("localhost");
        jedisConnectionFactory.setPort(6379);
        jedisConnectionFactory.setPassword("");
        jedisConnectionFactory.setDatabase(0);
        jedisConnectionFactory.setPoolConfig(jedisPoolConfig());
        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate redisTemplate() {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public RedisCache redisCache() {
        RedisCache redisCache = new RedisCache();
        redisCache.setRedisTemplate(redisTemplate());
        return redisCache;
    }


    @Bean
    public TokenManager tokenManager() {
        return new RedisTokenManager();
//        return new LocalTokenManager();
    }
}
