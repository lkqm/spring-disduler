# spring-disduler
Distributed spring scheduling task by lock & aop

一个让spring定时任务实现分布式支持的扩展工具, 通过AOP对定时任务加分布式独占锁实现, 支持redis, db分布式锁.

## Why
我们喜欢spring的定时任务, 它简单、强大、略施小计便可实现分布式任务, 在单体应用中不希望引入略显复杂的quartz, 或者依赖额外组件xxl-job等.

## Quick Start
1. 添加依赖
    ```xml
    <dependency>
        <groupId>com.github.lkqm</groupId>
        <artifactId>spring-disduler</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    ```

2. 定义一个定时任务
    ```java
    @Component
    public class TestJob {
        @Scheduled(fixedRate = 10*1000 )
        public void test() {
            System.out.println("disduler");
        }
    }
    ```
    
3. 启动类添加注解: `@EnableScheduling, @EnableDisduler`

4. 配置
    ```properties
    disduler.type=redis                        # 分布式类型(redis, db), 默认: redis
    disduler.lock-key-prefix=disduler          # 锁前缀, 默认: disduler
    disduler.lock-expire-seconds=10            # 锁过期秒数, 默认: 10s
    disduler.table-name=disduler_lock          # 基于数据库的锁表名, 默认: disduler_lock
    ```

## @ScheduledLock
默认会对所有定时任务(@Scheduled注解的方法)进行分布式支持, 注解@ScheduledLock可以对某个定时任务进行控制:

- value: 等价expireSeconds字段
- expireSeconds: 锁过期秒数, 默认: -1全局配置
- lock: 布尔类型, 是否开启锁, 默认: true

## 类似项目
[ShedLock](https://github.com/lukas-krecan/ShedLock): Distributed lock for your scheduled tasks
