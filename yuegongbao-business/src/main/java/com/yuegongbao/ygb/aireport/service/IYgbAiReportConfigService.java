package com.yuegongbao.ygb.aireport.service;

import java.util.Date;
import java.util.List;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportConfig;
import com.yuegongbao.ygb.aireport.domain.YgbAiReportConfigSummary;

public interface IYgbAiReportConfigService
{
    List<YgbAiReportConfig> selectAiReportConfigList(YgbAiReportConfig config);

    YgbAiReportConfigSummary selectAiReportConfigSummary(YgbAiReportConfig config);

    YgbAiReportConfig selectAiReportConfigById(Long configId);

    YgbAiReportConfig selectCurrentConfig(String regionCode, Date effectiveDate);

    int saveAiReportConfig(YgbAiReportConfig config, String operator);

    int activateConfig(Long configId, String operator);
}
