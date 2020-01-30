CREATE TABLE `disduler_lock` (
	`key` VARCHAR ( 255 ) NOT NULL COMMENT '键',
	`data` VARCHAR ( 512 ) COMMENT '值',
	`lock_timestamp` BIGINT NOT NULL COMMENT '锁定时间轴',
	`lock_auto_expired_timestamp` BIGINT NOT NULL COMMENT '锁定过期时间',
  PRIMARY KEY ( `key` )
) COMMENT = 'Disduler分布式定时任务锁';