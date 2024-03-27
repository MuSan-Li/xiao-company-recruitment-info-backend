package cn.xiao.company.aop;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import cn.xiao.company.annotation.AccessLimit;
import cn.xiao.company.common.ErrorCode;
import cn.xiao.company.exception.BusinessException;
import cn.xiao.company.manager.CacheManager;
import cn.xiao.company.manager.RedisLimiterManager;
import cn.xiao.company.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 请求响应日志 AOP
 *
 * @author xiao
 **/
@Aspect
@Order(1)
@Component
@Slf4j
public class LogInterceptor {

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private CacheManager cacheManager;

    @Resource
    private SystemLogService systemLogService;

    /**
     * 执行拦截
     */
    @Around("execution(* cn.xiao.company.controller.*.*(..))")
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
        // 计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 获取请求路径
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        // 生成请求唯一 id
        String requestId = new SnowflakeGenerator().next() + "-" + UUID.randomUUID();
        String url = request.getRequestURI();
        String addr = request.getRemoteAddr();
        String requestKey = "request_" + addr + "_" + url;
        // 获取请求参数
        Object[] args = point.getArgs();
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";
        Object target = point.getTarget();
        String methodName = point.getSignature().getName();
        // 黑名单设置
        Object accessKey = cacheManager.get(requestKey);
        if (Objects.nonNull(accessKey)) {
            // 输出响应日志
            stopWatch.stop();
            long totalTimeMillis = stopWatch.getTotalTimeMillis();
            log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);
            // 异步保存日志
            CompletableFuture.runAsync(() -> systemLogService.saveSystemLog(request, requestId,
                    reqParam, "此IP请求频繁，限制请求1小时", totalTimeMillis));
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
        Class<?>[] methodParameters = ((MethodSignature) point.getSignature()).getParameterTypes();
        Method method = target.getClass().getMethod(methodName, methodParameters);
        AccessLimit accessLimit = method.getAnnotation(AccessLimit.class);
        if (Objects.nonNull(accessLimit)) {
            // 输出注解信息
            log.info("accessLimit :{}", accessLimit);
            // 失效时间 单位（秒）
            int seconds = accessLimit.seconds();
            // 最大请求次数
            int maxCount = accessLimit.maxCount();
            redisLimiterManager.doRateLimit(requestKey, maxCount, seconds);
        }

        // 输出请求日志
        log.info("request start，id: {}, path: {}, ip: {}, params: {}", requestId, url, addr, reqParam);
        // 执行原方法
        Object result = point.proceed();
        // 输出响应日志
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);
        // 异步保存日志
        CompletableFuture.runAsync(() -> systemLogService.saveSystemLog(request, requestId, reqParam,
                null, totalTimeMillis));
        // 返回结果
        return result;
    }

}

