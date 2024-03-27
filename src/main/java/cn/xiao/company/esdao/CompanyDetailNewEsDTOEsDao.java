package cn.xiao.company.esdao;


import cn.xiao.company.model.entity.es.CompanyDetailNewEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 公司详情 ES 操作
 *
 * @author xiao
 */
public interface CompanyDetailNewEsDTOEsDao
        extends ElasticsearchRepository<CompanyDetailNewEsDTO, Long> {

}