package cn.xiao.company.model.dto.company;

import cn.xiao.company.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 * @author xiao
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CompanyQueryRequest extends PageRequest implements Serializable {


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
     * 行业分类
     */
    private String category;

    /**
     * 按照关键字搜索
     */
    private String searchText;

}