package cn.xiao.company.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统日志表
 *
 * @author xiao
 */
@Data
@TableName("t_system_log")
public class SystemLog implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 请求ID
     */
    @TableField("request_id")
    private String requestId;

    /**
     * 用户名称
     */
    @TableField("username")
    private String username;

    /**
     * ip
     */
    @TableField("ip")
    private String ip;

    /**
     * ip来源
     */
    @TableField("ip_source")
    private String ipSource;

    /**
     * 请求路径
     */
    @TableField("url")
    private String url;

    /**
     * 请求参数
     */
    @TableField("params")
    private String params;

    /**
     * 操作系统
     */
    @TableField("os")
    private String os;

    /**
     * 浏览器
     */
    @TableField("browser")
    private String browser;

    /**
     * 登录状态
     */
    @TableField("status")
    private Boolean status;

    /**
     * 操作描述
     */
    @TableField("description")
    private String description;

    /**
     * 登录时间
     */
    @TableField("login_time")
    private Date loginTime;

    /**
     * 消耗总时长（毫秒）
     */
    @TableField("total_time_millis")
    private Long totalTimeMillis;

    /**
     * user-agent用户代理
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField("is_delete")
    private Integer isDelete;
}
