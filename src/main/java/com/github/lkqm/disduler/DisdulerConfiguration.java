package com.github.lkqm.disduler;

import com.github.lkqm.disduler.lock.DatabaseLock;
import com.github.lkqm.disduler.lock.Lock;
import com.github.lkqm.disduler.lock.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 分布式定时任务配置类
 */
@Configuration
@EnableConfigurationProperties(DisdulerProperties.class)
@Slf4j
public class DisdulerConfiguration {

    @Autowired
    private DisdulerProperties disdulerProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = DisdulerProperties.PREFIX, name = "type", havingValue = "redis", matchIfMissing = true)
    public Lock redisDistributeLock(StringRedisTemplate redisTemplate) {
        return new RedisLock(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = DisdulerProperties.PREFIX, name = "type", havingValue = "db")
    public Lock databaseDistributeLock(JdbcTemplate jdbcTemplate) {
        return new DatabaseLock(DatabaseLock.DEFAULT_TABLE_NAME, jdbcTemplate);
    }

    @Bean
    public DisdulerAspect disdulerAspect(Lock distributeLock) {
        log.info("Disduler enabled {}", disdulerProperties);
        DisdulerAspect aop = new DisdulerAspect(this.disdulerProperties, distributeLock);
        return aop;
    }
}
