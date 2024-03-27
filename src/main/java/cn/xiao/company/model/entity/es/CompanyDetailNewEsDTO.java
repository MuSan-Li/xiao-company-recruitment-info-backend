package cn.xiao.company.model.entity.es;

import cn.xiao.company.constant.EsIndexConstant;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * 公司详细信息-ES实体
 */
@Data
@Document(indexName = EsIndexConstant.COMPANY_DETAIL_INFO_INDEX)
public class CompanyDetailNewEsDTO implements Serializable {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * id
     */
    @Id
    private Long id;

    /**
     * 公司名称
     */
    @Field(name = "company_name")
    private String companyName;

    /**
     * 公司简介
     */
    @Field(name = "company_profile")
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

    /**
     * 创建时间
     */
    @Field(index = false,
            name = "create_time",
            store = true,
            type = FieldType.Date,
            format = {},
            pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(index = false,
            name = "update_time",
            store = true,
            type = FieldType.Date,
            format = {},
            pattern = DATE_TIME_PATTERN)
    private Date updateTime;

    /**
     * 是否删除
     */
    @Field(name = "is_delete")
    private Integer isDelete;
}