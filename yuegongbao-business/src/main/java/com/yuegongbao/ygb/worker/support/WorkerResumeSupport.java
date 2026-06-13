package com.yuegongbao.ygb.worker.support;

import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.ygb.worker.domain.WorkerResume;
import com.yuegongbao.ygb.worker.domain.vo.WorkerResumeSaveRequest;

public final class WorkerResumeSupport
{
    private WorkerResumeSupport()
    {
    }

    public static void assertResumeRequestComplete(WorkerResumeSaveRequest request)
    {
        if (request == null || !isResumeComplete(request.getExpectedJob(), request.getIntro()))
        {
            throw new ServiceException("简历信息不完整。");
        }
    }

    public static void assertResumeReadyForApply(WorkerResume resume)
    {
        if (resume == null || !isResumeComplete(resume.getExpectedJob(), resume.getIntro()))
        {
            throw new ServiceException("请先完善简历后再投递岗位。");
        }
    }

    private static boolean isResumeComplete(String expectedJob, String intro)
    {
        return StringUtils.isNotEmpty(expectedJob) && StringUtils.isNotEmpty(intro);
    }
}
