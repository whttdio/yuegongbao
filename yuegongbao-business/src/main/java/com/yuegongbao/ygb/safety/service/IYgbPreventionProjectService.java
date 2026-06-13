package com.yuegongbao.ygb.safety.service;

import java.util.List;
import com.yuegongbao.ygb.safety.domain.YgbPreventionProject;
import com.yuegongbao.ygb.safety.domain.YgbPreventionProjectSummary;

public interface IYgbPreventionProjectService
{
    public List<YgbPreventionProject> selectPreventionProjectList(YgbPreventionProject preventionProject);

    public YgbPreventionProjectSummary selectPreventionProjectSummary(YgbPreventionProject preventionProject);

    public YgbPreventionProject selectPreventionProjectById(Long projectId);

    public int insertPreventionProject(YgbPreventionProject preventionProject);

    public int updatePreventionProject(YgbPreventionProject preventionProject);

    public int deletePreventionProjectByIds(Long[] projectIds, String updateBy);
}

