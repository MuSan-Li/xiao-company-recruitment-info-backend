package cn.xiao.company.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 公司视图
 *
 * @author xiao
 */
@Data
public class CompanyIndexVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 股票名称
     */
    private String stockName;

    /***
     * 公司全称
     */
    private String companyName;

    /**
     * 公司网址
     */
    private String webSiteAddr;

    /**
     * 主营业务
     */
    private String mainBusiness;

    /**
     * 行业分类
     */
    private String category;

    /**
     * 公司简介
     */
    private String companyProfile;

    /**
     * 所属地域
     */
    private String territory;

}
