package cn.xiao.company.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.xiao.company.annotation.AccessLimit;
import cn.xiao.company.annotation.AuthCheck;
import cn.xiao.company.common.BaseResponse;
import cn.xiao.company.common.DeleteRequest;
import cn.xiao.company.common.ErrorCode;
import cn.xiao.company.common.ResultUtils;
import cn.xiao.company.constant.UserConstant;
import cn.xiao.company.exception.BusinessException;
import cn.xiao.company.exception.ThrowUtils;
import cn.xiao.company.model.dto.company.CompanyQueryRequest;
import cn.xiao.company.model.dto.company.detail.CompanyDetailAddRequest;
import cn.xiao.company.model.dto.company.detail.CompanyDetailUpdateRequest;
import cn.xiao.company.model.entity.CompanyDetail;
import cn.xiao.company.model.vo.CompanyDetailNewEsVO;
import cn.xiao.company.model.vo.CompanyDetailVO;
import cn.xiao.company.model.vo.SearchVO;
import cn.xiao.company.service.CompanyDetailService;
import cn.xiao.company.utils.ConvertUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公司详情接口
 *
 * @author xiao
 */
@Slf4j
@RestController
@RequestMapping("/companyDetail")
public class CompanyDetailController {

    @Resource
    private CompanyDetailService companyDetailService;

    // region 增删改查

    /**
     * 创建
     *
     * @param addRequest
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addCompanyDetail(@RequestBody CompanyDetailAddRequest addRequest) {
        if (ObjectUtil.isEmpty(addRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        CompanyDetail companyDetail = new CompanyDetail();
        BeanUtils.copyProperties(addRequest, companyDetail);
        companyDetailService.validCompany(companyDetail, true);
        boolean result = companyDetailService.save(companyDetail);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newCompanyId = companyDetail.getId();
        return ResultUtils.success(newCompanyId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteCompanyDetail(@RequestBody DeleteRequest deleteRequest) {
        if (ObjectUtil.isEmpty(deleteRequest) || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        CompanyDetail companyDetail = companyDetailService.getById(deleteRequest.getId());
        ThrowUtils.throwIf(ObjectUtil.isEmpty(companyDetail), ErrorCode.NOT_FOUND_ERROR);
        boolean removed = companyDetailService.removeById(companyDetail);
        return ResultUtils.success(removed);
    }

    /**
     * 更新（仅管理员）
     *
     * @param updateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateCompanyDetail(@RequestBody CompanyDetailUpdateRequest updateRequest) {
        if (ObjectUtil.isEmpty(updateRequest) || updateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        CompanyDetail companyDetail = new CompanyDetail();
        BeanUtils.copyProperties(updateRequest, companyDetail);
        // 参数校验
        companyDetailService.validCompany(companyDetail, false);
        long id = updateRequest.getId();
        // 判断是否存在
        CompanyDetail oldCompanyDetail = companyDetailService.getById(id);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(oldCompanyDetail), ErrorCode.NOT_FOUND_ERROR);
        boolean result = companyDetailService.updateById(companyDetail);
        return ResultUtils.success(result);
    }


    /**
     * 根据 stockCode 获取 详情
     *
     * @param stockCode
     * @return
     */
    @GetMapping("/getByStockCode")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<CompanyDetailVO> getCompanyDetailVOByStockCode(String stockCode) {
        if (CharSequenceUtil.isBlank(stockCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<CompanyDetail> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CompanyDetail::getStockCode, stockCode);
        CompanyDetail companyDetail = companyDetailService.getOne(wrapper);
        if (ObjectUtil.isEmpty(companyDetail)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        CompanyDetailVO companyDetailVO = ConvertUtils.convert(companyDetail, CompanyDetailVO.class);
        return ResultUtils.success(companyDetailVO);
    }

    // endregion

    /**
     * 分页获取列表（封装类）es 首页
     *
     * @param queryRequest
     * @return
     */
    @AccessLimit(seconds = 1, maxCount = 5)
    @PostMapping("/es/page/vo")
    public BaseResponse<IPage<CompanyDetailNewEsVO>> esCompanyDetailVOByPage(@RequestBody CompanyQueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        String searchText = queryRequest.getSearchText();
        // 限制爬虫
        ThrowUtils.throwIf(size > 10 || current <= -1 || current > 9999, ErrorCode.PARAMS_ERROR);
        if (CharSequenceUtil.isNotBlank(searchText) && searchText.length() > 25) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索关键字长度过长！");
        }
        SearchVO searchVO = companyDetailService.searchEsByVOPage(queryRequest);
        return ResultUtils.success(searchVO.getPageVO());
    }


    /**
     * 根据关键词搜索公司信息
     *
     * @param searchKey
     * @return
     */
    @AccessLimit(seconds = 1, maxCount = 20)
    @GetMapping("/es/search/key")
    public BaseResponse<List<String>> esSearchCompanyDetailInfoKey(@RequestParam String searchKey) {
        List<String> esSearchCompanyInfoKeyList = companyDetailService.esSearchCompanyInfoKey(searchKey);
        return ResultUtils.success(esSearchCompanyInfoKeyList);
    }

}
