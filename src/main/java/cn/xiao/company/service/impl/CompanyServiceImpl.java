package cn.xiao.company.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.xiao.company.common.ErrorCode;
import cn.xiao.company.exception.BusinessException;
import cn.xiao.company.mapper.CompanyMapper;
import cn.xiao.company.model.dto.company.CompanyQueryRequest;
import cn.xiao.company.model.entity.Company;
import cn.xiao.company.model.vo.CompanyVO;
import cn.xiao.company.service.CompanyService;
import cn.xiao.company.utils.ConvertUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 公司信息业务逻辑实现层
 *
 * @author xiao
 */
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company>
        implements CompanyService {

    @Override
    public void validCompany(Company company, boolean add) {

        if (ObjectUtil.isEmpty(company)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String stockCode = company.getStockCode();
        String stockName = company.getStockName();

        // 创建时，参数不能为空
        if (add && StringUtils.isBlank(stockCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 有参数则校验
        if (StringUtils.isNotBlank(stockName) && stockName.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "股票名称过长");
        }
        // TODO 补全其他校验
    }

    /**
     * 获取查询包装类
     *
     * @param queryRequest
     * @return
     */
    @Override
    public LambdaQueryWrapper<Company> getQueryWrapper(CompanyQueryRequest queryRequest) {

        LambdaQueryWrapper<Company> wrapper = Wrappers.lambdaQuery();
        if (ObjectUtil.isEmpty(queryRequest)) {
            return wrapper;
        }
        String stockCode = queryRequest.getStockCode();
        String stockName = queryRequest.getStockName();
        String companyName = queryRequest.getCompanyName();
        String category = queryRequest.getCategory();
        String searchText = queryRequest.getSearchText();

        wrapper.like(CharSequenceUtil.isNotBlank(stockCode), Company::getStockCode, stockCode)
                .like(CharSequenceUtil.isNotBlank(stockName), Company::getStockName, stockName)
                .like(CharSequenceUtil.isNotBlank(companyName), Company::getCompanyName, companyName)
                .like(CharSequenceUtil.isNotBlank(category), Company::getCategory, category);
        if (CharSequenceUtil.isNotBlank(searchText)) {
            wrapper.and(wq ->
                    wq.like(Company::getCategory, searchText).or()
                            .like(Company::getCompanyName, searchText));
        }
        return wrapper;
    }

    @Override
    public Page<CompanyVO> getCompanyVOPage(Page<Company> page) {
        List<Company> companyList = page.getRecords();
        Page<CompanyVO> companyVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        if (CollectionUtils.isEmpty(companyList)) {
            return companyVOPage;
        }

        List<CompanyVO> companyVOList = companyList.stream()
                .map(company -> ConvertUtils.convert(company, CompanyVO.class))
                .collect(Collectors.toList());

        companyVOPage.setRecords(companyVOList);

        return companyVOPage;
    }

}




