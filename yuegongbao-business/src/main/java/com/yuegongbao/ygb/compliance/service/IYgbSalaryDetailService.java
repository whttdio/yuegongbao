package com.yuegongbao.ygb.compliance.service;

import java.util.List;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryDetail;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryDetailSummary;

/**
 * 工资明细服务接口。
 *
 * @author yuegongbao
 */
public interface IYgbSalaryDetailService
{
    public List<YgbSalaryDetail> selectSalaryDetailList(YgbSalaryDetail salaryDetail);

    public YgbSalaryDetailSummary selectSalaryDetailSummary(YgbSalaryDetail salaryDetail);

    public YgbSalaryDetail selectSalaryDetailById(Long detailId);

    public int updateSalaryDetail(YgbSalaryDetail salaryDetail);

    public int deleteSalaryDetailByIds(Long[] detailIds, String updateBy);
}

