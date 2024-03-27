package cn.xiao.company.service;

import cn.xiao.company.model.dto.system.log.SystemLogQueryRequest;
import cn.xiao.company.model.entity.SystemLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统日志表业务逻辑
 *
 * @author xiao
 */
public interface SystemLogService extends IService<SystemLog> {

    /**
     * 保存日志
     *
     * @param request
     * @param requestId
     * @param reqParam
     * @param desc
     * @param totalTimeMillis
     * @return
     */
    boolean saveSystemLog(HttpServletRequest request, String requestId, String reqParam,
                          String desc, long totalTimeMillis);

    LambdaQueryWrapper<SystemLog> getQueryWrapper(SystemLogQueryRequest queryRequest);
}
