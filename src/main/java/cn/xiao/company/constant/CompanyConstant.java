package cn.xiao.company.constant;

/**
 * 公司常量
 *
 * @author xiao
 */
public interface CompanyConstant {

    /**
     * 公司信息_KEY
     */
    String COMPANY_INFO_KEY = "company_info_key_";


    /**
     * 公司搜索_KEY
     */
    String COMPANY_SEARCH_KEY = "company_search_key_";



    static String getCompanyInfoKey(String companyInfoHash) {
        return COMPANY_INFO_KEY + companyInfoHash;
    }

    static String getCompanySearchKey(String companySearchHash) {
        return COMPANY_SEARCH_KEY + companySearchHash;
    }

}
