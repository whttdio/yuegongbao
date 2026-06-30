package com.yuegongbao.ygb.safety.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.ygb.foundation.domain.YgbEnterprise;
import com.yuegongbao.ygb.foundation.mapper.YgbEnterpriseMapper;
import com.yuegongbao.ygb.safety.domain.YgbPreventionProject;
import com.yuegongbao.ygb.safety.mapper.YgbPreventionProjectMapper;
import com.yuegongbao.ygb.util.YgbDataScopeGuard;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YgbPreventionProjectServiceImplTest
{
    @Mock
    private YgbPreventionProjectMapper preventionProjectMapper;

    @Mock
    private YgbEnterpriseMapper enterpriseMapper;

    @Mock
    private YgbDataScopeGuard dataScopeGuard;

    @InjectMocks
    private YgbPreventionProjectServiceImpl service;

    @Test
    void insertPreventionProjectMustStartFromDraft()
    {
        YgbPreventionProject project = project("1");
        when(enterpriseMapper.selectEnterpriseById(10L)).thenReturn(enterprise());

        assertThrows(ServiceException.class, () -> service.insertPreventionProject(project));

        verify(preventionProjectMapper, never()).insertPreventionProject(any(YgbPreventionProject.class));
    }

    @Test
    void updatePreventionProjectRejectsSkippedStatus()
    {
        YgbPreventionProject previous = project("0");
        previous.setProjectId(8L);
        YgbPreventionProject target = project("2");
        target.setProjectId(8L);
        target.setBudgetAmount(new BigDecimal("10000"));
        target.setStartDate(new Date());
        target.setEndDate(new Date());
        when(preventionProjectMapper.selectPreventionProjectById(8L)).thenReturn(previous);
        when(enterpriseMapper.selectEnterpriseById(10L)).thenReturn(enterprise());

        assertThrows(ServiceException.class, () -> service.updatePreventionProject(target));

        verify(preventionProjectMapper, never()).updatePreventionProject(any(YgbPreventionProject.class));
    }

    @Test
    void deletePreventionProjectRejectsActiveProject()
    {
        YgbPreventionProject previous = project("1");
        previous.setProjectId(8L);
        when(preventionProjectMapper.selectPreventionProjectById(8L)).thenReturn(previous);

        assertThrows(ServiceException.class, () -> service.deletePreventionProjectByIds(new Long[] { 8L }, "tester"));

        verify(preventionProjectMapper, never()).deletePreventionProjectByIds(any(Long[].class), any());
    }

    @Test
    void updatePreventionProjectAllowsNextStatus()
    {
        YgbPreventionProject previous = project("0");
        previous.setProjectId(8L);
        YgbPreventionProject target = project("1");
        target.setProjectId(8L);
        target.setBudgetAmount(new BigDecimal("10000"));
        when(preventionProjectMapper.selectPreventionProjectById(8L)).thenReturn(previous);
        when(enterpriseMapper.selectEnterpriseById(10L)).thenReturn(enterprise());
        when(preventionProjectMapper.updatePreventionProject(any(YgbPreventionProject.class))).thenReturn(1);

        int rows = service.updatePreventionProject(target);

        assertEquals(1, rows);
        verify(preventionProjectMapper).updatePreventionProject(target);
    }

    private YgbPreventionProject project(String status)
    {
        YgbPreventionProject project = new YgbPreventionProject();
        project.setProjectName("预防项目");
        project.setProjectType("1");
        project.setEnterpriseId(10L);
        project.setProjectStatus(status);
        project.setBudgetAmount(BigDecimal.ZERO);
        project.setActualAmount(BigDecimal.ZERO);
        return project;
    }

    private YgbEnterprise enterprise()
    {
        YgbEnterprise enterprise = new YgbEnterprise();
        enterprise.setEnterpriseId(10L);
        enterprise.setEnterpriseName("ENT-10");
        enterprise.setRegionCode("440100");
        return enterprise;
    }
}
