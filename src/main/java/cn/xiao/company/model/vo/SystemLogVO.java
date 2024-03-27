package cn.xiao.company.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 公司视图
 *
 * @author xiao
 */
@Data
public class SystemLogVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 用户名称
     */
    private String username;

    /**
     * ip
     */
    private String ip;

    /**
     * ip来源
     */
    private String ipSource;

    /**
     * 请求路径
     */
    private String url;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 登录状态
     */
    private Boolean status;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 结果
     */
    private String result;

    /**
     * 消耗总时长（毫秒）
     */
    private Long totalTimeMillis;

    /**
     * user-agent用户代理
     */
    private String userAgent;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}
