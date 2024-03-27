package cn.xiao.company.controller;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.xiao.company.annotation.AuthCheck;
import cn.xiao.company.common.BaseResponse;
import cn.xiao.company.common.DeleteRequest;
import cn.xiao.company.common.ErrorCode;
import cn.xiao.company.common.ResultUtils;
import cn.xiao.company.constant.UserConstant;
import cn.xiao.company.exception.BusinessException;
import cn.xiao.company.exception.ThrowUtils;
import cn.xiao.company.model.dto.user.UserAddRequest;
import cn.xiao.company.model.dto.user.UserLoginRequest;
import cn.xiao.company.model.dto.user.UserQueryRequest;
import cn.xiao.company.model.dto.user.UserRegisterRequest;
import cn.xiao.company.model.dto.user.UserUpdateRequest;
import cn.xiao.company.model.entity.User;
import cn.xiao.company.model.vo.LoginUserVO;
import cn.xiao.company.model.vo.UserVO;
import cn.xiao.company.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户接口
 *
 * @author xiao
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    // region 登录相关

    /**
     * 用户注册
     *
     * @param registerRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest registerRequest) {
        if (ObjectUtil.isEmpty(registerRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = registerRequest.getUserAccount();
        String userPassword = registerRequest.getUserPassword();
        String checkPassword = registerRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param loginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest loginRequest,
                                               HttpServletRequest request) {
        if (ObjectUtil.isEmpty(loginRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = loginRequest.getUserAccount();
        String userPassword = loginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (ObjectUtil.isEmpty(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param addRequest
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest addRequest) {
        if (ObjectUtil.isEmpty(addRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验用户角色
        String account = addRequest.getUserAccount();
        if (CharSequenceUtil.isBlank(account) || (3 >= account.length() || account.length() >= 10)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户名为空或长度小于4大于10");
        }
        // 校验用户角色
        String userRole = addRequest.getUserRole();
        if (!UserConstant.DEFAULT_ROLE.equals(userRole) && !UserConstant.ADMIN_ROLE.equals(userRole)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        user.setUserAccount(addRequest.getUserAccount());
        String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + "12345678").getBytes());
        user.setUserPassword(encryptPassword);
        user.setUserName(RandomUtil.randomString(5));
        user.setUserAvatar("https://gw.alipayobjects.com/zos/antfincdn/aPkFc8Sj7n/method-draw-image.svg");
        user.setUserProfile("个人简介...");
        user.setUserRole(UserConstant.DEFAULT_ROLE);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (ObjectUtil.isEmpty(deleteRequest) || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param updateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest updateRequest) {
        if (ObjectUtil.isEmpty(updateRequest) || ObjectUtil.isEmpty(updateRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(updateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(user), ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    public BaseResponse<UserVO> getUserVOById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param queryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(queryRequest));
        return ResultUtils.success(userPage);
    }

    /**
     * 分页获取用户封装列表
     *
     * @param queryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest queryRequest) {
        if (ObjectUtil.isEmpty(queryRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(queryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    // endregion

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest
     * @param request
     * @return
     */
    // @PostMapping("/update/my")
    // public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
    //                                           HttpServletRequest request) {
    //     if (ObjectUtil.isEmpty(userUpdateMyRequest)) {
    //         throw new BusinessException(ErrorCode.PARAMS_ERROR);
    //     }
    //     User loginUser = userService.getLoginUser(request);
    //     User user = new User();
    //     BeanUtils.copyProperties(userUpdateMyRequest, user);
    //     user.setId(loginUser.getId());
    //     boolean result = userService.updateById(user);
    //     ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    //     return ResultUtils.success(true);
    // }
}
