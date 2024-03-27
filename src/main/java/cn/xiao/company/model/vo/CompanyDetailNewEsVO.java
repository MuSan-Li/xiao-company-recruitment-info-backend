package cn.xiao.company.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 公司详细信息-ES实体
 */
@Data
public class CompanyDetailNewEsVO implements Serializable {


    /**
     * id
     */

    private Long id;

    /**
     * 公司名称
     */

    private String companyName;

    /**
     * 公司简介
     */
    private String companyProfile;

    /**
     * 地区-省份
     */
    private String territory;

    /**
     * 分类
     */
    private String category;

    /**
     * 招聘网址 JSON格式
     */
    private String recruitment;
}