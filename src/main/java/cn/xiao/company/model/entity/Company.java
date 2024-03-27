package cn.xiao.company.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 公司数据
 *
 * @author xiao
 */
@Data
@TableName(value = "t_company")
public class Company implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 股票代码
     */
    @TableField("stock_code")
    private String stockCode;

    /**
     * 股票名称
     */
    @TableField("stock_name")
    private String stockName;

    /***
     * 公司全称
     */
    @TableField("company_name")
    private String companyName;

    /**
     * 上市时间
     */
    @TableField("time_to_market")
    private Date timeToMarket;

    /**
     * 详细信息网址
     */
    @TableField("detail_info_url")
    private String detailInfoUrl;

    /**
     * 招股书
     */
    @TableField("prospectus")
    private String prospectus;

    /**
     * 公司财报
     */
    @TableField("financial_report")
    private String financialReport;

    /**
     * 行业分类
     */
    @TableField("category")
    private String category;

    /**
     * 主营业务
     */
    @TableField("main_business")
    private String mainBusiness;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

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