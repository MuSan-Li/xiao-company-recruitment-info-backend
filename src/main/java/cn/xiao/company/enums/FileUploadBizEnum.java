package cn.xiao.company.enums;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 文件上传业务类型枚举
 *
 * @author xiao
 */
@Getter
@AllArgsConstructor
public enum FileUploadBizEnum {

    USER_AVATAR("用户头像", "user_avatar");

    private final String text;

    private final String value;

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static FileUploadBizEnum getEnumByValue(String value) {
        if (CharSequenceUtil.isBlank(value)) {
            return null;
        }
        return Arrays.stream(FileUploadBizEnum.values())
                .filter(anEnum -> anEnum.value.equals(value))
                .findFirst().orElse(null);
    }

}
