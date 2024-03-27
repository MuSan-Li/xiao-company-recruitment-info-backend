package cn.xiao.company.service.impl;

import cn.hutool.core.net.Ipv4Util;
import cn.xiao.company.constant.UserConstant;
import cn.xiao.company.mapper.SystemLogMapper;
import cn.xiao.company.model.dto.system.log.SystemLogQueryRequest;
import cn.xiao.company.model.entity.SystemLog;
import cn.xiao.company.model.entity.User;
import cn.xiao.company.service.SystemLogService;
import cn.xiao.company.utils.IpAddressUtils;
import cn.xiao.company.utils.UserAgentUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 系统日志表业务逻辑实现
 *
 * @author xiao
 */
@Slf4j
@Service
public class SystemLogServiceImpl extends ServiceImpl<SystemLogMapper, SystemLog>
        implements SystemLogService {

    @Resource
    private UserAgentUtils userAgentUtils;


    @Override
    public boolean saveSystemLog(HttpServletRequest request, String requestId,
                                 String reqParam, String desc, long totalTimeMillis) {
        log.info("save system log start,request: {},requestId: {},reqParam: {},totalTimeMillis: {},",
                request, requestId, reqParam, totalTimeMillis);
        String ip = IpAddressUtils.getIpAddress(request);
        String ipSource = IpAddressUtils.getCityInfo(ip);
        String url = request.getRequestURI();
        String userAgent = request.getHeader("User-Agent");
        Map<String, String> userAgentMap = userAgentUtils.parseOsAndBrowser(userAgent);
        String os = userAgentMap.get("os");
        String browser = userAgentMap.get("browser");

        // 查询用户信息
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;

        SystemLog systemLog = new SystemLog();
        systemLog.setUsername(Objects.isNull(currentUser) ? null : currentUser.getUserAccount());
        systemLog.setRequestId(requestId);
        systemLog.setIp(ip);
        systemLog.setIpSource(ipSource);
        systemLog.setUrl(url);
        systemLog.setParams(reqParam);
        systemLog.setOs(os);
        systemLog.setDescription(desc);
        systemLog.setBrowser(browser);
        systemLog.setStatus(Objects.nonNull(currentUser));
        systemLog.setLoginTime(new Date());
        systemLog.setUserAgent(userAgent);
        systemLog.setTotalTimeMillis(totalTimeMillis);
        boolean saved = save(systemLog);
        log.info("save system log end,saved: {}", saved);
        return saved;
    }

    @Override
    public LambdaQueryWrapper<SystemLog> getQueryWrapper(SystemLogQueryRequest queryRequest) {
        LambdaQueryWrapper<SystemLog> wrapper = Wrappers.lambdaQuery();
        return wrapper;
    }
}




