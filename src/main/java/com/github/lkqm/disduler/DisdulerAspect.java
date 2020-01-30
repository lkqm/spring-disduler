package com.github.lkqm.disduler;

import com.github.lkqm.disduler.lock.Lock;
import com.github.lkqm.disduler.lock.LockInfo;
import com.github.lkqm.disduler.lock.ScheduledLock;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.UUID;

/**
 * 定时任务切面
 */
@Aspect
@AllArgsConstructor
@Slf4j
public class DisdulerAspect {

    private DisdulerProperties disdulerProperties;
    private Lock distributeLock;

    @Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void scheduled() {
    }

    @Around("scheduled()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        LockInfo lockInfo = resolveLockInfo(point);
        if (!lockInfo.isNeedLock()) {
            return point.proceed();
        }
        String who = UUID.randomUUID().toString();
        boolean isLockSuccess = distributeLock.lock(lockInfo.getKey(), who, lockInfo.getExpiredSeconds());
        if (isLockSuccess) {
            log.debug("disduler lock key={}, expire={}s", lockInfo.getKey(), lockInfo.getExpiredSeconds());
            try {
                return point.proceed();
            } finally {
                distributeLock.release(lockInfo.getKey(), who);
                log.debug("disduler unlocked key={}", lockInfo.getKey());
            }
        }
        log.debug("disduler lock unsuccessful key={}, expire={}s", lockInfo.getKey(), lockInfo.getExpiredSeconds());
        return null;
    }

    private LockInfo resolveLockInfo(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        ScheduledLock scheduledLock = AnnotationUtils.getAnnotation(signature.getMethod(), ScheduledLock.class);

        LockInfo lockInfo = new LockInfo();
        lockInfo.setNeedLock(true);
        lockInfo.setKey(disdulerProperties.getLockKeyPrefix() + ":" + getLockKey(signature));
        lockInfo.setExpiredSeconds(disdulerProperties.getLockExpireSeconds());
        if (scheduledLock != null) {
            lockInfo.setNeedLock(scheduledLock.lock());
            if (scheduledLock.expireSeconds() > 0) {
                lockInfo.setExpiredSeconds(scheduledLock.expireSeconds());
            } else if (scheduledLock.value() > 0) {
                lockInfo.setExpiredSeconds(scheduledLock.value());
            }
        }
        return lockInfo;
    }

    private String getLockKey(MethodSignature signature) {
        return String.format("%s.%s", signature.getDeclaringTypeName(), signature.getMethod().getName());
    }

}
