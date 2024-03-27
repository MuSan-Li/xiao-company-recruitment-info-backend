package cn.xiao.company.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.xiao.company.common.ErrorCode;
import cn.xiao.company.constant.CompanyConstant;
import cn.xiao.company.exception.BusinessException;
import cn.xiao.company.manager.CacheManager;
import cn.xiao.company.mapper.CompanyDetailMapper;
import cn.xiao.company.model.dto.company.CompanyQueryRequest;
import cn.xiao.company.model.entity.CompanyDetail;
import cn.xiao.company.model.entity.es.CompanyDetailNewEsDTO;
import cn.xiao.company.model.vo.CompanyDetailNewEsVO;
import cn.xiao.company.model.vo.SearchVO;
import cn.xiao.company.service.CompanyDetailService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 公司详细信息-新表服务实现类
 *
 * @author xiao
 */
@Slf4j
@Service
public class CompanyDetailServiceImpl
        extends ServiceImpl<CompanyDetailMapper, CompanyDetail> implements CompanyDetailService {

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private CacheManager cacheManager;


    /**
     * 先查询缓存 没有在从 es 获取最新动态数据 然后更新缓存
     *
     * @param queryRequest
     * @return
     */
    @Override
    public SearchVO searchEsByVOPage(CompanyQueryRequest queryRequest) {
        // 构建查询条件
        NativeSearchQuery searchQuery = buildNativeSearchQuery(queryRequest);
        // 执行查询
        String companyInfoHash = SecureUtil.md5(searchQuery.toString());
        String companyInfoKey = CompanyConstant.getCompanyInfoKey(companyInfoHash);
        Object object = cacheManager.get(companyInfoKey);
        if (Objects.nonNull(object)) {
            return (SearchVO) object;
        }
        // 从 es 获取数据
        SearchHits<CompanyDetailNewEsDTO> searchHits = elasticsearchRestTemplate
                .search(searchQuery, CompanyDetailNewEsDTO.class);
        SearchVO searchVO = new SearchVO();
        Page<CompanyDetailNewEsVO> page = new Page<>();
        List<CompanyDetailNewEsVO> resourceList = new ArrayList<>();

        if (!searchHits.hasSearchHits()) {
            searchVO.setPageVO(page);
            return searchVO;
        }

        List<SearchHit<CompanyDetailNewEsDTO>> searchHitList = searchHits.getSearchHits();
        if (ObjectUtil.isEmpty(searchHitList)) {
            searchVO.setPageVO(page);
            return searchVO;
        }

        searchHitList.stream()
                .filter(Objects::nonNull)
                .map(SearchHit::getContent)
                .forEach(content -> {
                    CompanyDetailNewEsVO companyDetailNewEsVO = new CompanyDetailNewEsVO();
                    BeanUtils.copyProperties(content, companyDetailNewEsVO);
                    resourceList.add(companyDetailNewEsVO);
                });

        cacheManager.set(companyInfoKey, resourceList, RandomUtil.randomInt(10, 20), TimeUnit.MINUTES);

        page.setRecords(resourceList);
        page.setTotal(searchHits.getTotalHits());
        searchVO.setPageVO(page);
        return searchVO;
    }

    @Override
    public List<String> esSearchCompanyInfoKey(String searchKey) {
        if (CharSequenceUtil.isBlank(searchKey) || searchKey.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        CompanyQueryRequest queryRequest = new CompanyQueryRequest();
        queryRequest.setSearchText(searchKey);
        // get redis cache
        String md5 = SecureUtil.md5(queryRequest.toString());
        String companySearchKey = CompanyConstant.getCompanySearchKey(md5);
        Object object = cacheManager.get(companySearchKey);
        if (Objects.nonNull(object)) {
            return (List<String>) object;
        }
        NativeSearchQuery nativeSearchQuery = buildNativeSearchQuery(queryRequest);
        // 使用 ElasticsearchOperations 来查询 Elasticsearch
        SearchHits<CompanyDetailNewEsDTO> searchHits = elasticsearchRestTemplate
                .search(nativeSearchQuery, CompanyDetailNewEsDTO.class);
        // 将查询结果转换为你想要的格式
        if (!searchHits.hasSearchHits()) {
            return new ArrayList<>();
        }
        Set<String> resultSet = new LinkedHashSet<>();
        List<SearchHit<CompanyDetailNewEsDTO>> hitsSearchHits = searchHits.getSearchHits();
        hitsSearchHits.stream()
                .filter(Objects::nonNull)
                .map(SearchHit::getContent)
                .forEach(content -> {
                    String companyName = content.getCompanyName();
                    if (CharSequenceUtil.isNotBlank(companyName) && companyName.contains(searchKey)) {
                        resultSet.add(companyName);
                    }
                    String category = content.getCategory();
                    if (CharSequenceUtil.isNotBlank(category) && category.contains(searchKey)) {
                        resultSet.add(category);
                    }
                    String territory = content.getTerritory();
                    if (CharSequenceUtil.isNotBlank(territory) && territory.contains(searchKey)) {
                        resultSet.add(territory);
                    }
                });
        List<String> resultList = resultSet.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // add redis cache
        cacheManager.set(companySearchKey, resultList, RandomUtil.randomInt(10, 20), TimeUnit.MINUTES);
        return resultList;
    }

    @Override
    public void validCompany(CompanyDetail companyDetail, boolean add) {
        if (ObjectUtil.isEmpty(companyDetail)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String stockCode = companyDetail.getStockCode();
        String stockName = companyDetail.getStockName();

        // 创建时，参数不能为空
        if (add && StringUtils.isBlank(stockCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 有参数则校验
        if (StringUtils.isNotBlank(stockName) && stockName.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "股票名称过长");
        }
        // TODO 补全其他校验
    }

    /**
     * 构建查询条件
     *
     * @param queryRequest
     * @return
     */
    @NotNull
    private static NativeSearchQuery buildNativeSearchQuery(CompanyQueryRequest queryRequest) {

        String searchText = queryRequest.getSearchText();

        // es 起始页为 0
        long current = queryRequest.getCurrent() - 1;
        long pageSize = queryRequest.getPageSize();
        // 过滤
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.termQuery("is_delete", 0));
        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("company_name", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("territory", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("category", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);
        // 构造查询
        return new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(pageRequest)
                .build();
    }
}

