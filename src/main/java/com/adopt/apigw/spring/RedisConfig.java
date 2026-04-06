package com.adopt.apigw.spring;

import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.SocketOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value(value = "${spring.redis.host}")
    private String host;

    @Value(value = "${spring.redis.port}")
    private Integer port;

    @Value("${redis.sentinel.master}")
    private String master;

    @Value("${redis.sentinel.nodes}")
    private String sentinelNodes;
    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
//        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(configuration);
//        return lettuceConnectionFactory;
//    }

//    @Bean
//    public RedisConnectionFactory redisConnectionFactorys() {
//        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
//
//        // Configure connection pooling
//        LettucePoolingClientConfiguration poolingClientConfiguration = LettucePoolingClientConfiguration.builder()
//                .commandTimeout(Duration.ofMillis(5000))
//                .clientOptions(ClientOptions.builder()
//                        .socketOptions(SocketOptions.builder().connectTimeout(Duration.ofMillis(500)).build()) // Fast connection timeout
//                        .build())
//                .build();
//
//        return new LettuceConnectionFactory(configuration, poolingClientConfiguration);
//    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder().readFrom(ReadFrom.REPLICA_PREFERRED).build();
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration().master(master);
        for (String url : sentinelNodes.split(",")){
         String[] hostPort = url.split(":");
         sentinelConfig.sentinel(hostPort[0], Integer.valueOf(hostPort[1]));
        }
//        redisProperties.getSentinel().getNodes().forEach(s -> sentinelConfig.sentinel(redisProperties.getUrl(), Integer.valueOf(s)));
        sentinelConfig.setPassword(RedisPassword.of(password));
        return new LettuceConnectionFactory(sentinelConfig, clientConfig);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

//    @Bean
//    public RedisTemplate<String, Object> redisTemplates(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisConnectionFactory);
//
//        // Use optimized Jackson JSON serializer for objects (generic)
//        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(customObjectMapper());
//
//        template.setKeySerializer(new StringRedisSerializer()); // Serialize keys as strings
//        template.setValueSerializer(jsonSerializer); // Serialize values as objects
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(jsonSerializer);
//
//        template.setEnableTransactionSupport(false); // Disable transaction for better performance
//
//        return template;
//    }
//
//    @Bean
//    public ObjectMapper customObjectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        objectMapper.registerModule(new JavaTimeModule()); // Handle Java 8 Time API
//        return objectMapper;
//    }

    @Bean
    public RedisTemplate<String, Object> redisTemplates(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Use StringRedisSerializer for keys
        template.setKeySerializer(new StringRedisSerializer());

        // Use Jackson serializer for values (Generic)
        Jackson2JsonRedisSerializer<Object> valueSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        template.setValueSerializer(valueSerializer);

        return template;
    }

//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
//        return new LettuceConnectionFactory(configuration);
//    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.registerModule(new JavaTimeModule()); // For LocalDateTime support
        return objectMapper;
    }



}
