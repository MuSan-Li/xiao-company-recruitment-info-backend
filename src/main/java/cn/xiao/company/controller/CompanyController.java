package cn.xiao.company.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.xiao.company.annotation.AuthCheck;
import cn.xiao.company.common.BaseResponse;
import cn.xiao.company.common.DeleteRequest;
import cn.xiao.company.common.ErrorCode;
import cn.xiao.company.common.ResultUtils;
import cn.xiao.company.constant.UserConstant;
import cn.xiao.company.exception.BusinessException;
import cn.xiao.company.exception.ThrowUtils;
import cn.xiao.company.model.dto.company.CompanyAddRequest;
import cn.xiao.company.model.dto.company.CompanyQueryRequest;
import cn.xiao.company.model.dto.company.CompanyUpdateRequest;
import cn.xiao.company.model.entity.Company;
import cn.xiao.company.model.entity.User;
import cn.xiao.company.model.vo.CompanyVO;
import cn.xiao.company.service.CompanyDetailService;
import cn.xiao.company.service.CompanyService;
import cn.xiao.company.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 公司接口
 *
 * @author xiao
 */
@Slf4j
@RestController
@RequestMapping("/company")
public class CompanyController {

    @Resource
    private UserService userService;

    @Resource
    private CompanyService companyService;


    // region 增删改查

    /**
     * 创建
     *
     * @param addRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addCompany(@RequestBody CompanyAddRequest addRequest,
                                         HttpServletRequest request) {
        if (ObjectUtil.isEmpty(addRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Company company = new Company();
        BeanUtils.copyProperties(addRequest, company);
        companyService.validCompany(company, true);
        User loginUser = userService.getLoginUser(request);
        company.setUserId(loginUser.getId());
        boolean result = companyService.save(company);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newCompanyId = company.getId();
        return ResultUtils.success(newCompanyId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteCompany(@RequestBody DeleteRequest deleteRequest,
                                               HttpServletRequest request) {
        if (ObjectUtil.isEmpty(deleteRequest) || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Company oldCompany = companyService.getById(id);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(oldCompany), ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldCompany.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean removed = companyService.removeById(id);
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
    public BaseResponse<Boolean> updateCompany(@RequestBody CompanyUpdateRequest updateRequest) {
        if (ObjectUtil.isEmpty(updateRequest) || updateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Company company = new Company();
        BeanUtils.copyProperties(updateRequest, company);
        // 参数校验
        companyService.validCompany(company, false);
        long id = updateRequest.getId();
        // 判断是否存在
        Company oldCompany = companyService.getById(id);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(oldCompany), ErrorCode.NOT_FOUND_ERROR);
        boolean result = companyService.updateById(company);
        return ResultUtils.success(result);
    }

    /**
     * 分页获取列表（封装类） mysql 管理员
     *
     * @param queryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<CompanyVO>> listCompanyVOByPage(@RequestBody CompanyQueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Company> companyPage = companyService.page(new Page<>(current, size),
                companyService.getQueryWrapper(queryRequest));
        return ResultUtils.success(companyService.getCompanyVOPage(companyPage));
    }

    // endregion
}
