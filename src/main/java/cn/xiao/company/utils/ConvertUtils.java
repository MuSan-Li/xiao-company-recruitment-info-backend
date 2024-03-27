package cn.xiao.company.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对象转换
 *
 * @author xiao
 */
@Slf4j
public class ConvertUtils {

    private ConvertUtils() {
    }


    /**
     * This method takes in an object and a class and returns an object of the target class
     *
     * @param sourceClass
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> T convert(Object sourceClass, Class<T> targetClass) {
        //Uses the BeanUtil class to copy the properties of the source class to the target class
        return BeanUtil.copyProperties(sourceClass, targetClass);
    }


    /**
     * This method takes in a List of generic type F and a Class of T and returns a List of T
     *
     * @param sourceList
     * @param targetClass
     * @param <F>
     * @param <T>
     * @return
     */
    public static <F, T> List<T> convert(List<F> sourceList, Class<T> targetClass) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return new ArrayList<>();
        }
        return sourceList.stream()
                .map(source -> convert(source, targetClass))
                .collect(Collectors.toList());
    }


    /**
     * This method takes an IPage object and a Class object as parameters and returns an IPage object
     *
     * @param source
     * @param targetClass
     * @param <F>
     * @param <T>
     * @return
     */
    public static <F, T> IPage<T> convert(IPage<F> source, Class<T> targetClass) {
        IPage<T> info = new Page<>();
        List<T> records;
        if (CollUtil.isNotEmpty(source.getRecords())) {
            records = source.getRecords().stream()
                    .map(item -> convert(item, targetClass))
                    .collect(Collectors.toList());
        } else {
            records = new ArrayList<>();
        }
        BeanUtil.copyProperties(source, info, "records");
        info.setRecords(records);
        return info;
    }
}
