package cn.xiao.company.controller;

import cn.xiao.company.annotation.AuthCheck;
import cn.xiao.company.common.BaseResponse;
import cn.xiao.company.common.ErrorCode;
import cn.xiao.company.common.ResultUtils;
import cn.xiao.company.constant.UserConstant;
import cn.xiao.company.exception.ThrowUtils;
import cn.xiao.company.model.dto.system.log.SystemLogQueryRequest;
import cn.xiao.company.model.entity.SystemLog;
import cn.xiao.company.model.vo.SystemLogVO;
import cn.xiao.company.service.SystemLogService;
import cn.xiao.company.utils.ConvertUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 系统日志接口
 *
 * @author xiao
 */
@Slf4j
@RestController
@RequestMapping("/systemLog")
public class SystemLogController {

    @Resource
    private SystemLogService systemLogService;

    /**
     * 分页获取列表（封装类）
     *
     * @param queryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<IPage<SystemLogVO>> listSystemLogVOByPage(@RequestBody SystemLogQueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        IPage<SystemLog> page = systemLogService.page(new Page<>(current, size),
                systemLogService.getQueryWrapper(queryRequest));
        return ResultUtils.success(ConvertUtils.convert(page, SystemLogVO.class));
    }

}
