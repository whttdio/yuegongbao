package com.yuegongbao.ygb.regulation.service;

import java.util.List;
import com.yuegongbao.ygb.regulation.domain.YgbEmploymentRatio;
import com.yuegongbao.ygb.regulation.domain.YgbEmploymentRatioSummary;

public interface IYgbEmploymentRatioService
{
    public List<YgbEmploymentRatio> selectEmploymentRatioList(YgbEmploymentRatio employmentRatio);

    public YgbEmploymentRatioSummary selectEmploymentRatioSummary(YgbEmploymentRatio employmentRatio);

    public int calculate(String statMonth, Long employerEnterpriseId, String operator);
}

