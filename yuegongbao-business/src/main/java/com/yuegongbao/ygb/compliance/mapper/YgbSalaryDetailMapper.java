package com.yuegongbao.ygb.compliance.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.compliance.domain.YgbSalaryDetail;

/**
 * 工资明细Mapper接口
 *
 * @author yuegongbao
 */
public interface YgbSalaryDetailMapper
{
    public List<YgbSalaryDetail> selectSalaryDetailList(YgbSalaryDetail salaryDetail);

    public YgbSalaryDetail selectSalaryDetailById(Long detailId);

    public int insertSalaryDetail(YgbSalaryDetail salaryDetail);

    public int updateSalaryDetail(YgbSalaryDetail salaryDetail);

    public int deleteSalaryDetailByIds(@Param("detailIds") Long[] detailIds, @Param("updateBy") String updateBy);

    public int deleteByBatchId(Long batchId);

    public int updatePayStatusByBatchId(@Param("batchId") Long batchId, @Param("payStatus") String payStatus,
        @Param("updateBy") String updateBy);

    public int updatePayResultByBatchAndCardNo(@Param("batchId") Long batchId, @Param("bankAccountNo") String bankAccountNo,
        @Param("payStatus") String payStatus, @Param("failReason") String failReason, @Param("updateBy") String updateBy);

    public BigDecimal selectLatestNetAmountByPerson(@Param("personId") Long personId, @Param("statMonth") String statMonth);

    public List<YgbSalaryDetail> selectWorkerSalaryList(@Param("personId") Long personId, @Param("yearPrefix") String yearPrefix);

    public YgbSalaryDetail selectWorkerSalaryDetail(@Param("personId") Long personId, @Param("statMonth") String statMonth);
}
