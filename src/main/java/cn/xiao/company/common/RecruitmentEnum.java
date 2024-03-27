package cn.xiao.company.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 招聘信息类型枚举
 *
 * @author xiao
 */
@Getter
@AllArgsConstructor
public enum RecruitmentEnum {

    /**
     * 官方招聘
     */
    OFFICIAL(1, "官方招聘"),

    /**
     * 校园招聘
     */
    SCHOOL(2, "校园招聘"),

    /**
     * 社会招聘
     */
    SOCIETY(3, "社会招聘"),

    /**
     * 实习生招聘
     */
    INTERN(4, "实习生招聘"),

    /**
     * 其他类型招聘
     */
    OTHER(5, "其他类型招聘"),
    ;

    /**
     * 类型索引
     */
    private Integer code;

    /**
     * 类型名称
     */
    private String desc;
}
