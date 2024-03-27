package cn.xiao.company.crawler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.xiao.company.MainApplicationTests;
import cn.xiao.company.model.entity.Company;
import cn.xiao.company.model.entity.CompanyDetail;
import cn.xiao.company.service.CompanyDetailService;
import cn.xiao.company.service.CompanyService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 爬虫测试
 *
 * @author xiao
 */
@Slf4j
class CrawlerTests extends MainApplicationTests {

    @Resource
    private CompanyService companyService;

    @Resource
    private CompanyDetailService companyDetailService;

    private static Node getNode(Node node, int index) {
        if (index < 0) {
            return null;
        }
        List<Node> nodeList = node.childNodes().get(index).childNodes();
        return CollUtil.isEmpty(nodeList) ? null : nodeList.get(0);
    }

    /**
     * 获取公司基本数据 中商情报网
     * <a href="https://s.askci.com">...</a>
     */
    @Test
    void testGetCompanyInfo() throws IOException, InterruptedException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int index = 615;
        int min = 1000;
        int max = 5000;
        Random random = new Random();

        for (int i = 487; i <= index; i++) {
            int valNum = random.nextInt(max - min + 1000) + min;
            log.info("valNum: {}", valNum);
            Thread.sleep(valNum);
            // https://s.askci.com/stock/0-0-0/%d/
            // https://s.askci.com/stock/xsb-0-0/2/
            String url = String.format("https://s.askci.com/stock/0-0-0/%d/", i);
            Document document = Jsoup.connect(url).get();
            Element resultUl = document.getElementById("ResultUl");
            log.info("resultUl:{}", resultUl);
            if (Objects.isNull(resultUl)) {
                continue;
            }
            List<Node> nodes = resultUl.childNodes();
            nodes = nodes.stream()
                    .filter(item -> item.childNodeSize() > 0)
                    .collect(Collectors.toList());
            log.info("nodes:{}", nodes);
            List<Company> companies = new ArrayList<>();
            for (Node node : nodes) {
                Company company = new Company();
                // 索引
                // Optional.ofNullable(getNode(node, 1))
                //         .ifPresent(val -> company.setId(Long.parseLong(val.outerHtml())));
                // 股票代码
                Optional.ofNullable(getNode(node, 3))
                        .ifPresent(item -> company.setStockCode(item.childNode(0).outerHtml()));
                // 股票名称
                Optional.ofNullable(getNode(node, 5))
                        .filter(item -> item.childNodeSize() > 0)
                        .flatMap(item -> Optional.of(item.childNode(0)))
                        .ifPresent(val -> company.setStockName(val.outerHtml()));

                // 公司名称
                Optional.ofNullable(getNode(node, 7))
                        .ifPresent(item -> company.setCompanyName(item.outerHtml()));
                // 上市时间
                Optional.ofNullable(getNode(node, 9))
                        .ifPresent(item -> {
                            try {
                                company.setTimeToMarket(dateFormat.parse(item.outerHtml()));
                            } catch (ParseException e) {
                                log.error("时间转换异常,item=> {},e=> ", item, e);
                            }
                        });
                // 招股书
                Optional.ofNullable(getNode(node, 11))
                        .ifPresent(item -> company.setProspectus(item.attr("href")));
                // 公司财报
                Optional.ofNullable(getNode(node, 13))
                        .ifPresent(item -> company.setFinancialReport(item.attr("href")));
                // 分类
                Optional.ofNullable(getNode(node, 15))
                        .ifPresent(item -> company.setCategory(item.outerHtml()));
                // 主营业务
                Optional.ofNullable(getNode(node, 17))
                        .ifPresent(item -> company.setMainBusiness(item.outerHtml()));
                companies.add(company);
            }
            boolean saved = companyService.saveBatch(companies);
            log.info("保存成功: {}", saved);
        }
    }

    /**
     * 获取公司详情数据 中商情报网
     * <a href="https://s.askci.com/stock/summary/000001/">...</a>
     */
    @Test
    void testGetCompanyDetailInfo() throws InterruptedException, IOException {
        int min = 3000;
        int max = 6000;
        Random random = new Random();
        List<Company> companies = companyService.list();
        Map<String, String> map = new HashMap<>();
        for (Company company : companies) {
            log.info("company: {}", company);
            LambdaQueryWrapper<CompanyDetail> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(CompanyDetail::getStockCode, company.getStockCode());
            CompanyDetail companyDetail = companyDetailService.getOne(wrapper);
            if (Objects.nonNull(companyDetail)) {
                continue;
            }
            int valNum = random.nextInt(max - min + 3000) + min;
            log.info("valNum: {}", valNum);
            Thread.sleep(valNum);

            Document document = Jsoup.connect(company.getDetailInfoUrl()).get();
            // log.info("document:{}", document);
            Elements elements = document.select("tbody");
            // log.info("elements:{}", elements);
            Element element = elements.get(0);
            List<Node> nodes = element.childNodes();
            for (Node item : nodes) {
                if (Objects.isNull(item)) {
                    continue;
                }
                List<Node> nodeList = item.childNodes();
                if (CollUtil.isEmpty(nodeList)) {
                    continue;
                }
                Node nodeKey = nodeList.get(1);
                Node nodeValue = nodeList.get(3);
                if (ObjUtil.hasNull(nodeKey, nodeValue)) {
                    continue;
                }
                // log.info("nodeKey: {}", nodeKey);
                // log.info("nodeValue:{}", nodeValue);
                String key = Optional.of(nodeKey.childNode(0).outerHtml()).orElse("");
                if (nodeValue.childNodeSize() <= 0) {
                    continue;
                }
                String value = Optional.of(nodeValue.childNode(0).outerHtml()).orElse("");
                map.put(key, value);
            }
            saveCompanyDetailInfo(company, map);
            map.clear();
        }
    }

    private void saveCompanyDetailInfo(Company company, Map<String, String> map) {
        CompanyDetail companyDetail = new CompanyDetail();
        companyDetail.setStockCode(company.getStockCode());
        companyDetail.setCompanyName(map.getOrDefault("公司名称：", null));
        companyDetail.setEnglishName(map.getOrDefault("英文名称：", null));
        companyDetail.setFormerName(map.getOrDefault("曾 用 名：", null));
        companyDetail.setTerritory(map.getOrDefault("所属地域：", null));
        companyDetail.setCategory(map.getOrDefault("所属行业：", null));
        companyDetail.setCompanyWebsiteAddress(map.getOrDefault("公司网址：", null));
        companyDetail.setMainBusiness(map.getOrDefault("主营业务：", null));
        companyDetail.setProductName(map.getOrDefault("产品名称：", null));
        companyDetail.setControllingShareholder(map.getOrDefault("控股股东：", null));
        companyDetail.setActualController(map.getOrDefault("实际控制人：", null));
        companyDetail.setUltimateController(map.getOrDefault("最终控制人：", null));
        companyDetail.setChairmanOfTheBoard(map.getOrDefault("董 事 长：", null));
        companyDetail.setSecretaryToTheBoard(map.getOrDefault("董　　秘：", null));
        companyDetail.setCorporateRepresentative(map.getOrDefault("法人代表：", null));
        companyDetail.setGeneralManager(map.getOrDefault("总 经 理：", null));
        companyDetail.setRegisteredCapital(map.getOrDefault("注册资金：", null));
        companyDetail.setNumberOfEmployees(Integer.parseInt(map.getOrDefault("员工人数：", null)));
        companyDetail.setTelephone(map.getOrDefault("电　　话：", null));
        companyDetail.setFax(map.getOrDefault("传　　真：", null));
        companyDetail.setPostcode(map.getOrDefault("邮　　编：", null));
        companyDetail.setOfficeAddress(map.getOrDefault("办公地址：", null));
        companyDetail.setCompanyProfile(map.getOrDefault("公司简介：", null));
        boolean saved = companyDetailService.save(companyDetail);
        log.info("保存结果：{}", saved);
    }
}
