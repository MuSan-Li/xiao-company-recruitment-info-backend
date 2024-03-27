package cn.xiao.company.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.xiao.company.MainApplicationTests;
import cn.xiao.company.common.RecruitmentEnum;
import cn.xiao.company.model.entity.Company;
import cn.xiao.company.model.entity.CompanyDetail;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
class CompanyDetailNewServiceTest extends MainApplicationTests {

    @Resource
    private CompanyService companyService;

    @Resource
    private CompanyDetailService companyDetailService;

    @Test
    void mergeData() {
        List<Company> companyList = companyService.list();
        List<CompanyDetail> companyDetailList = companyDetailService.list();
        Map<String, CompanyDetail> companyDetailMap = new HashMap<>();
        if (CollUtil.isNotEmpty(companyDetailList)) {
            companyDetailMap = companyDetailList.stream()
                    .collect(Collectors.toMap(CompanyDetail::getStockCode, t -> t, (k1, k2) -> k1));
        }
        List<CompanyDetail> companyDetailNewList = new ArrayList<>();
        for (Company company : companyList) {
            String stockCode = company.getStockCode();
            CompanyDetail companyDetail = companyDetailMap.get(stockCode);
            CompanyDetail companyDetailNew = new CompanyDetail();
            companyDetailNew.setStockCode(stockCode);
            companyDetailNew.setStockName(company.getStockName());
            companyDetailNew.setCompanyName(companyDetail.getCompanyName());
            companyDetailNew.setEnglishName(companyDetail.getEnglishName());
            companyDetailNew.setFormerName(companyDetail.getFormerName());
            companyDetailNew.setCompanyProfile(companyDetail.getCompanyProfile());
            companyDetailNew.setTimeToMarket(company.getTimeToMarket());
            companyDetailNew.setDetailInfoUrl(company.getDetailInfoUrl());
            companyDetailNew.setTerritory(companyDetail.getTerritory());
            companyDetailNew.setCategory(company.getCategory());
            companyDetailNew.setCompanyWebsiteAddress(companyDetail.getCompanyWebsiteAddress());
            companyDetailNew.setOfficeAddress(companyDetail.getOfficeAddress());
            companyDetailNew.setMainBusiness(company.getMainBusiness());
            companyDetailNew.setProductName(companyDetail.getProductName());
            companyDetailNew.setControllingShareholder(companyDetail.getControllingShareholder());
            companyDetailNew.setActualController(companyDetail.getActualController());
            companyDetailNew.setUltimateController(companyDetail.getUltimateController());
            companyDetailNew.setCorporateRepresentative(companyDetail.getCorporateRepresentative());
            companyDetailNew.setChairmanOfTheBoard(companyDetail.getChairmanOfTheBoard());
            companyDetailNew.setSecretaryToTheBoard(companyDetail.getSecretaryToTheBoard());
            companyDetailNew.setGeneralManager(companyDetail.getGeneralManager());
            companyDetailNew.setRegisteredCapital(companyDetail.getRegisteredCapital());
            companyDetailNew.setNumberOfEmployees(companyDetail.getNumberOfEmployees());
            companyDetailNew.setTelephone(companyDetail.getTelephone());
            companyDetailNew.setFax(companyDetail.getFax());
            companyDetailNewList.add(companyDetailNew);
        }
        boolean saved = companyDetailService.saveBatch(companyDetailNewList);
        log.info("保存结果:{}", saved);
    }


    @Test
    void addRecruitmentInfo() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 创建一个固定大小为3的线程池
        ExecutorService executor = new ThreadPoolExecutor(10,
                20, 60,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(20000));

        LambdaQueryWrapper<CompanyDetail> wrapper = Wrappers.lambdaQuery();
        wrapper.isNull(CompanyDetail::getRecruitment);
        wrapper.isNotNull(CompanyDetail::getCompanyWebsiteAddress);
        List<CompanyDetail> list = companyDetailService.list(wrapper);

        List<List<CompanyDetail>> partitionList = ListUtil.partition(list, 10);


        // 设置 EdgeDriver 的路径
        System.setProperty("webdriver.edge.driver", "D:\\Dev\\edgedriver_win32\\msedgedriver.exe");

        // 创建 EdgeOptions 对象
        EdgeOptions options = new EdgeOptions();
        // 初始化 EdgeDriver，并配置为无头模式（不显示浏览器窗口）
        options.addArguments("--headless");
        //解决 403 出错问题
        options.addArguments("--remote-allow-origins=*");

        // 设置用户代理
        options.setCapability("userAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

        // 设置窗口大小
        options.setCapability("window-size", "1200,800");


        for (List<CompanyDetail> companyDetails : partitionList) {

            for (CompanyDetail companyDetail : companyDetails) {

                TimeUnit.SECONDS.sleep(1);

                Future<?> submit = executor.submit(() -> {
                    log.info("Task " + companyDetail.getCompanyWebsiteAddress() + " is running on thread: " + Thread.currentThread().getName());
                    String companyWebSiteAddr = companyDetail.getCompanyWebsiteAddress();
                    log.info("companyWebSiteAddr :{}", companyWebSiteAddr);
                    WebDriver driver = new EdgeDriver(options);
                    Document document = getData(companyWebSiteAddr, driver);
                    // log.info("document:{}", document);
                    if (Objects.nonNull(document)) {
                        // 人才招聘
                        Elements elements = document.select(":contains(人才招聘)");
                        extracted(companyDetail, elements, RecruitmentEnum.OFFICIAL);
                        elements = document.select(":contains(人力资源)");
                        extracted(companyDetail, elements, RecruitmentEnum.OFFICIAL);
                        elements = document.select(":contains(人才发展)");
                        extracted(companyDetail, elements, RecruitmentEnum.OFFICIAL);
                        elements = document.select(":contains(加入)");
                        extracted(companyDetail, elements, RecruitmentEnum.OFFICIAL);
                        elements = document.select(":contains(招贤纳士)");
                        extracted(companyDetail, elements, RecruitmentEnum.OFFICIAL);
                        elements = document.select(":contains(招聘信息)");
                        extracted(companyDetail, elements, RecruitmentEnum.OFFICIAL);

                        elements = document.select(":contains(校园招聘)");
                        extracted(companyDetail, elements, RecruitmentEnum.SCHOOL);
                        elements = document.select(":contains(社会招聘)");
                        extracted(companyDetail, elements, RecruitmentEnum.SOCIETY);

                        if (StrUtil.isNotBlank(companyDetail.getRecruitment())) {
                            log.warn("id:{},recruitment:{}", companyDetail.getId(), companyDetail.getRecruitment());
                            boolean updated = companyDetailService.updateById(companyDetail);
                            log.info("updated:{}", updated);
                        } else {
                            log.warn("recruitment is null id:{}", companyDetail.getId());
                            companyDetail.setRecruitment("recruitment is null");
                            boolean updated = companyDetailService.updateById(companyDetail);
                            log.info("updated:{}", updated);
                        }
                    } else {
                        log.warn("document is null id:{}", companyDetail.getId());
                        companyDetail.setRecruitment("document is null");
                        boolean updated = companyDetailService.updateById(companyDetail);
                        log.info("updated:{}", updated);
                    }
                    driver.close();
                    driver.quit();
                    System.out.println("Task " + companyDetail.getCompanyWebsiteAddress() + " is completed");
                });
            }
        }
        // 关闭线程池
        executor.shutdown();
        stopWatch.stop();
        log.info("总耗时：{}", stopWatch.getTotalTimeSeconds());
    }

    @Test
    void addRecruitmentInfo2() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        LambdaQueryWrapper<CompanyDetail> wrapper = Wrappers.lambdaQuery();
        wrapper.isNull(CompanyDetail::getRecruitment);
        wrapper.isNotNull(CompanyDetail::getCompanyWebsiteAddress);
        List<CompanyDetail> companyDetailList = companyDetailService.list(wrapper);

        for (CompanyDetail companyDetail : companyDetailList) {
            String companyWebSiteAddr = companyDetail.getCompanyWebsiteAddress();
            log.info("companyWebSiteAddr :{}", companyWebSiteAddr);
            Document document = getData(companyWebSiteAddr);
            if (Objects.isNull(document)) {
                log.error("document is null, id:{}, companyWebSiteAddr :{}",
                        companyDetail.getId(), companyWebSiteAddr);
                continue;
            }
            // 人才招聘
            Elements elements = document.select(":contains(人才招聘)");
            extracted(companyDetail, elements, RecruitmentEnum.OFFICIAL);
            elements = document.select(":contains(人力资源)");
            extracted(companyDetail, elements, RecruitmentEnum.OFFICIAL);
            elements = document.select(":contains(加入)");
            extracted(companyDetail, elements, RecruitmentEnum.OFFICIAL);
            elements = document.select(":contains(招贤纳士)");
            extracted(companyDetail, elements, RecruitmentEnum.OFFICIAL);
            elements = document.select(":contains(校园招聘)");
            extracted(companyDetail, elements, RecruitmentEnum.SCHOOL);
            elements = document.select(":contains(社会招聘)");
            extracted(companyDetail, elements, RecruitmentEnum.SOCIETY);

            if (StrUtil.isNotBlank(companyDetail.getRecruitment())) {
                log.warn("id:{},recruitment:{}", companyDetail.getId(), companyDetail.getRecruitment());
                boolean updated = companyDetailService.updateById(companyDetail);
                log.info("updated:{}", updated);
            }
            killProcessByName("msedge.exe");
            killProcessByName("msedgedriver.exe");
        }
        stopWatch.stop();
        log.info("总耗时：{}", stopWatch.getTotalTimeSeconds());
    }


    public static void killProcessByName(String processName) {
        try {
            // 构建taskkill命令
            String cmd = "taskkill /IM " + processName + " /F";
            // 执行命令
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            // 读取命令的输出信息
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 检查命令是否执行成功
            if (process.exitValue() == 0) {
                System.out.println("Process \"" + processName + "\" has been terminated.");
            } else {
                System.out.println("Could not terminate process \"" + processName + "\".");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void extracted(CompanyDetail companyDetail, Elements elements, RecruitmentEnum recruitmentEnum) {
        if (Objects.isNull(elements)) {
            return;
        }
        for (Element element : elements) {
            if (element.tagName().equals("a")) {
                // 这个元素是一个 <a> 标签
                String href = element.attr("href");
                System.out.println("Href attribute value: " + href);
                String recruitment = companyDetail.getRecruitment();
                companyDetail.setRecruitment("," + recruitmentEnum.getCode() + "--" +
                        href + "," + recruitment + ",");
            }
        }
    }


    /**
     * 修复数据
     */
    @Test
    void fixData() {
        LambdaQueryWrapper<CompanyDetail> wrapper = Wrappers.lambdaQuery();
        wrapper.ne(CompanyDetail::getRecruitment, "recruitment is null")
                .ne(CompanyDetail::getRecruitment, "document is null")
                .isNotNull(CompanyDetail::getRecruitment)
                .isNotNull(CompanyDetail::getCompanyWebsiteAddress);
        List<CompanyDetail> list = companyDetailService.list(wrapper);
        list = list.stream()
                .filter(item -> StrUtil.isNotBlank(item.getRecruitment()))
                .collect(Collectors.toList());
        List<CompanyDetail> resultList = new ArrayList<>();
        List<String> stockCodeList = new ArrayList<>();
        for (CompanyDetail companyDetail : list) {
            log.info("companyDetail:{}", companyDetail);
            String recruitment = companyDetail.getRecruitment();
            String companyWebsiteAddress = companyDetail.getCompanyWebsiteAddress();
            Set<String> collect = Arrays.stream(recruitment.split(","))
                    .filter(Objects::nonNull)
                    .distinct()
                    .filter(item -> !"null".contains(item))
                    .collect(Collectors.toSet());
            log.info("collect:{}", collect.size());

            stockCodeList.add("002197");
            stockCodeList.add("002763");
            stockCodeList.add("300108");
            stockCodeList.add("300279");
            stockCodeList.add("600077");
            stockCodeList.add("600520");
            stockCodeList.add("600662");

            if (stockCodeList.contains(companyDetail.getStockCode())) {
                continue;
            }

            StringBuilder resultStr = new StringBuilder();
            for (String str : collect) {
                if (!str.contains(companyWebsiteAddress) && !str.contains("http")) {
                    String substring = str.substring(0, 3);
                    String substring1 = str.substring(3);
                    resultStr.append(substring)
                            .append(companyWebsiteAddress)
                            .append(substring1)
                            .append(",");
                } else {
                    resultStr.append(str).append(",");
                }
            }
            companyDetail.setRecruitment(resultStr.toString());
            // String companyWebsiteAddress = companyDetail.getCompanyWebsiteAddress();
            // List<String> collect = Arrays.stream(companyWebsiteAddress.split(" ")).collect(Collectors.toList());
            // companyDetail.setCompanyWebsiteAddress(collect.get(0));
            resultList.add(companyDetail);
        }
        log.info("resultList:{}", resultList);
        companyDetailService.updateBatchById(resultList);
    }


    /**
     * 获取招聘相关数据
     *
     * @param url
     * @return
     */
    private static Document getData(String url) {
        log.info("url:{}", url);

        // 设置 EdgeDriver 的路径
        System.setProperty("webdriver.edge.driver", "D:\\Dev\\edgedriver_win32\\msedgedriver.exe");

        // 创建 EdgeOptions 对象
        EdgeOptions options = new EdgeOptions();
        // 初始化 EdgeDriver，并配置为无头模式（不显示浏览器窗口）
        options.addArguments("--headless");
        //解决 403 出错问题
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver = new EdgeDriver(options);
        try {
            // 使用 WebDriver 访问网站
            driver.get(url);
            // 获取页面源码
            String pageSource = driver.getPageSource();
            // 使用 Jsoup 解析页面源码
            return Jsoup.parse(pageSource);
        } catch (WebDriverException e) {
            log.error("获取页面源码失败,:{}", e.getMessage());
        } finally {
            driver.close();
            // 关闭 WebDriver
            driver.quit();
        }
        return null;
    }

    /**
     * 获取招聘相关数据
     *
     * @param url
     * @return
     */
    private static Document getData(String url, WebDriver driver) {
        log.info("url:{}", url);
        try {
            // 使用 WebDriver 访问网站
            driver.get(url);
            // 获取页面源码
            String pageSource = driver.getPageSource();
            // 使用 Jsoup 解析页面源码
            return Jsoup.parse(pageSource);
        } catch (WebDriverException e) {
            log.error("获取页面源码失败,:{}", e.getMessage());
        }
        return null;
    }
}

