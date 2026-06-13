/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSON
 *  com.alibaba.fastjson2.JSONObject
 *  com.yuegongbao.common.core.domain.entity.SysUser
 *  com.yuegongbao.common.exception.ServiceException
 *  com.yuegongbao.common.utils.StringUtils
 *  com.yuegongbao.system.domain.SysNotice
 *  com.yuegongbao.system.service.ISysNoticeReadService
 *  com.yuegongbao.system.service.ISysNoticeService
 *  com.yuegongbao.ygb.foundation.domain.YgbPerson
 *  com.yuegongbao.ygb.portal.domain.YgbPortalContent
 *  com.yuegongbao.ygb.portal.mapper.YgbPortalContentMapper
 *  com.yuegongbao.ygb.worker.domain.WorkerComplaint
 *  com.yuegongbao.ygb.worker.domain.WorkerLegalConsult
 *  com.yuegongbao.ygb.worker.domain.WorkerNoticeMessage
 *  com.yuegongbao.ygb.worker.domain.WorkerSetting
 *  com.yuegongbao.ygb.worker.domain.vo.WorkerComplaintCreateRequest
 *  com.yuegongbao.ygb.worker.domain.vo.WorkerLegalConsultCreateRequest
 *  com.yuegongbao.ygb.worker.mapper.WorkerComplaintMapper
 *  com.yuegongbao.ygb.worker.mapper.WorkerLegalConsultMapper
 *  com.yuegongbao.ygb.worker.mapper.WorkerNoticeMessageMapper
 *  com.yuegongbao.ygb.worker.mapper.WorkerProfileMapper
 *  com.yuegongbao.ygb.worker.service.WorkerMessageService
 *  com.yuegongbao.ygb.worker.service.WorkerPushGatewayService
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Service
 */
package com.yuegongbao.ygb.worker.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yuegongbao.common.core.domain.entity.SysUser;
import com.yuegongbao.common.exception.ServiceException;
import com.yuegongbao.common.utils.StringUtils;
import com.yuegongbao.system.domain.SysNotice;
import com.yuegongbao.system.service.ISysNoticeReadService;
import com.yuegongbao.system.service.ISysNoticeService;
import com.yuegongbao.ygb.foundation.domain.YgbPerson;
import com.yuegongbao.ygb.portal.domain.YgbPortalContent;
import com.yuegongbao.ygb.portal.mapper.YgbPortalContentMapper;
import com.yuegongbao.ygb.worker.domain.WorkerComplaint;
import com.yuegongbao.ygb.worker.domain.WorkerLegalConsult;
import com.yuegongbao.ygb.worker.domain.WorkerNoticeMessage;
import com.yuegongbao.ygb.worker.domain.WorkerSetting;
import com.yuegongbao.ygb.worker.domain.vo.WorkerComplaintCreateRequest;
import com.yuegongbao.ygb.worker.domain.vo.WorkerLegalConsultCreateRequest;
import com.yuegongbao.ygb.worker.mapper.WorkerComplaintMapper;
import com.yuegongbao.ygb.worker.mapper.WorkerLegalConsultMapper;
import com.yuegongbao.ygb.worker.mapper.WorkerNoticeMessageMapper;
import com.yuegongbao.ygb.worker.mapper.WorkerProfileMapper;
import com.yuegongbao.ygb.worker.service.WorkerMessageService;
import com.yuegongbao.ygb.worker.service.WorkerPushGatewayService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkerMessageServiceImpl
implements WorkerMessageService {
    private static final Logger log = LoggerFactory.getLogger(WorkerMessageServiceImpl.class);
    private static final String WORKER_PORTAL_CODE = "ygb";
    private static final String CMS_KEY_PREFIX = "cms-";
    @Autowired
    private WorkerComplaintMapper workerComplaintMapper;
    @Autowired
    private WorkerLegalConsultMapper workerLegalConsultMapper;
    @Autowired
    private WorkerNoticeMessageMapper workerNoticeMessageMapper;
    @Autowired
    private WorkerProfileMapper workerProfileMapper;
    @Autowired
    private ISysNoticeService sysNoticeService;
    @Autowired
    private ISysNoticeReadService sysNoticeReadService;
    @Autowired
    private WorkerPushGatewayService workerPushGatewayService;
    @Autowired
    private YgbPortalContentMapper portalContentMapper;

    public Map<String, Object> createComplaint(YgbPerson worker, SysUser user, WorkerComplaintCreateRequest request) {
        this.validateComplaintRequest(request);
        WorkerComplaint complaint = new WorkerComplaint();
        complaint.setUserId(user.getUserId());
        complaint.setPersonId(worker.getPersonId());
        complaint.setPersonName(worker.getPersonName());
        complaint.setEnterpriseId(worker.getEnterpriseId());
        complaint.setEnterpriseName(worker.getEnterpriseName());
        complaint.setComplaintType(request.getComplaintType());
        complaint.setTitle(request.getTitle());
        complaint.setContent(request.getContent());
        complaint.setContactMobile(this.firstNonBlank(request.getContactMobile(), worker.getMobile(), user.getPhonenumber()));
        complaint.setAnonymousFlag(Boolean.TRUE.equals(request.getAnonymous()) ? "1" : "0");
        complaint.setSyncUnionFlag(Boolean.TRUE.equals(request.getSyncUnion()) ? "1" : "0");
        complaint.setAttachments(request.getAttachments());
        complaint.setStatus("0");
        complaint.setReplyContent(null);
        complaint.setHandleTimeText(null);
        complaint.setCreateBy(user.getUserName());
        this.workerComplaintMapper.insertWorkerComplaint(complaint);
        this.tryInsertComplaintNoticeMessage(worker, user, complaint);
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("complaintId", complaint.getComplaintId());
        result.put("status", complaint.getStatus());
        result.put("syncUnion", "1".equals(complaint.getSyncUnionFlag()));
        result.put("syncUnionText", this.yesNoText(complaint.getSyncUnionFlag(), "\u5df2\u540c\u6b65\u5de5\u4f1a", "\u672a\u540c\u6b65\u5de5\u4f1a"));
        result.put("submitTime", complaint.getCreateTime());
        result.put("pushTriggered", this.trySendComplaintCreatedPush(worker, user, complaint));
        return result;
    }

    public Map<String, Object> listComplaints(Long userId, String status) {
        List list = this.workerComplaintMapper.selectWorkerComplaintList(userId, status);
        ArrayList rows = new ArrayList();
        for (WorkerComplaint item : list) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("complaintId", item.getComplaintId());
            row.put("complaintType", item.getComplaintType());
            row.put("title", item.getTitle());
            row.put("status", item.getStatus());
            row.put("statusText", this.complaintStatusText(item.getStatus()));
            row.put("syncUnion", "1".equals(item.getSyncUnionFlag()));
            row.put("syncUnionText", this.yesNoText(item.getSyncUnionFlag(), "\u5df2\u540c\u6b65\u5de5\u4f1a", "\u672a\u540c\u6b65\u5de5\u4f1a"));
            row.put("submitTime", item.getCreateTime());
            rows.add(row);
        }
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("total", rows.size());
        result.put("rows", rows);
        return result;
    }

    public Map<String, Object> getComplaintDetail(Long userId, Long complaintId) {
        WorkerComplaint item = this.workerComplaintMapper.selectWorkerComplaintById(complaintId, userId);
        if (item == null) {
            throw new ServiceException("\u672a\u627e\u5230\u6295\u8bc9\u8bb0\u5f55\u3002");
        }
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("complaintId", item.getComplaintId());
        result.put("complaintType", item.getComplaintType());
        result.put("title", item.getTitle());
        result.put("content", item.getContent());
        result.put("attachments", item.getAttachments());
        result.put("status", item.getStatus());
        result.put("statusText", this.complaintStatusText(item.getStatus()));
        result.put("anonymous", "1".equals(item.getAnonymousFlag()));
        result.put("anonymousText", this.yesNoText(item.getAnonymousFlag(), "\u533f\u540d\u63d0\u4ea4", "\u5b9e\u540d\u63d0\u4ea4"));
        result.put("syncUnion", "1".equals(item.getSyncUnionFlag()));
        result.put("syncUnionText", this.yesNoText(item.getSyncUnionFlag(), "\u5df2\u540c\u6b65\u5de5\u4f1a", "\u672a\u540c\u6b65\u5de5\u4f1a"));
        result.put("replyContent", item.getReplyContent());
        result.put("submitTime", item.getCreateTime());
        result.put("handleTime", item.getHandleTimeText());
        return result;
    }

    public Map<String, Object> createLegalConsult(YgbPerson worker, SysUser user, WorkerLegalConsultCreateRequest request) {
        this.validateLegalConsultRequest(request);
        WorkerLegalConsult consult = new WorkerLegalConsult();
        consult.setUserId(user.getUserId());
        consult.setPersonId(worker.getPersonId());
        consult.setPersonName(worker.getPersonName());
        consult.setEnterpriseId(worker.getEnterpriseId());
        consult.setEnterpriseName(worker.getEnterpriseName());
        consult.setConsultType(request.getConsultType());
        consult.setTitle(request.getTitle());
        consult.setContent(request.getContent());
        consult.setContactMobile(this.firstNonBlank(request.getContactMobile(), worker.getMobile(), user.getPhonenumber()));
        consult.setAttachments(request.getAttachments());
        consult.setStatus("0");
        consult.setReplyContent(null);
        consult.setReplyTimeText(null);
        consult.setCreateBy(user.getUserName());
        this.workerLegalConsultMapper.insertWorkerLegalConsult(consult);
        this.tryInsertLegalConsultNoticeMessage(worker, user, consult);
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("consultId", consult.getConsultId());
        result.put("status", consult.getStatus());
        result.put("submitTime", consult.getCreateTime());
        result.put("pushTriggered", this.trySendLegalConsultCreatedPush(worker, user, consult));
        return result;
    }

    public Map<String, Object> listLegalConsults(Long userId, String status) {
        List list = this.workerLegalConsultMapper.selectWorkerLegalConsultList(userId, status);
        ArrayList rows = new ArrayList();
        for (WorkerLegalConsult item : list) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("consultId", item.getConsultId());
            row.put("consultType", item.getConsultType());
            row.put("title", item.getTitle());
            row.put("status", item.getStatus());
            row.put("statusText", this.consultStatusText(item.getStatus()));
            row.put("submitTime", item.getCreateTime());
            rows.add(row);
        }
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("total", rows.size());
        result.put("rows", rows);
        return result;
    }

    public Map<String, Object> getLegalConsultDetail(Long userId, Long consultId) {
        WorkerLegalConsult item = this.workerLegalConsultMapper.selectWorkerLegalConsultById(consultId, userId);
        if (item == null) {
            throw new ServiceException("\u672a\u627e\u5230\u54a8\u8be2\u8bb0\u5f55\u3002");
        }
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("consultId", item.getConsultId());
        result.put("consultType", item.getConsultType());
        result.put("title", item.getTitle());
        result.put("content", item.getContent());
        result.put("attachments", item.getAttachments());
        result.put("status", item.getStatus());
        result.put("statusText", this.consultStatusText(item.getStatus()));
        result.put("replyContent", item.getReplyContent());
        result.put("replyTime", item.getReplyTimeText());
        result.put("submitTime", item.getCreateTime());
        return result;
    }

    public Map<String, Object> getNoticeList(Long userId, int pageNum, int pageSize) {
        int limit = Math.max(pageNum * pageSize, pageSize);
        ArrayList<Map> rows = new ArrayList<Map>();
        rows.addAll(this.adaptWorkerNoticeMessageRows(this.workerNoticeMessageMapper.selectWorkerNoticeMessageList(userId, Integer.valueOf(limit))));
        rows.addAll(this.adaptSysNoticeRows(this.sysNoticeReadService.selectNoticeListWithReadStatus(userId, limit)));
        rows.sort(Comparator.comparing(this::extractPublishTimeForSort, Comparator.nullsLast(Date::compareTo)).reversed());
        int fromIndex = Math.max(0, (pageNum - 1) * pageSize);
        int toIndex = Math.min(rows.size(), fromIndex + pageSize);
        ArrayList pageRows = new ArrayList();
        if (fromIndex < rows.size()) {
            pageRows.addAll(rows.subList(fromIndex, toIndex));
        }
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("total", rows.size());
        result.put("rows", pageRows);
        return result;
    }

    public Map<String, Object> getNoticeDetail(Long userId, Long noticeId) {
        if (this.isWorkerNoticeMessageId(noticeId)) {
            return this.getWorkerNoticeDetail(userId, noticeId);
        }
        SysNotice notice = this.sysNoticeService.selectNoticeById(noticeId);
        if (notice == null || !"0".equals(notice.getStatus())) {
            throw new ServiceException("\u672a\u627e\u5230\u901a\u77e5\u516c\u544a\u3002");
        }
        this.sysNoticeReadService.markRead(noticeId, userId);
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("noticeId", notice.getNoticeId());
        result.put("title", notice.getNoticeTitle());
        result.put("content", notice.getNoticeContent());
        result.put("noticeType", notice.getNoticeType());
        result.put("publishTime", notice.getCreateTime());
        result.put("readFlag", true);
        this.applyNoticeJumpFields(result, notice);
        return result;
    }

    public void markNoticeRead(Long userId, Long noticeId) {
        if (this.isWorkerNoticeMessageId(noticeId)) {
            this.workerNoticeMessageMapper.markWorkerNoticeMessageRead(this.resolveWorkerNoticeMessageId(noticeId), userId, "worker-app");
            return;
        }
        this.sysNoticeReadService.markRead(noticeId, userId);
    }

    public int getUnreadNoticeCount(Long userId) {
        int workerUnread = this.workerNoticeMessageMapper.countUnreadWorkerNoticeMessage(userId);
        int sysUnread = this.sysNoticeReadService.selectUnreadCount(userId);
        return workerUnread + sysUnread;
    }

    public Map<String, Object> getHotline() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("phoneNumber", "12351");
        result.put("displayText", "\u5de5\u4f1a\u6cd5\u5f8b\u670d\u52a1\u70ed\u7ebf 12351");
        return result;
    }

    public Map<String, Object> getLegalArticleList() {
        List<Map<String, Object>> cmsRows = this.buildCmsLegalArticleRows();
        if (!cmsRows.isEmpty()) {
            LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
            result.put("rows", cmsRows);
            result.put("total", cmsRows.size());
            return result;
        }
        ArrayList<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        rows.add(this.article("lecture-1", "\u6cd5\u5f8b\u516c\u76ca\u8bb2\u5ea7", "\u6b20\u85aa\u4e89\u8bae\u600e\u4e48\u7559\u8bc1", "\u7ed3\u5408\u5de5\u8d44\u3001\u8003\u52e4\u548c\u804a\u5929\u8bb0\u5f55\u5feb\u901f\u56fa\u5b9a\u8bc1\u636e\u3002"));
        rows.add(this.article("lecture-2", "\u5de5\u4f1a\u670d\u52a1", "\u793e\u4fdd\u65ad\u7f34\u5982\u4f55\u7ef4\u6743", "\u5148\u786e\u8ba4\u65ad\u7f34\u6708\u4efd\uff0c\u518d\u51c6\u5907\u52b3\u52a8\u5173\u7cfb\u548c\u5de5\u8d44\u8bc1\u660e\u3002"));
        rows.add(this.article("lecture-3", "\u52b3\u52a8\u5408\u540c", "\u8bd5\u7528\u671f\u4e5f\u5fc5\u987b\u7b7e\u5408\u540c\u5417", "\u8bd5\u7528\u671f\u5c5e\u4e8e\u52b3\u52a8\u5408\u540c\u671f\u9650\u7684\u4e00\u90e8\u5206\uff0c\u5e94\u7b7e\u4e66\u9762\u5408\u540c\u3002"));
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("rows", rows);
        result.put("total", rows.size());
        return result;
    }

    public Map<String, Object> getLegalArticleDetail(String articleKey) {
        Map<String, Object> item;
        Map<String, Object> cmsDetail;
        Long cmsContentId = this.parseCmsContentId(articleKey);
        if (cmsContentId != null && (cmsDetail = this.buildCmsDetailArticle(cmsContentId, articleKey)) != null) {
            return cmsDetail;
        }
        switch (articleKey) {
            case "lecture-1": {
                Map<String, Object> map = this.detailArticle("lecture-1", "\u6cd5\u5f8b\u516c\u76ca\u8bb2\u5ea7", "\u6b20\u85aa\u4e89\u8bae\u600e\u4e48\u7559\u8bc1", Arrays.asList("\u4fdd\u7559\u5de5\u8d44\u6761\u3001\u94f6\u884c\u6d41\u6c34\u548c\u8003\u52e4\u8bb0\u5f55\u3002", "\u5982\u5de5\u8d44\u901a\u8fc7\u5fae\u4fe1\u6216\u652f\u4ed8\u5b9d\u53d1\u653e\uff0c\u4e5f\u8981\u622a\u56fe\u8f6c\u8d26\u8bb0\u5f55\u3002", "\u5982\u534f\u5546\u65e0\u679c\uff0c\u53ef\u5148\u5411\u5de5\u4f1a\u6216\u52b3\u52a8\u76d1\u5bdf\u6295\u8bc9\u3002"));
                break;
            }
            case "lecture-2": {
                Map<String, Object> map = this.detailArticle("lecture-2", "\u5de5\u4f1a\u670d\u52a1", "\u793e\u4fdd\u65ad\u7f34\u5982\u4f55\u7ef4\u6743", Arrays.asList("\u5148\u786e\u8ba4\u4f01\u4e1a\u65ad\u7f34\u7684\u5177\u4f53\u6708\u4efd\u3002", "\u51c6\u5907\u8eab\u4efd\u8bc1\u660e\u3001\u52b3\u52a8\u5408\u540c\u3001\u5de5\u8d44\u53d1\u653e\u8bb0\u5f55\u3002", "\u4f18\u5148\u901a\u8fc7\u5de5\u4f1a\u548c\u4eba\u793e\u6e20\u9053\u53d1\u8d77\u534f\u540c\u5904\u7406\u3002"));
                break;
            }
            case "lecture-3": {
                Map<String, Object> map = this.detailArticle("lecture-3", "\u52b3\u52a8\u5408\u540c", "\u8bd5\u7528\u671f\u4e5f\u5fc5\u987b\u7b7e\u5408\u540c\u5417", Arrays.asList("\u8bd5\u7528\u671f\u5305\u542b\u5728\u52b3\u52a8\u5408\u540c\u671f\u9650\u5185\u3002", "\u672a\u7b7e\u5408\u540c\u53ef\u80fd\u6d89\u53ca\u53cc\u500d\u5de5\u8d44\u8d23\u4efb\u3002", "\u5c3d\u5feb\u8865\u7b7e\u5e76\u56fa\u5b9a\u5165\u804c\u8bc1\u636e\u3002"));
                break;
            }
            default: {
                Map<String, Object> map = item = null;
            }
        }
        if (item == null) {
            throw new ServiceException("\u672a\u627e\u5230\u8bb2\u5ea7\u5185\u5bb9\u3002");
        }
        return item;
    }

    public Map<String, Object> getLegalFaqList(String keyword) {
        String trimmedKeyword;
        ArrayList<Map> rows = new ArrayList<Map>();
        rows.add(this.faq("faq-1", "\u6b20\u85aa\u540e\u5148\u4fdd\u7559\u54ea\u4e9b\u8bc1\u636e", "\u5de5\u8d44\u7ef4\u6743", "\u4f18\u5148\u4fdd\u7559\u5de5\u8d44\u6761\u3001\u94f6\u884c\u6d41\u6c34\u3001\u8003\u52e4\u548c\u6c9f\u901a\u8bb0\u5f55\u3002"));
        rows.add(this.faq("faq-2", "\u793e\u4fdd\u65ad\u7f34\u540e\u600e\u4e48\u529e", "\u793e\u4fdd\u4e89\u8bae", "\u5148\u786e\u8ba4\u65ad\u7f34\u6708\u4efd\uff0c\u518d\u51c6\u5907\u52b3\u52a8\u5173\u7cfb\u548c\u5de5\u8d44\u8bc1\u660e\u3002"));
        rows.add(this.faq("faq-3", "\u8bd5\u7528\u671f\u9700\u8981\u7b7e\u52b3\u52a8\u5408\u540c\u5417", "\u52b3\u52a8\u5408\u540c", "\u8bd5\u7528\u671f\u5c5e\u4e8e\u52b3\u52a8\u5408\u540c\u671f\u9650\u7684\u4e00\u90e8\u5206\uff0c\u5e94\u7b7e\u4e66\u9762\u5408\u540c\u3002"));
        rows.add(this.faq("faq-4", "\u5de5\u4f24\u540e\u5e73\u53f0\u4e0a\u8981\u8865\u4ec0\u4e48\u6750\u6599", "\u5de5\u4f24\u8d54\u4ed8", "\u5148\u8865\u9f50\u75c5\u5386\u3001\u73b0\u573a\u7167\u7247\u3001\u8003\u52e4\u548c\u8bc1\u4eba\u4fe1\u606f\u3002"));
        String string = trimmedKeyword = keyword == null ? "" : keyword.trim();
        if (StringUtils.isNotEmpty((String)trimmedKeyword)) {
            rows.removeIf(item -> !this.faqMatches((Map<String, Object>)item, trimmedKeyword));
        }
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("keyword", trimmedKeyword);
        result.put("rows", rows);
        result.put("total", rows.size());
        return result;
    }

    public Map<String, Object> getLegalFaqDetail(String faqKey) {
        Map<String, Object> item;
        switch (faqKey) {
            case "faq-1": {
                Map<String, Object> map = this.detailArticle("faq-1", "\u5de5\u8d44\u7ef4\u6743", "\u6b20\u85aa\u540e\u5148\u4fdd\u7559\u54ea\u4e9b\u8bc1\u636e", Arrays.asList("\u5148\u4fdd\u7559\u5de5\u8d44\u6761\u3001\u94f6\u884c\u6d41\u6c34\u548c\u8003\u52e4\u8bb0\u5f55\u3002", "\u5982\u901a\u8fc7\u5fae\u4fe1\u3001\u652f\u4ed8\u5b9d\u6216\u73b0\u91d1\u652f\u4ed8\uff0c\u4e5f\u8981\u4fdd\u7559\u8f6c\u8d26\u622a\u56fe\u6216\u6536\u6b3e\u51ed\u8bc1\u3002", "\u4e0e\u7528\u5de5\u5355\u4f4d\u6c9f\u901a\u65f6\u5c3d\u91cf\u4fdd\u7559\u804a\u5929\u8bb0\u5f55\u3001\u5f55\u97f3\u6216\u4e66\u9762\u627f\u8bfa\u3002"));
                break;
            }
            case "faq-2": {
                Map<String, Object> map = this.detailArticle("faq-2", "\u793e\u4fdd\u4e89\u8bae", "\u793e\u4fdd\u65ad\u7f34\u540e\u600e\u4e48\u529e", Arrays.asList("\u5148\u786e\u8ba4\u65ad\u7f34\u7684\u5177\u4f53\u6708\u4efd\u548c\u9669\u79cd\u3002", "\u51c6\u5907\u52b3\u52a8\u5408\u540c\u3001\u5de5\u8d44\u8bb0\u5f55\u3001\u5728\u5c97\u8bc1\u660e\u7b49\u6750\u6599\u3002", "\u53ef\u5148\u8fdb\u5165\u6cd5\u5f8b\u54a8\u8be2\u6216\u5de5\u4f1a\u670d\u52a1\u9875\uff0c\u6309\u534f\u540c\u6e20\u9053\u63a8\u8fdb\u8865\u7f34\u3002"));
                break;
            }
            case "faq-3": {
                Map<String, Object> map = this.detailArticle("faq-3", "\u52b3\u52a8\u5408\u540c", "\u8bd5\u7528\u671f\u9700\u8981\u7b7e\u52b3\u52a8\u5408\u540c\u5417", Arrays.asList("\u8bd5\u7528\u671f\u5c5e\u4e8e\u52b3\u52a8\u5408\u540c\u671f\u9650\u7684\u4e00\u90e8\u5206\u3002", "\u5373\u4f7f\u5728\u8bd5\u7528\u671f\uff0c\u4e5f\u5e94\u7b7e\u8ba2\u4e66\u9762\u52b3\u52a8\u5408\u540c\u3002", "\u82e5\u672a\u7b7e\u5408\u540c\uff0c\u5e94\u5c3d\u5feb\u56fa\u5b9a\u5165\u804c\u65f6\u95f4\u3001\u5c97\u4f4d\u548c\u5de5\u8d44\u6807\u51c6\u7b49\u8bc1\u636e\u3002"));
                break;
            }
            case "faq-4": {
                Map<String, Object> map = this.detailArticle("faq-4", "\u5de5\u4f24\u8d54\u4ed8", "\u5de5\u4f24\u540e\u5e73\u53f0\u4e0a\u8981\u8865\u4ec0\u4e48\u6750\u6599", Arrays.asList("\u5148\u4e0a\u4f20\u73b0\u573a\u7167\u7247\u3001\u5c31\u8bca\u75c5\u5386\u548c\u8d39\u7528\u7968\u636e\u3002", "\u540c\u6b65\u8865\u9f50\u8003\u52e4\u8bb0\u5f55\u3001\u5c97\u4f4d\u4fe1\u606f\u548c\u8bc1\u4eba\u8bf4\u660e\u3002", "\u5982\u5b58\u5728\u4e89\u8bae\uff0c\u53ef\u540c\u65f6\u53d1\u8d77\u6cd5\u5f8b\u54a8\u8be2\u548c\u6295\u8bc9\u4e3e\u62a5\uff0c\u4fdd\u8bc1\u8bc1\u636e\u94fe\u5b8c\u6574\u3002"));
                break;
            }
            default: {
                Map<String, Object> map = item = null;
            }
        }
        if (item == null) {
            throw new ServiceException("\u672a\u627e\u5230\u5e38\u89c1\u6cd5\u5f8b\u95ee\u9898\u3002");
        }
        return item;
    }

    public Map<String, Object> getUnionServiceHome() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("hotline", this.buildUnionHotline());
        result.put("quickActions", Arrays.asList(this.action("union-legal", "\u5728\u7ebf\u6cd5\u5f8b\u54a8\u8be2", "/pages/legal/index"), this.action("union-complaint", "\u6295\u8bc9\u5efa\u8bae", "/pages/complaint/index"), this.action("union-contract", "\u96c6\u4f53\u5408\u540c\u67e5\u9605", "/pages/union/contracts"), this.action("union-lecture", "\u7ef4\u6743\u8bb2\u5ea7", "/pages/legal/article-list")));
        result.put("caseList", this.getUnionCaseList().get("rows"));
        result.put("noticeList", this.getUnionNoticeList().get("rows"));
        result.put("contractList", this.getUnionContractList().get("rows"));
        return result;
    }

    public Map<String, Object> getUnionCaseList() {
        List<Map<String, Object>> cmsRows = this.buildCmsUnionRows("case");
        if (!cmsRows.isEmpty()) {
            LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
            result.put("rows", cmsRows);
            result.put("total", cmsRows.size());
            return result;
        }
        ArrayList<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        rows.add(this.unionCase("case-1", "\u6b20\u85aa\u534f\u5546\u6848\u4f8b", "\u901a\u8fc7\u5de5\u8d44\u6761\u3001\u8003\u52e4\u548c\u804a\u5929\u8bb0\u5f55\u56fa\u5b9a\u8bc1\u636e\u540e\uff0c\u5b8c\u6210\u534f\u5546\u5904\u7406\u3002"));
        rows.add(this.unionCase("case-2", "\u793e\u4fdd\u65ad\u7f34\u7ef4\u6743\u6848\u4f8b", "\u5148\u786e\u8ba4\u65ad\u7f34\u6708\u4efd\uff0c\u518d\u7531\u5de5\u4f1a\u534f\u540c\u4eba\u793e\u6e20\u9053\u63a8\u8fdb\u8865\u7f34\u3002"));
        rows.add(this.unionCase("case-3", "\u5de5\u4f24\u7559\u75d5\u8865\u8bc1\u6848\u4f8b", "\u53d1\u751f\u8f7b\u4f24\u540e\u53ca\u65f6\u8865\u9f50\u7167\u7247\u3001\u75c5\u5386\u548c\u8003\u52e4\uff0c\u907f\u514d\u8ba4\u5b9a\u6ede\u540e\u3002"));
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("rows", rows);
        result.put("total", rows.size());
        return result;
    }

    public Map<String, Object> getUnionNoticeList() {
        List<Map<String, Object>> cmsRows = this.buildCmsUnionRows("guide");
        if (!cmsRows.isEmpty()) {
            LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
            result.put("rows", cmsRows);
            result.put("total", cmsRows.size());
            return result;
        }
        ArrayList<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        rows.add(this.unionNotice("notice-1", "\u5de5\u4f1a\u7ef4\u6743\u503c\u73ed\u5b89\u6392", "\u5de5\u4f5c\u65e5 9:00-18:00 \u53ef\u5728\u7ebf\u53d1\u8d77\u6cd5\u5f8b\u54a8\u8be2\u3002"));
        rows.add(this.unionNotice("notice-2", "\u96c6\u4f53\u5408\u540c\u67e5\u9605\u63d0\u9192", "\u5982\u9700\u6838\u5bf9\u52b3\u52a8\u5408\u540c\u6761\u6b3e\uff0c\u53ef\u5148\u4fdd\u7559\u5408\u540c\u9996\u9875\u548c\u7b7e\u7ae0\u9875\u3002"));
        rows.add(this.unionNotice("notice-3", "\u5de5\u4f1a\u6d3b\u52a8\u62a5\u540d", "\u672c\u6708\u5c06\u4e3e\u529e\u52b3\u52a8\u8005\u6743\u76ca\u8bb2\u5ea7\u548c\u73b0\u573a\u7b54\u7591\u3002"));
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("rows", rows);
        result.put("total", rows.size());
        return result;
    }

    public Map<String, Object> getUnionCaseDetail(String caseKey) {
        Map<String, Object> detail;
        Map<String, Object> cmsDetail;
        Long cmsContentId = this.parseCmsContentId(caseKey);
        if (cmsContentId != null && (cmsDetail = this.buildCmsUnionDetail(cmsContentId, caseKey)) != null) {
            return cmsDetail;
        }
        switch (caseKey) {
            case "case-1": {
                Map<String, Object> map = this.unionDetail("case-1", "\u6b20\u85aa\u534f\u5546\u6848\u4f8b", Arrays.asList("\u52b3\u52a8\u8005\u5148\u6c47\u603b\u5de5\u8d44\u6761\u3001\u8003\u52e4\u548c\u94f6\u884c\u6d41\u6c34\u3002", "\u901a\u8fc7\u5e73\u53f0\u53d1\u8d77\u6cd5\u5f8b\u54a8\u8be2\uff0c\u786e\u8ba4\u7ef4\u6743\u8def\u5f84\u3002", "\u5de5\u4f1a\u534f\u52a9\u4f01\u4e1a\u4e0e\u52b3\u52a8\u8005\u5b8c\u6210\u534f\u5546\u652f\u4ed8\u3002"));
                break;
            }
            case "case-2": {
                Map<String, Object> map = this.unionDetail("case-2", "\u793e\u4fdd\u65ad\u7f34\u7ef4\u6743\u6848\u4f8b", Arrays.asList("\u5148\u786e\u8ba4\u65ad\u7f34\u6708\u4efd\u548c\u53c2\u4fdd\u72b6\u6001\u3002", "\u51c6\u5907\u52b3\u52a8\u5408\u540c\u3001\u5de5\u8d44\u8bb0\u5f55\u548c\u793e\u4fdd\u622a\u56fe\u3002", "\u7531\u5de5\u4f1a\u534f\u540c\u4eba\u793e\u6e20\u9053\u7763\u4fc3\u4f01\u4e1a\u8865\u7f34\u3002"));
                break;
            }
            case "case-3": {
                Map<String, Object> map = this.unionDetail("case-3", "\u5de5\u4f24\u7559\u75d5\u8865\u8bc1\u6848\u4f8b", Arrays.asList("\u53d1\u751f\u8f7b\u4f24\u540e\u5148\u5c31\u533b\u5e76\u4fdd\u5b58\u75c5\u5386\u3002", "\u8865\u9f50\u73b0\u573a\u7167\u7247\u3001\u8003\u52e4\u548c\u8bc1\u4eba\u4fe1\u606f\u3002", "\u5fc5\u8981\u65f6\u540c\u6b65\u8fdb\u5165\u6295\u8bc9\u4e3e\u62a5\u548c\u6cd5\u5f8b\u54a8\u8be2\u6a21\u5757\u3002"));
                break;
            }
            default: {
                Map<String, Object> map = detail = null;
            }
        }
        if (detail == null) {
            throw new ServiceException("\u672a\u627e\u5230\u5de5\u4f1a\u6848\u4f8b\u3002");
        }
        return detail;
    }

    public Map<String, Object> getUnionNoticeDetail(String noticeKey) {
        Map<String, Object> detail;
        Map<String, Object> cmsDetail;
        Long cmsContentId = this.parseCmsContentId(noticeKey);
        if (cmsContentId != null && (cmsDetail = this.buildCmsUnionDetail(cmsContentId, noticeKey)) != null) {
            return cmsDetail;
        }
        switch (noticeKey) {
            case "notice-1": {
                Map<String, Object> map = this.unionDetail("notice-1", "\u5de5\u4f1a\u7ef4\u6743\u503c\u73ed\u5b89\u6392", Arrays.asList("\u5de5\u4f5c\u65e5 9:00-18:00 \u53ef\u5728\u7ebf\u53d1\u8d77\u6cd5\u5f8b\u54a8\u8be2\u3002", "\u7d27\u6025\u60c5\u51b5\u53ef\u76f4\u63a5\u62e8\u6253 12351 \u5de5\u4f1a\u70ed\u7ebf\u3002", "\u63d0\u4ea4\u95ee\u9898\u65f6\u5c3d\u91cf\u9644\u5de5\u8d44\u6761\u3001\u793e\u4fdd\u622a\u56fe\u7b49\u8bc1\u636e\u3002"));
                break;
            }
            case "notice-2": {
                Map<String, Object> map = this.unionDetail("notice-2", "\u96c6\u4f53\u5408\u540c\u67e5\u9605\u63d0\u9192", Arrays.asList("\u6838\u5bf9\u52b3\u52a8\u5408\u540c\u9996\u9875\u3001\u7b7e\u7ae0\u9875\u548c\u5c97\u4f4d\u6761\u6b3e\u3002", "\u5982\u53d1\u73b0\u6761\u6b3e\u4e0e\u5b9e\u9645\u4e0d\u7b26\uff0c\u5148\u4fdd\u7559\u8bc1\u636e\u3002", "\u53ef\u901a\u8fc7\u5de5\u4f1a\u670d\u52a1\u9875\u54a8\u8be2\u5408\u540c\u4e89\u8bae\u5904\u7406\u65b9\u5f0f\u3002"));
                break;
            }
            case "notice-3": {
                Map<String, Object> map = this.unionDetail("notice-3", "\u5de5\u4f1a\u6d3b\u52a8\u62a5\u540d", Arrays.asList("\u672c\u6708\u5c06\u4e3e\u529e\u52b3\u52a8\u8005\u6743\u76ca\u8bb2\u5ea7\u548c\u73b0\u573a\u7b54\u7591\u3002", "\u4f18\u5148\u5efa\u8bae\u5df2\u5b8c\u6210\u57f9\u8bad\u7684\u52b3\u52a8\u8005\u62a5\u540d\u53c2\u52a0\u3002", "\u6d3b\u52a8\u5165\u53e3\u548c\u7b7e\u5230\u65b9\u5f0f\u4ee5\u5e73\u53f0\u901a\u77e5\u4e3a\u51c6\u3002"));
                break;
            }
            default: {
                Map<String, Object> map = detail = null;
            }
        }
        if (detail == null) {
            throw new ServiceException("\u672a\u627e\u5230\u5de5\u4f1a\u901a\u77e5\u3002");
        }
        return detail;
    }

    public Map<String, Object> getUnionContractList() {
        ArrayList<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        rows.add(this.unionContract("contract-1", "\u52b3\u52a1\u6d3e\u9063\u96c6\u4f53\u5408\u540c", "\u5e7f\u4e1c\u9633\u5149\u52b3\u52a1\u6709\u9650\u516c\u53f8", "2026-01-01 \u81f3 2026-12-31", "\u5728\u5c97\u4eba\u5458\u5de5\u8d44\u53d1\u653e\u3001\u52a0\u73ed\u89c4\u5219\u4e0e\u4f11\u606f\u4f11\u5047\u7ea6\u5b9a\u3002"));
        rows.add(this.unionContract("contract-2", "\u7075\u6d3b\u7528\u5de5\u6743\u76ca\u534f\u5546\u5907\u5fd8", "\u73e0\u6d77\u84dd\u6d77\u7528\u5de5\u670d\u52a1\u4e2d\u5fc3", "2026-03-01 \u81f3 2027-02-28", "\u793e\u4fdd\u7f34\u7eb3\u3001\u5de5\u4f24\u7559\u75d5\u548c\u4e89\u8bae\u534f\u5546\u6d41\u7a0b\u8bf4\u660e\u3002"));
        rows.add(this.unionContract("contract-3", "\u65b0\u4e1a\u6001\u52b3\u52a8\u8005\u96c6\u4f53\u534f\u5546\u8981\u70b9", "\u5e7f\u5dde\u57ce\u914d\u670d\u52a1\u8054\u76df", "2026-04-01 \u81f3 2027-03-31", "\u6d3e\u5355\u6536\u5165\u3001\u7533\u8bc9\u65f6\u6548\u4e0e\u5b89\u5168\u57f9\u8bad\u8d23\u4efb\u7ea6\u5b9a\u3002"));
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("rows", rows);
        result.put("total", rows.size());
        return result;
    }

    public Map<String, Object> getUnionContractDetail(String contractKey) {
        Map<String, Object> detail;
        switch (contractKey) {
            case "contract-1": {
                Map<String, Object> map = this.unionContractDetail("contract-1", "\u52b3\u52a1\u6d3e\u9063\u96c6\u4f53\u5408\u540c", "\u5e7f\u4e1c\u9633\u5149\u52b3\u52a1\u6709\u9650\u516c\u53f8", "2026-01-01 \u81f3 2026-12-31", Arrays.asList("\u5de5\u8d44\u5e94\u6309\u6708\u8db3\u989d\u53d1\u653e\uff0c\u9047\u8282\u5047\u65e5\u5e94\u63d0\u524d\u652f\u4ed8\u3002", "\u52a0\u73ed\u5b89\u6392\u9700\u63d0\u524d\u544a\u77e5\uff0c\u5e76\u6309\u7ea6\u5b9a\u6807\u51c6\u652f\u4ed8\u52a0\u73ed\u5de5\u8d44\u3002", "\u52b3\u52a8\u8005\u53ef\u901a\u8fc7\u5de5\u4f1a\u670d\u52a1\u9875\u63d0\u4ea4\u5de5\u8d44\u4e89\u8bae\u548c\u8bc1\u636e\u6750\u6599\u3002"));
                break;
            }
            case "contract-2": {
                Map<String, Object> map = this.unionContractDetail("contract-2", "\u7075\u6d3b\u7528\u5de5\u6743\u76ca\u534f\u5546\u5907\u5fd8", "\u73e0\u6d77\u84dd\u6d77\u7528\u5de5\u670d\u52a1\u4e2d\u5fc3", "2026-03-01 \u81f3 2027-02-28", Arrays.asList("\u7075\u6d3b\u7528\u5de5\u4eba\u5458\u5e94\u6309\u6708\u6838\u5bf9\u793e\u4fdd\u7f34\u7eb3\u72b6\u6001\u3002", "\u53d1\u751f\u5de5\u4f24\u6216\u4e8b\u6545\u65f6\uff0c\u5e94\u5728\u5e73\u53f0\u53ca\u65f6\u7559\u75d5\u5e76\u8865\u5145\u75c5\u5386\u6750\u6599\u3002", "\u534f\u5546\u4e0d\u6210\u65f6\u53ef\u5148\u7ecf\u5de5\u4f1a\u534f\u8c03\uff0c\u518d\u8d70\u52b3\u52a8\u4e89\u8bae\u5904\u7406\u6e20\u9053\u3002"));
                break;
            }
            case "contract-3": {
                Map<String, Object> map = this.unionContractDetail("contract-3", "\u65b0\u4e1a\u6001\u52b3\u52a8\u8005\u96c6\u4f53\u534f\u5546\u8981\u70b9", "\u5e7f\u5dde\u57ce\u914d\u670d\u52a1\u8054\u76df", "2026-04-01 \u81f3 2027-03-31", Arrays.asList("\u6d3e\u5355\u8ba1\u4ef7\u3001\u5956\u52b1\u6263\u51cf\u5e94\u63d0\u524d\u516c\u5f00\u8bf4\u660e\u3002", "\u57f9\u8bad\u3001\u6295\u8bc9\u3001\u7533\u8bc9\u6d41\u7a0b\u5e94\u4fdd\u7559\u7ebf\u4e0a\u7559\u75d5\u8bb0\u5f55\u3002", "\u51fa\u73b0\u4e89\u8bae\u65f6\uff0c\u52b3\u52a8\u8005\u53ef\u76f4\u63a5\u5411\u5de5\u4f1a\u63d0\u4ea4\u5e73\u53f0\u6d41\u6c34\u548c\u6c9f\u901a\u8bb0\u5f55\u3002"));
                break;
            }
            default: {
                Map<String, Object> map = detail = null;
            }
        }
        if (detail == null) {
            throw new ServiceException("\u672a\u627e\u5230\u96c6\u4f53\u5408\u540c\u3002");
        }
        return detail;
    }

    private void validateComplaintRequest(WorkerComplaintCreateRequest request) {
        if (request == null || StringUtils.isEmpty((String)request.getComplaintType()) || StringUtils.isEmpty((String)request.getTitle()) || StringUtils.isEmpty((String)request.getContent())) {
            throw new ServiceException("\u6295\u8bc9\u4e3e\u62a5\u4fe1\u606f\u4e0d\u5b8c\u6574\u3002");
        }
    }

    private void validateLegalConsultRequest(WorkerLegalConsultCreateRequest request) {
        if (request == null || StringUtils.isEmpty((String)request.getConsultType()) || StringUtils.isEmpty((String)request.getTitle()) || StringUtils.isEmpty((String)request.getContent())) {
            throw new ServiceException("\u6cd5\u5f8b\u54a8\u8be2\u4fe1\u606f\u4e0d\u5b8c\u6574\u3002");
        }
    }

    private void tryInsertComplaintNoticeMessage(YgbPerson worker, SysUser user, WorkerComplaint complaint) {
        WorkerNoticeMessage message = new WorkerNoticeMessage();
        message.setUserId(user.getUserId());
        message.setPersonId(worker.getPersonId());
        message.setPersonName(worker.getPersonName());
        message.setMessageType("BUSINESS");
        message.setTitle("\u6295\u8bc9\u63d0\u4ea4\u6210\u529f");
        message.setSummary("\u60a8\u7684\u6295\u8bc9/\u4e3e\u62a5\u5df2\u63d0\u4ea4\u6210\u529f\uff0c\u53ef\u5728\u5e73\u53f0\u6301\u7eed\u8ddf\u8e2a\u5904\u7406\u8fdb\u5ea6\u3002");
        message.setContent("\u60a8\u7684\u6295\u8bc9/\u4e3e\u62a5\u5df2\u63d0\u4ea4\u6210\u529f\uff0c\u53ef\u5728\u5e73\u53f0\u6301\u7eed\u8ddf\u8e2a\u5904\u7406\u8fdb\u5ea6\u3002");
        message.setBizType("complaint");
        message.setBizId(String.valueOf(complaint.getComplaintId()));
        message.setJumpPath("/pages/complaint/detail");
        message.setJumpQueryText(JSON.toJSONString(Map.of("complaintId", complaint.getComplaintId())));
        message.setActionLabel("\u67e5\u770b\u6295\u8bc9");
        message.setSourceLabel("\u6295\u8bc9\u63d0\u4ea4\u6210\u529f");
        message.setReadFlag("0");
        message.setCreateBy(user.getUserName());
        this.tryInsertWorkerNoticeMessage(message);
    }

    private void tryInsertLegalConsultNoticeMessage(YgbPerson worker, SysUser user, WorkerLegalConsult consult) {
        WorkerNoticeMessage message = new WorkerNoticeMessage();
        message.setUserId(user.getUserId());
        message.setPersonId(worker.getPersonId());
        message.setPersonName(worker.getPersonName());
        message.setMessageType("BUSINESS");
        message.setTitle("\u6cd5\u5f8b\u54a8\u8be2\u63d0\u4ea4\u6210\u529f");
        message.setSummary("\u60a8\u7684\u6cd5\u5f8b\u54a8\u8be2\u5df2\u63d0\u4ea4\u6210\u529f\uff0c\u53ef\u5728\u5e73\u53f0\u67e5\u770b\u72b6\u6001\u548c\u56de\u590d\u8fdb\u5c55\u3002");
        message.setContent("\u60a8\u7684\u6cd5\u5f8b\u54a8\u8be2\u5df2\u63d0\u4ea4\u6210\u529f\uff0c\u53ef\u5728\u5e73\u53f0\u67e5\u770b\u72b6\u6001\u548c\u56de\u590d\u8fdb\u5c55\u3002");
        message.setBizType("legal-consult");
        message.setBizId(String.valueOf(consult.getConsultId()));
        message.setJumpPath("/pages/legal/detail");
        message.setJumpQueryText(JSON.toJSONString(Map.of("consultId", consult.getConsultId())));
        message.setActionLabel("\u67e5\u770b\u54a8\u8be2");
        message.setSourceLabel("\u6cd5\u5f8b\u54a8\u8be2\u63d0\u4ea4\u6210\u529f");
        message.setReadFlag("0");
        message.setCreateBy(user.getUserName());
        this.tryInsertWorkerNoticeMessage(message);
    }

    private void tryInsertWorkerNoticeMessage(WorkerNoticeMessage message) {
        try {
            this.workerNoticeMessageMapper.insertWorkerNoticeMessage(message);
        }
        catch (Exception ex) {
            log.warn("Worker notice message insert failed, userId={}, bizType={}, bizId={}, message={}", new Object[]{message == null ? null : message.getUserId(), message == null ? null : message.getBizType(), message == null ? null : message.getBizId(), ex.getMessage()});
        }
    }

    private boolean trySendComplaintCreatedPush(YgbPerson worker, SysUser user, WorkerComplaint complaint) {
        LinkedHashMap<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("jumpPath", "/pages/complaint/detail");
        payload.put("jumpQuery", Map.of("complaintId", complaint.getComplaintId()));
        payload.put("actionLabel", "\u67e5\u770b\u6295\u8bc9");
        payload.put("sourceLabel", "\u6295\u8bc9\u63d0\u4ea4\u6210\u529f");
        LinkedHashMap<String, Object> metadata = new LinkedHashMap<String, Object>();
        metadata.put("source", "worker-complaint-created");
        metadata.put("userId", user.getUserId());
        metadata.put("personId", worker.getPersonId());
        metadata.put("complaintId", complaint.getComplaintId());
        metadata.put("complaintType", complaint.getComplaintType());
        return this.trySendBusinessPush(user, "\u6295\u8bc9\u63d0\u4ea4\u6210\u529f", "\u60a8\u7684\u6295\u8bc9/\u4e3e\u62a5\u5df2\u63d0\u4ea4\u6210\u529f\uff0c\u53ef\u5728\u5e73\u53f0\u6301\u7eed\u8ddf\u8e2a\u5904\u7406\u8fdb\u5ea6\u3002", payload, metadata);
    }

    private boolean trySendLegalConsultCreatedPush(YgbPerson worker, SysUser user, WorkerLegalConsult consult) {
        LinkedHashMap<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("jumpPath", "/pages/legal/detail");
        payload.put("jumpQuery", Map.of("consultId", consult.getConsultId()));
        payload.put("actionLabel", "\u67e5\u770b\u54a8\u8be2");
        payload.put("sourceLabel", "\u6cd5\u5f8b\u54a8\u8be2\u63d0\u4ea4\u6210\u529f");
        LinkedHashMap<String, Object> metadata = new LinkedHashMap<String, Object>();
        metadata.put("source", "worker-legal-consult-created");
        metadata.put("userId", user.getUserId());
        metadata.put("personId", worker.getPersonId());
        metadata.put("consultId", consult.getConsultId());
        metadata.put("consultType", consult.getConsultType());
        return this.trySendBusinessPush(user, "\u6cd5\u5f8b\u54a8\u8be2\u63d0\u4ea4\u6210\u529f", "\u60a8\u7684\u6cd5\u5f8b\u54a8\u8be2\u5df2\u63d0\u4ea4\u6210\u529f\uff0c\u53ef\u5728\u5e73\u53f0\u67e5\u770b\u72b6\u6001\u548c\u56de\u590d\u8fdb\u5c55\u3002", payload, metadata);
    }

    private boolean trySendBusinessPush(SysUser user, String title, String content, Map<String, Object> payload, Map<String, Object> metadata) {
        WorkerSetting setting = this.workerProfileMapper.selectWorkerSetting(user.getUserId());
        if (!this.isBusinessPushReady(setting)) {
            return false;
        }
        try {
            this.workerPushGatewayService.sendPush(setting.getPushClientId(), title, content, payload, metadata);
            log.info("Worker business push accepted, userId={}, source={}, title={}", new Object[]{user.getUserId(), metadata == null ? null : metadata.get("source"), title});
            return true;
        }
        catch (Exception ex) {
            log.warn("Worker business push failed, userId={}, source={}, message={}", new Object[]{user.getUserId(), metadata == null ? null : metadata.get("source"), ex.getMessage()});
            return false;
        }
    }

    private boolean isBusinessPushReady(WorkerSetting setting) {
        return setting != null && "1".equals(setting.getNotifyEnabled()) && StringUtils.isNotEmpty((String)setting.getPushClientId());
    }

    private List<Map<String, Object>> adaptWorkerNoticeMessageRows(List<WorkerNoticeMessage> messages) {
        ArrayList<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (messages == null) {
            return rows;
        }
        for (WorkerNoticeMessage message : messages) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("noticeId", this.wrapWorkerNoticeMessageId(message.getMessageId()));
            row.put("title", message.getTitle());
            row.put("summary", this.firstNonBlank(message.getSummary(), this.buildNoticeSummary(message.getContent())));
            row.put("noticeType", this.firstNonBlank(message.getMessageType(), "BUSINESS"));
            row.put("publishTime", message.getCreateTime());
            row.put("readFlag", "1".equals(message.getReadFlag()));
            row.put("jumpPath", message.getJumpPath());
            row.put("jumpQuery", this.parseJumpQueryText(message.getJumpQueryText()));
            row.put("actionLabel", message.getActionLabel());
            row.put("sourceLabel", message.getSourceLabel());
            row.put("_sortTime", message.getCreateTime());
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> adaptSysNoticeRows(List<SysNotice> notices) {
        ArrayList<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (notices == null) {
            return rows;
        }
        for (SysNotice notice : notices) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("noticeId", notice.getNoticeId());
            row.put("title", notice.getNoticeTitle());
            row.put("summary", this.buildNoticeSummary(notice.getNoticeContent()));
            row.put("noticeType", notice.getNoticeType());
            row.put("publishTime", notice.getCreateTime());
            row.put("readFlag", notice.getIsRead());
            row.put("_sortTime", notice.getCreateTime());
            this.applyNoticeJumpFields(row, notice);
            rows.add(row);
        }
        return rows;
    }

    private Date extractPublishTimeForSort(Map<String, Object> row) {
        Object value;
        Object object = value = row == null ? null : row.get("_sortTime");
        if (value instanceof Date) {
            return (Date)value;
        }
        value = row == null ? null : row.get("publishTime");
        return value instanceof Date ? (Date)value : null;
    }

    private Map<String, Object> getWorkerNoticeDetail(Long userId, Long noticeId) {
        Long messageId = this.resolveWorkerNoticeMessageId(noticeId);
        WorkerNoticeMessage message = this.workerNoticeMessageMapper.selectWorkerNoticeMessageById(messageId, userId);
        if (message == null) {
            throw new ServiceException("\u672a\u627e\u5230\u4e2a\u4eba\u6d88\u606f\u3002");
        }
        this.workerNoticeMessageMapper.markWorkerNoticeMessageRead(messageId, userId, "worker-app");
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("noticeId", this.wrapWorkerNoticeMessageId(message.getMessageId()));
        result.put("title", message.getTitle());
        result.put("content", this.firstNonBlank(message.getContent(), message.getSummary()));
        result.put("noticeType", this.firstNonBlank(message.getMessageType(), "BUSINESS"));
        result.put("publishTime", message.getCreateTime());
        result.put("readFlag", true);
        result.put("jumpPath", message.getJumpPath());
        result.put("jumpQuery", this.parseJumpQueryText(message.getJumpQueryText()));
        result.put("actionLabel", message.getActionLabel());
        result.put("sourceLabel", message.getSourceLabel());
        return result;
    }

    private boolean isWorkerNoticeMessageId(Long noticeId) {
        return noticeId != null && noticeId < 0L;
    }

    private Long resolveWorkerNoticeMessageId(Long noticeId) {
        return noticeId == null ? null : Long.valueOf(Math.abs(noticeId));
    }

    private Long wrapWorkerNoticeMessageId(Long messageId) {
        return messageId == null ? null : Long.valueOf(-messageId.longValue());
    }

    private Object parseJumpQueryText(String jumpQueryText) {
        if (StringUtils.isEmpty((String)jumpQueryText)) {
            return null;
        }
        try {
            return JSON.parse((String)jumpQueryText);
        }
        catch (Exception ignored) {
            return null;
        }
    }

    private String buildNoticeSummary(String content) {
        if (StringUtils.isEmpty((String)content)) {
            return "";
        }
        return content.length() <= 48 ? content : content.substring(0, 48) + "...";
    }

    private void applyNoticeJumpFields(Map<String, Object> row, SysNotice notice) {
        Map<String, Object> jumpConfig = this.parseNoticeJumpConfig(notice == null ? null : notice.getRemark());
        if (jumpConfig.isEmpty()) {
            return;
        }
        row.putAll(jumpConfig);
    }

    private Map<String, Object> parseNoticeJumpConfig(String remark) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        if (StringUtils.isEmpty((String)remark)) {
            return result;
        }
        String trimmedRemark = remark.trim();
        if (!trimmedRemark.startsWith("{")) {
            return result;
        }
        try {
            String sourceLabel;
            String actionLabel;
            Object jumpQuery;
            JSONObject payload = JSON.parseObject((String)trimmedRemark);
            if (payload == null) {
                return result;
            }
            String jumpPath = this.firstNonBlank(payload.getString("jumpPath"), payload.getString("path"), payload.getString("url"));
            if (StringUtils.isNotEmpty((String)jumpPath)) {
                result.put("jumpPath", jumpPath);
            }
            if ((jumpQuery = payload.get("jumpQuery")) == null) {
                jumpQuery = payload.get("query");
            }
            if (jumpQuery != null) {
                result.put("jumpQuery", jumpQuery);
            }
            if (StringUtils.isNotEmpty((String)(actionLabel = this.firstNonBlank(payload.getString("actionLabel"), payload.getString("actionText"), payload.getString("jumpLabel"), payload.getString("label"))))) {
                result.put("actionLabel", actionLabel);
            }
            if (StringUtils.isNotEmpty((String)(sourceLabel = this.firstNonBlank(payload.getString("sourceLabel"), payload.getString("sourceText"))))) {
                result.put("sourceLabel", sourceLabel);
            }
        }
        catch (Exception ignored) {
            return new LinkedHashMap<String, Object>();
        }
        return result;
    }

    private String complaintStatusText(String status) {
        if ("1".equals(status)) {
            return "\u5904\u7406\u4e2d";
        }
        if ("2".equals(status)) {
            return "\u5df2\u5904\u7406";
        }
        return "\u5f85\u5904\u7406";
    }

    private String consultStatusText(String status) {
        if ("1".equals(status)) {
            return "\u5df2\u56de\u590d";
        }
        return "\u5f85\u56de\u590d";
    }

    private String yesNoText(String flag, String yesText, String noText) {
        return "1".equals(flag) ? yesText : noText;
    }

    private String firstNonBlank(String ... values) {
        for (String value : values) {
            if (!StringUtils.isNotEmpty((String)value)) continue;
            return value;
        }
        return null;
    }

    private Map<String, Object> article(String articleKey, String category, String title, String summary) {
        LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
        row.put("articleKey", articleKey);
        row.put("category", category);
        row.put("title", title);
        row.put("summary", summary);
        return row;
    }

    private Map<String, Object> faq(String faqKey, String category, String title, String summary) {
        LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
        row.put("faqKey", faqKey);
        row.put("category", category);
        row.put("title", title);
        row.put("summary", summary);
        return row;
    }

    private boolean faqMatches(Map<String, Object> item, String keyword) {
        return this.containsIgnoreCase(item.get("title"), keyword) || this.containsIgnoreCase(item.get("category"), keyword) || this.containsIgnoreCase(item.get("summary"), keyword);
    }

    private boolean containsIgnoreCase(Object value, String keyword) {
        if (value == null || StringUtils.isEmpty((String)keyword)) {
            return false;
        }
        return String.valueOf(value).toLowerCase().contains(keyword.toLowerCase());
    }

    private Map<String, Object> detailArticle(String articleKey, String category, String title, List<String> paragraphs) {
        LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
        row.put("articleKey", articleKey);
        row.put("category", category);
        row.put("title", title);
        row.put("paragraphs", paragraphs);
        return row;
    }

    private Map<String, Object> action(String key, String label, String path) {
        LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
        row.put("key", key);
        row.put("label", label);
        row.put("path", path);
        return row;
    }

    private Map<String, Object> unionCase(String caseKey, String title, String summary) {
        LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
        row.put("caseKey", caseKey);
        row.put("title", title);
        row.put("summary", summary);
        return row;
    }

    private Map<String, Object> unionNotice(String noticeKey, String title, String summary) {
        LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
        row.put("noticeKey", noticeKey);
        row.put("title", title);
        row.put("summary", summary);
        return row;
    }

    private Map<String, Object> unionContract(String contractKey, String title, String enterpriseName, String period, String summary) {
        LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
        row.put("contractKey", contractKey);
        row.put("title", title);
        row.put("enterpriseName", enterpriseName);
        row.put("period", period);
        row.put("summary", summary);
        return row;
    }

    private Map<String, Object> unionDetail(String key, String title, List<String> paragraphs) {
        LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
        row.put("detailKey", key);
        row.put("title", title);
        row.put("paragraphs", paragraphs);
        return row;
    }

    private List<Map<String, Object>> buildCmsLegalArticleRows() {
        List list = this.portalContentMapper.selectPublishedPortalContentList(WORKER_PORTAL_CODE, "union", null, null, null);
        ArrayList<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (list == null) {
            return rows;
        }
        for (YgbPortalContent item : list) {
            if ("intro".equals(item.getCategoryCode())) continue;
            rows.add(this.article(this.toCmsKey(item.getContentId()), this.portalTypeLabel(item, "\u52b3\u52a8\u8005\u6743\u76ca"), item.getTitle(), this.firstNonBlank(item.getSummary(), this.resolvePortalContentBody(item))));
        }
        return rows;
    }

    private List<Map<String, Object>> buildCmsUnionRows(String categoryCode) {
        List list = this.portalContentMapper.selectPublishedPortalContentList(WORKER_PORTAL_CODE, "union", categoryCode, null, null);
        ArrayList<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (list == null) {
            return rows;
        }
        for (YgbPortalContent item : list) {
            String key = this.toCmsKey(item.getContentId());
            String summary = this.firstNonBlank(item.getSummary(), this.resolvePortalContentBody(item));
            if ("case".equals(categoryCode)) {
                rows.add(this.unionCase(key, item.getTitle(), summary));
                continue;
            }
            if (!"guide".equals(categoryCode)) continue;
            rows.add(this.unionNotice(key, item.getTitle(), summary));
        }
        return rows;
    }

    private Map<String, Object> buildCmsDetailArticle(Long contentId, String articleKey) {
        YgbPortalContent item = this.selectPublishedPortalContent(contentId);
        if (item == null) {
            return null;
        }
        return this.detailArticle(articleKey, this.portalTypeLabel(item, "\u52b3\u52a8\u8005\u6743\u76ca"), item.getTitle(), this.splitPortalParagraphs(item));
    }

    private Map<String, Object> buildCmsUnionDetail(Long contentId, String detailKey) {
        YgbPortalContent item = this.selectPublishedPortalContent(contentId);
        if (item == null) {
            return null;
        }
        return this.unionDetail(detailKey, item.getTitle(), this.splitPortalParagraphs(item));
    }

    private Map<String, Object> buildUnionHotline() {
        YgbPortalContent intro = this.firstPublishedPortalContent("union", "intro");
        if (intro == null) {
            return this.getHotline();
        }
        JSONObject extra = this.parseExtraJson(intro.getExtraJson());
        String hotline = this.firstNonBlank(extra.getString("hotline"), "12351");
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("phoneNumber", hotline);
        result.put("displayText", "\u5de5\u4f1a\u6cd5\u5f8b\u670d\u52a1\u70ed\u7ebf " + hotline);
        if (StringUtils.isNotEmpty((String)extra.getString("legalAid"))) {
            result.put("legalAid", extra.getString("legalAid"));
        }
        return result;
    }

    private YgbPortalContent firstPublishedPortalContent(String sectionCode, String categoryCode) {
        List list = this.portalContentMapper.selectPublishedPortalContentList(WORKER_PORTAL_CODE, sectionCode, categoryCode, null, Integer.valueOf(1));
        return list == null || list.isEmpty() ? null : (YgbPortalContent)list.get(0);
    }

    private YgbPortalContent selectPublishedPortalContent(Long contentId) {
        if (contentId == null) {
            return null;
        }
        YgbPortalContent item = this.portalContentMapper.selectPortalContentById(contentId);
        if (item == null || !"0".equals(item.getStatus()) || !WORKER_PORTAL_CODE.equals(item.getPortalCode())) {
            return null;
        }
        return item;
    }

    private String toCmsKey(Long contentId) {
        return contentId == null ? null : CMS_KEY_PREFIX + contentId;
    }

    private Long parseCmsContentId(String key) {
        if (StringUtils.isEmpty((String)key) || !key.startsWith(CMS_KEY_PREFIX)) {
            return null;
        }
        try {
            return Long.valueOf(key.substring(CMS_KEY_PREFIX.length()));
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    private JSONObject parseExtraJson(String extraJson) {
        if (StringUtils.isEmpty((String)extraJson)) {
            return new JSONObject();
        }
        try {
            JSONObject json = JSON.parseObject((String)extraJson);
            return json == null ? new JSONObject() : json;
        }
        catch (Exception ignored) {
            return new JSONObject();
        }
    }

    private String portalTypeLabel(YgbPortalContent item, String defaultLabel) {
        JSONObject extra = this.parseExtraJson(item == null ? null : item.getExtraJson());
        return this.firstNonBlank(extra.getString("typeLabel"), extra.getString("categoryLabel"), defaultLabel);
    }

    private String resolvePortalContentBody(YgbPortalContent item) {
        if (item == null) {
            return "";
        }
        return this.firstNonBlank(item.getContent(), item.getSummary(), item.getTitle(), "");
    }

    private List<String> splitPortalParagraphs(YgbPortalContent item) {
        String body = this.resolvePortalContentBody(item);
        String cleaned = body.replaceAll("(?i)<br\\s*/?>", "\n").replaceAll("(?i)</p>", "\n").replaceAll("<[^>]+>", " ").replace("&nbsp;", " ").replace("\r", "\n").trim();
        ArrayList<String> rows = new ArrayList<String>();
        for (String block : cleaned.split("\\n+")) {
            String trimmedBlock = block.replaceAll("\\s+", " ").trim();
            if (StringUtils.isEmpty((String)trimmedBlock)) continue;
            String[] fragments = trimmedBlock.split("[\uff1b;\u3002\uff01\uff1f]");
            int added = 0;
            for (String fragment : fragments) {
                String sentence = fragment.replaceAll("\\s+", " ").trim();
                if (StringUtils.isEmpty((String)sentence)) continue;
                rows.add(sentence);
                ++added;
            }
            if (added != 0) continue;
            rows.add(trimmedBlock);
        }
        if (rows.isEmpty()) {
            rows.add(this.firstNonBlank(item == null ? null : item.getSummary(), item == null ? null : item.getTitle(), ""));
        }
        return rows;
    }

    private Map<String, Object> unionContractDetail(String contractKey, String title, String enterpriseName, String period, List<String> clauses) {
        LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
        row.put("contractKey", contractKey);
        row.put("title", title);
        row.put("enterpriseName", enterpriseName);
        row.put("period", period);
        row.put("clauses", clauses);
        return row;
    }
}
