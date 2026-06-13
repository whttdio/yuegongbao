package com.yuegongbao.ygb.worker.service;

import java.util.List;
import java.util.Map;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.worker.domain.WorkerTrainingProgress;
import com.yuegongbao.ygb.worker.domain.WorkerTrainingQuestion;

public interface WorkerTrainingService
{
    WorkerTrainingProgress getProgress(YgbPerson worker);

    List<WorkerTrainingQuestion> listQuestions(YgbPerson worker);

    Map<String, Object> answerQuestion(YgbPerson worker, String questionId, Integer answerIndex);

    Map<String, Object> getHistory(YgbPerson worker);

    Map<String, Object> getCourses(YgbPerson worker);

    Map<String, Object> getCourseDetail(YgbPerson worker, String courseKey);

    Map<String, Object> saveStudyProgress(YgbPerson worker, String courseKey, Integer studiedSeconds);
}
