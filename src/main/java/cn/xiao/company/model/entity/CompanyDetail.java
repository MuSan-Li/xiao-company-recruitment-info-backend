package cn.xiao.company.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 公司详细信息-新
 *
 * @author xiao
 */
@Data
@TableName("t_company_detail_new")
public class CompanyDetail implements Serializable {

    /**
     * ID
     */
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

    /**
     * 公司名称
     */
    @TableField("company_name")
    private String companyName;

    /**
     * 英文名称
     */
    @TableField("english_name")
    private String englishName;

    /**
     * 曾用名
     */
    @TableField("former_name")
    private String formerName;

    /**
     * 公司简介
     */
    @TableField("company_profile")
    private String companyProfile;

    /**
     * 上市时间
     */
    @TableField("time_to_market")
    private Date timeToMarket;

    /**
     * 公司详细信息网址
     */
    @TableField("detail_info_url")
    private String detailInfoUrl;

    /**
     * 所属地域
     */
    @TableField("territory")
    private String territory;

    /**
     * 行业分类
     */
    @TableField("category")
    private String category;

    /**
     * 公司网址
     */
    @TableField("company_website_address")
    private String companyWebsiteAddress;

    /**
     * 办公地址
     */
    @TableField("office_address")
    private String officeAddress;

    /**
     * 主营业务
     */
    @TableField("main_business")
    private String mainBusiness;

    /**
     * 产品名称
     */
    @TableField("product_name")
    private String productName;

    /**
     * 控股股东
     */
    @TableField("controlling_shareholder")
    private String controllingShareholder;

    /**
     * 实际控制人
     */
    @TableField("actual_controller")
    private String actualController;

    /**
     * 最终控制人
     */
    @TableField("ultimate_controller")
    private String ultimateController;

    /**
     * 法人代表
     */
    @TableField("corporate_representative")
    private String corporateRepresentative;

    /**
     * 董事长
     */
    @TableField("chairman_of_the_board")
    private String chairmanOfTheBoard;

    /**
     * 董秘
     */
    @TableField("secretary_to_the_board")
    private String secretaryToTheBoard;

    /**
     * 总经理
     */
    @TableField("general_manager")
    private String generalManager;

    /**
     * 注册资金
     */
    @TableField("registered_capital")
    private String registeredCapital;

    /**
     * 员工人数
     */
    @TableField("number_of_employees")
    private Integer numberOfEmployees;

    /**
     * 电话
     */
    @TableField("telephone")
    private String telephone;

    /**
     * 传真
     */
    @TableField("fax")
    private String fax;

    /**
     * 邮编
     */
    @TableField("postcode")
    private String postcode;

    /**
     * 招聘网址 JSON格式
     */
    @TableField("recruitment")
    private String recruitment;

    /**
     * 扩展字段
     */
    @TableField("extend")
    private String extend;

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

