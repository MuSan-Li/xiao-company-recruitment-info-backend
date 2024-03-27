package cn.xiao.company.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 接口防刷注解类
 *
 * @author xiao
 **/
@Retention(RUNTIME)
@Target(METHOD)
public @interface AccessLimit {

    // 失效时间 单位（秒）
    int seconds();

    // 最大请求次数
    int maxCount();
}