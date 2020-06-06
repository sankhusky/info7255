package com.sanket.springboot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@SpringBootApplication
@EnableRedisRepositories
@ComponentScan
@Configuration
public class SpringbootApplication {


	@Value(value = "${redis.hostname}")
	private String redisHostname;

	@Value(value = "${redis.port}")
	private int redisPort;

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
	}

	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHostname, redisPort);
		return new JedisConnectionFactory(redisStandaloneConfiguration);
	}
	@Bean
	public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
	    return new ShallowEtagHeaderFilter();
	}
	@Bean
	public RedisTemplate<String, Object> userTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		return template;
	}
}
