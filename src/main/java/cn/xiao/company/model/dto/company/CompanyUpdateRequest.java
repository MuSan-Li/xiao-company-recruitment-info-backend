package cn.xiao.company.model.dto.company;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新请求
 *
 * @author xiao
 */
@Data
public class CompanyUpdateRequest implements Serializable {

    /**
     * id
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

    /***
     * 公司全称
     */
    private String name;

    /**
     * 上市时间
     */
    private Date timeToMarket;

    /**
     * 详细信息网址
     */
    private String detailInfoUrl;

    /**
     * 招股书
     */
    private String prospectus;

    /**
     * 公司财报
     */
    private String financialReport;

    /**
     * 行业分类
     */
    private String category;

    /**
     * 主营业务
     */
    private String mainBusiness;
}