package cn.xiao.company;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 主类测试
 *
 * @author xiao
 */

@SpringBootTest
public class MainApplicationTests {

    @Test
    void contextLoads() {
        for (int i = 0; i < 100; i++) {
            System.out.println(new SnowflakeGenerator().next() + "-" + UUID.randomUUID());
        }
    }

    public static void main(String[] args) {
        getStrLength("12378222");
    }


    // private static long getStrLength(String str) {
    //     // 判断str 是否为空 长度是否超长
    //     if (str == null || str.length() > 100 || str.isEmpty()) {
    //         return 0;
    //     }
    //     // 递归遍历str 返回字符串长度
    //     List<String> collect = Arrays.stream(str.split("")).collect(Collectors.toList());
    //     int strings2 = getStrLength(collect);
    //     System.out.println("getStrLength:" + strings2);
    //     return strings2;
    // }
    //
    // private static int getStrLength(List<String> collect) {
    //     List<String> resultList = new ArrayList<>(collect);
    //     for (int i = 1; i < collect.size(); i++) {
    //         if (Integer.parseInt(collect.get(i)) + Integer.parseInt(collect.get(i - 1)) == 10) {
    //             resultList.remove(i);
    //             resultList.remove(i - 1);
    //             return getStrLength(resultList);
    //         }
    //     }
    //     return resultList.size();
    // }


    private static int getStrLength(String str) {
        // 判断str 是否为空 长度是否超长
        if (str == null || str.length() > 100 || str.isEmpty()) {
            return 0;
        }

        // 使用栈来处理数字
        Deque<Character> stack = new ArrayDeque<>();

        for (char c : str.toCharArray()) {
            if (!stack.isEmpty() && (Character.getNumericValue(c) + Character.getNumericValue(stack.peek()) == 10)) {
                stack.pop();
            } else {
                stack.push(c);
            }
        }
        return stack.size();
    }


}
