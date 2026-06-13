package com.yuegongbao.ygb.safety.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.yuegongbao.ygb.safety.domain.YgbPreventionProject;

public interface YgbPreventionProjectMapper
{
    public List<YgbPreventionProject> selectPreventionProjectList(YgbPreventionProject preventionProject);

    public YgbPreventionProject selectPreventionProjectById(Long projectId);

    public int insertPreventionProject(YgbPreventionProject preventionProject);

    public int updatePreventionProject(YgbPreventionProject preventionProject);

    public int deletePreventionProjectByIds(@Param("projectIds") Long[] projectIds,
        @Param("updateBy") String updateBy);
}
