package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CompanyTaxRateDao {

    public int insertCompanyTaxRate(@Param("list") List<Map<String, Object>> beans) throws Exception;

    /**
     * 根据公司id删除该公司拥有的个人所得税税率信息
     *
     * @param companyId 公司id
     * @return
     * @throws Exception
     */
    public int deleteCompanyTaxRateByCompanyId(@Param("companyId") String companyId) throws Exception;

    public List<Map<String, Object>> queryCompanyTaxRateByCompanyId(@Param("companyId") String companyId) throws Exception;

    public List<Map<String, Object>> queryAllCompanyTaxRate() throws Exception;

}
