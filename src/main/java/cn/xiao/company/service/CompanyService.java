package cn.xiao.company.service;

import cn.xiao.company.model.dto.company.CompanyQueryRequest;
import cn.xiao.company.model.entity.Company;
import cn.xiao.company.model.vo.CompanyIndexVO;
import cn.xiao.company.model.vo.CompanyVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 公司信息业务逻辑层
 *
 * @author xiao
 */
public interface CompanyService extends IService<Company> {

    /**
     * 校验
     *
     * @param company
     * @param add
     */
    void validCompany(Company company, boolean add);

    /**
     * 获取查询条件
     *
     * @param companyQueryRequest
     * @return
     */
    LambdaQueryWrapper<Company> getQueryWrapper(CompanyQueryRequest companyQueryRequest);

    /**
     * 分页获取公司封装
     *
     * @param companyPage
     * @return
     */
    Page<CompanyVO> getCompanyVOPage(Page<Company> companyPage);

}
