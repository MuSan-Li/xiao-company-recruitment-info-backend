package cn.xiao.company.model.dto.company.detail;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新请求
 *
 * @author xiao
 */
@Data
public class CompanyDetailUpdateRequest implements Serializable {

    /**
     * ID
     */
    private Long id;

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 股票名称
     */
    private String stockName;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 英文名称
     */
    private String englishName;

    /**
     * 曾用名
     */
    private String formerName;

    /**
     * 公司简介
     */
    private String companyProfile;

    /**
     * 上市时间
     */
    private Date timeToMarket;

    /**
     * 公司详细信息网址
     */
    private String detailInfoUrl;

    /**
     * 所属地域
     */
    private String territory;

    /**
     * 行业分类
     */
    private String category;

    /**
     * 公司网址
     */
    private String companyWebsiteAddress;

    /**
     * 办公地址
     */
    private String officeAddress;

    /**
     * 主营业务
     */
    private String mainBusiness;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 控股股东
     */
    private String controllingShareholder;

    /**
     * 实际控制人
     */
    private String actualController;

    /**
     * 最终控制人
     */
    private String ultimateController;

    /**
     * 法人代表
     */
    private String corporateRepresentative;

    /**
     * 董事长
     */
    private String chairmanOfTheBoard;

    /**
     * 董秘
     */
    private String secretaryToTheBoard;

    /**
     * 总经理
     */
    private String generalManager;

    /**
     * 注册资金
     */
    private String registeredCapital;

    /**
     * 员工人数
     */
    private Integer numberOfEmployees;

    /**
     * 电话
     */
    private String telephone;

    /**
     * 传真
     */
    private String fax;

    /**
     * 邮编
     */
    private String postcode;

    /**
     * 招聘网址 JSON格式
     */
    private String recruitment;

    /**
     * 扩展字段
     */
    private String extend;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

}