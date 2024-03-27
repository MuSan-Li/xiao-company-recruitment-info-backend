package cn.xiao.company.service;

import cn.hutool.core.util.StrUtil;
import cn.xiao.company.MainApplicationTests;
import cn.xiao.company.model.entity.CompanyDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class CompanyDetailServiceTest extends MainApplicationTests {

    @Resource
    private CompanyDetailService companyDetailService;

    @Test
    void fixData() {
        List<CompanyDetail> companyDetailList = companyDetailService.list();
        List<CompanyDetail> updateList = new ArrayList<>();
        for (CompanyDetail companyDetail : companyDetailList) {
            String formerName = companyDetail.getFormerName();
            if (StrUtil.isNotBlank(formerName)) {
                formerName = formerName.replaceAll("-&gt;", ",");
                companyDetail.setFormerName(formerName);
            }
            String companyWebsiteAddress = companyDetail.getCompanyWebsiteAddress();
            if (StrUtil.isNotBlank(companyWebsiteAddress) && companyWebsiteAddress.contains(">")) {
                companyWebsiteAddress = companyWebsiteAddress.substring(companyWebsiteAddress.indexOf(">") + 1, companyWebsiteAddress.lastIndexOf("<"));
                companyDetail.setCompanyWebsiteAddress(companyWebsiteAddress);
            }
            String controllingShareholder = companyDetail.getControllingShareholder();
            if (StrUtil.isNotBlank(controllingShareholder) && controllingShareholder.equals("控股股东： -")) {
                companyDetail.setControllingShareholder(null);
            }
            String ultimateController = companyDetail.getUltimateController();
            if (StrUtil.isNotBlank(ultimateController) && ultimateController.equals("最终控制人： -")) {
                companyDetail.setUltimateController(null);
            }
            String actualController = companyDetail.getActualController();
            if (StrUtil.isNotBlank(actualController) && actualController.equals("实际控制人： -")) {
                companyDetail.setActualController(null);
            }
            String telephone = companyDetail.getTelephone();
            if (StrUtil.isNotBlank(telephone)) {
                companyDetail.setTelephone(telephone.replaceAll(";", ",").replaceAll("86-", ""));
            }
            String fax = companyDetail.getFax();
            if (StrUtil.isNotBlank(fax)) {
                companyDetail.setFax(fax.replaceAll(";", ",").replaceAll("86-", ""));
            }
            String postcode = companyDetail.getPostcode();
            if (StrUtil.isNotBlank(postcode)) {
                companyDetail.setPostcode(postcode.replaceAll(";", ","));
            }
            String profile = companyDetail.getCompanyProfile();
            if (StrUtil.isNotBlank(profile)) {
                String profileStr = profile.replaceAll("<p>", "").replaceAll("</p>", "");
                if (profileStr.contains("查看全部▼")) {
                    profileStr = profileStr.substring(profileStr.indexOf("查看全部▼") + 1, profileStr.lastIndexOf("收起▲"));
                }
                if (profileStr.contains("看全部▼")) {
                    profileStr = profileStr.replaceAll("看全部▼", "");
                }
                if (profileStr.contains("&nbsp;")) {
                    profileStr = profileStr.replaceAll("&nbsp;", "");
                }
                if (profileStr.contains("　")) {
                    profileStr = profileStr.replaceAll("　", "");
                }
                companyDetail.setCompanyProfile(profileStr);
            }
            updateList.add(companyDetail);
        }
        log.info("updateList:{}", updateList);
        log.info("更新公司信息条数:{}", updateList.size());
        boolean updated = companyDetailService.updateBatchById(updateList);
        log.info("更新公司信息结果:{}", updated);
    }

}