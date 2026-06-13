package com.yuegongbao.ygb.integration.stub;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.yuegongbao.ygb.domain.vo.YgbOccupationMonitorStubItem;
import com.yuegongbao.ygb.integration.OccupationClient;

/**
 * 职业病监测 Stub。
 *
 * @author yuegongbao
 */
@Service
@ConditionalOnProperty(prefix = "ygb.integration", name = "mode", havingValue = "stub", matchIfMissing = true)
public class StubOccupationClient extends AbstractYgbStubClient implements OccupationClient
{
    @Override
    public List<YgbOccupationMonitorStubItem> pullMonitorRecords(String statMonth)
    {
        return List.of(
            buildRecord(statMonth, "440106", "建筑施工", 24, 1860, 3, 4, "卫健 Stub"),
            buildRecord(statMonth, "440305", "制造加工", 18, 1420, 5, 6, "卫健 Stub"),
            buildRecord(statMonth, "440606", "平台配送", 12, 980, 4, 5, "卫健 Stub"));
    }

    private YgbOccupationMonitorStubItem buildRecord(String statMonth, String regionCode, String industryType,
        int enterpriseCount, int workerCount, int caseCount, int highRiskEnterpriseCount, String sourceChannel)
    {
        YgbOccupationMonitorStubItem item = new YgbOccupationMonitorStubItem();
        item.setStatMonth(statMonth);
        item.setRegionCode(regionCode);
        item.setIndustryType(industryType);
        item.setEnterpriseCount(enterpriseCount);
        item.setWorkerCount(workerCount);
        item.setCaseCount(caseCount);
        item.setHighRiskEnterpriseCount(highRiskEnterpriseCount);
        item.setIncidenceRate(new BigDecimal(caseCount).multiply(new BigDecimal("1000"))
            .divide(new BigDecimal(workerCount), 2, RoundingMode.HALF_UP));
        item.setSourceChannel(sourceChannel);

        Map<String, Object> payload = new HashMap<>();
        payload.put("statMonth", statMonth);
        payload.put("regionCode", regionCode);
        payload.put("industryType", industryType);
        fillStubMeta(item, "SUCCESS", "卫健职业病监测 Stub 拉取成功", payload);
        return item;
    }
}
