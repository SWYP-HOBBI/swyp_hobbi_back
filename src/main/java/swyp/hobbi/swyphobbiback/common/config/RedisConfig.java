package swyp.hobbi.swyphobbiback.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import swyp.hobbi.swyphobbiback.challenge.dto.ChallengeCache;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, ChallengeCache> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, ChallengeCache> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }
}
