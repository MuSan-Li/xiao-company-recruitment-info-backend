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
public class CompanyVO implements Serializable {

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
    private String companyName;

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

    /**
     * 用户信息
     */
    private UserVO userVO;
}
