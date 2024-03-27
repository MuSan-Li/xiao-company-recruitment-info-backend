package cn.xiao.company.model.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 搜索
 *
 * @author xiao
 **/
@Data
public class SearchVO implements Serializable {

    // 返回聚合对象
    private IPage pageVO;

    // 搜索建议
    private List<String> suggestionList;
}