package cn.xiao.company.service;

import cn.xiao.company.model.dto.company.CompanyQueryRequest;
import cn.xiao.company.model.entity.CompanyDetail;
import cn.xiao.company.model.vo.SearchVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 公司详细信息-新表服务接口
 *
 * @author xiao
 */
public interface CompanyDetailService extends IService<CompanyDetail> {

    /**
     * 从 ES 查询
     *
     * @param queryRequest
     * @return
     */
    SearchVO searchEsByVOPage(CompanyQueryRequest queryRequest);

    /**
     * 根据关键词查询公司信息
     *
     * @param searchKey
     * @return
     */
    List<String> esSearchCompanyInfoKey(String searchKey);

    /**
     * 校验公司信息
     *
     * @param company
     * @param add     是否添加
     */
    void validCompany(CompanyDetail company, boolean add);

}

