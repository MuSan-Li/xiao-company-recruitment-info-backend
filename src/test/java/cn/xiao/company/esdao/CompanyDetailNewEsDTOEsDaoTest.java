package cn.xiao.company.esdao;

import cn.xiao.company.MainApplicationTests;
import cn.xiao.company.model.entity.CompanyDetail;
import cn.xiao.company.model.entity.es.CompanyDetailNewEsDTO;
import cn.xiao.company.service.CompanyDetailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
class CompanyDetailNewEsDTOEsDaoTest extends MainApplicationTests {

    @Resource
    private CompanyDetailNewEsDTOEsDao companyDetailNewEsDTOEsDao;

    @Resource
    private CompanyDetailService companyDetailService;


    /**
     * 测试添加公司数据数据
     */
    @Test
    void testAddCompanyDetailData() {
        log.info("testAddCompanyDetailData-start");
        CompanyDetailNewEsDTO companyDetailNewEsDTO = new CompanyDetailNewEsDTO();
        companyDetailNewEsDTO.setId(29504L);
        companyDetailNewEsDTO.setCompanyName("平安银行股份有限公司");
        companyDetailNewEsDTO.setTerritory("广东省");
        companyDetailNewEsDTO.setCategory("银行");
        companyDetailNewEsDTO.setRecruitment("http://bank.pingan.com");
        companyDetailNewEsDTO.setCreateTime(new Date());
        companyDetailNewEsDTO.setUpdateTime(new Date());
        companyDetailNewEsDTO.setIsDelete(0);
        CompanyDetailNewEsDTO saved = companyDetailNewEsDTOEsDao.save(companyDetailNewEsDTO);
        log.info("testAddCompanyDetailData-saved:{}", saved);
        Optional<CompanyDetailNewEsDTO> companyDetailNewEsDTOEsDaoById = companyDetailNewEsDTOEsDao.findById(saved.getId());
        if (companyDetailNewEsDTOEsDaoById.isPresent()) {
            CompanyDetailNewEsDTO esDTO = companyDetailNewEsDTOEsDaoById.get();
            log.info("esDTO:{}", esDTO);
        }
        log.info("testAddCompanyDetailData-end");
    }


    /**
     * 测试添加公司数据数据
     */
    @Test
    void testDBToEsAddCompanyDetailData() {
        List<CompanyDetail> companyDetailNews = companyDetailService.list();
        List<CompanyDetailNewEsDTO> companyDetailNewEsDTOList = new ArrayList<>();
        for (CompanyDetail companyDetailNew : companyDetailNews) {
            CompanyDetailNewEsDTO companyDetailNewEsDTO = new CompanyDetailNewEsDTO();
            BeanUtils.copyProperties(companyDetailNew, companyDetailNewEsDTO);
            companyDetailNewEsDTOList.add(companyDetailNewEsDTO);
        }
        Iterable<CompanyDetailNewEsDTO> detailNewEsDTOS = companyDetailNewEsDTOEsDao.saveAll(companyDetailNewEsDTOList);
        detailNewEsDTOS.forEach(item -> log.info("testDBToEsAddCompanyDetailData-item:{}", item));
    }
}