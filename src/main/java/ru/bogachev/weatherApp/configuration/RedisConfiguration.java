package ru.bogachev.weatherApp.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

    private final String redisHost;
    private final int redisPort;
    private final String redisPassword;


    public RedisConfiguration(
            @Value("${spring.data.redis.host}") final String redisHost,
            @Value("${spring.data.redis.port}") final int redisPort,
            @Value("${spring.data.redis.password}") final String redisPassword
    ) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
        this.redisPassword = redisPassword;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig =
                new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        redisConfig.setPassword(redisPassword);
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration
                .builder()
                .build();
        return new LettuceConnectionFactory(redisConfig, clientConfig);
    }

    @Bean
    public RedisSerializer<String> stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public RedisSerializer<Object> objectRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            final RedisConnectionFactory connectionFactory,
            final RedisSerializer<String> stringRedisSerializer,
            final RedisSerializer<Object> objectRedisSerializer
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(objectRedisSerializer);
        return template;
    }
}
