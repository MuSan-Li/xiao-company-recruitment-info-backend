package cn.xiao.company.manager;

import cn.xiao.company.common.ErrorCode;
import cn.xiao.company.exception.BusinessException;
import cn.xiao.company.exception.ThrowUtils;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private CacheManager cacheManager;

    /**
     * 限流操作
     *
     * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
     */
    // public void doRateLimit(String key) {
    //     // 创建一个限流器
    //     RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
    //     // 每秒最多访问 1 次
    //     // 参数1 type：限流类型，可以是自定义的任何类型，用于区分不同的限流策略。
    //     // 参数2 rate：限流速率，即单位时间内允许通过的请求数量。
    //     // 参数3 rateInterval：限流时间间隔，即限流速率的计算周期长度。
    //     // 参数4 unit：限流时间间隔单位，可以是秒、毫秒等。
    //     rateLimiter.trySetRate(RateType.OVERALL, 1, 1, RateIntervalUnit.SECONDS);
    //     // 每当一个操作来了后，请求一个令牌
    //     boolean canOp = rateLimiter.tryAcquire(1);
    //     ThrowUtils.throwIf(!canOp, ErrorCode.TOO_MANY_REQUEST);
    // }

    /**
     * 限流操作
     *
     * @param key          区分不同的限流器，比如不同的用户 id 应该分别统计
     * @param rate         每秒最多访问的次数
     * @param rateInterval 限流的时间间隔，单位是秒
     */
    public void doRateLimit(String key, int rate, int rateInterval) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, rate, rateInterval, RateIntervalUnit.SECONDS);
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp) {
            cacheManager.put(key, true);
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
        // ThrowUtils.throwIf(!canOp, ErrorCode.TOO_MANY_REQUEST);
    }
}