package com.yuegongbao.ygb.extension.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.yuegongbao.common.exception.ServiceException;

public enum YgbBusinessRecordModule
{
    SOCIAL_SUPPLEMENT("socialSupplement", "ygb_br_social_supplement", "SOCIAL_SUPPLEMENT"),
    SOCIAL_ENROLLMENT("socialEnrollment", "ygb_br_social_enrollment", "SOCIAL_ENROLLMENT"),
    AQ_INSURANCE_CLAIM("aqInsuranceClaim", "ygb_br_aq_insurance_claim", "AQ_INSURANCE_CLAIM"),
    TAX_INVOICE("taxInvoice", "ygb_br_tax_invoice", "TAX_INVOICE"),
    TAX_FUND_FLOW("taxFundFlow", "ygb_br_tax_fund_flow", "TAX_FUND_FLOW"),
    TAX_RECOVERY("taxRecovery", "ygb_br_tax_recovery", "TAX_RECOVERY"),
    THREE_NATURE_POST("threeNaturePost", "ygb_br_three_nature_post", "THREE_NATURE_POST"),
    SPECIAL_RECTIFICATION("specialRectification", "ygb_br_special_rectification", "SPECIAL_RECTIFICATION"),
    EXPANSION_SUBSIDY("expansionSubsidy", "ygb_br_expansion_subsidy", "EXPANSION_SUBSIDY"),
    EXPANSION_EVALUATION("expansionEvaluation", "ygb_br_expansion_evaluation", "EXPANSION_EVALUATION"),
    ENTERPRISE_RELATION("enterpriseRelation", "ygb_br_enterprise_relation", "ENTERPRISE_RELATION"),
    ENTERPRISE_HIGH_RISK("enterpriseHighRisk", "ygb_br_enterprise_high_risk", "ENTERPRISE_HIGH_RISK"),
    ENTERPRISE_UNION("enterpriseUnion", "ygb_br_enterprise_union", "ENTERPRISE_UNION"),
    CREDIT_RULE("creditRule", "ygb_br_credit_rule", "CREDIT_RULE"),
    CREDIT_REPAIR("creditRepair", "ygb_br_credit_repair", "CREDIT_REPAIR"),
    CREDIT_SANCTION("creditSanction", "ygb_br_credit_sanction", "CREDIT_SANCTION"),
    INJURY_PERSON_MONITOR("injuryPersonMonitor", "ygb_br_injury_person_monitor", "INJURY_PERSON_MONITOR"),
    INJURY_EMPLOYER_MONITOR("injuryEmployerMonitor", "ygb_br_injury_employer_monitor", "INJURY_EMPLOYER_MONITOR"),
    INJURY_REGION_MONITOR("injuryRegionMonitor", "ygb_br_injury_region_monitor", "INJURY_REGION_MONITOR"),
    INJURY_OCC_HAZARD_MONITOR("injuryOccHazardMonitor", "ygb_br_injury_occ_hazard_monitor", "INJURY_OCC_HAZARD_MONITOR"),
    INJURY_NEWFORM_HAZARD_MONITOR("injuryNewformHazardMonitor", "ygb_br_injury_newform_hazard_monitor", "INJURY_NEWFORM_HAZARD_MONITOR"),
    INJURY_ACCIDENT_WARNING("injuryAccidentWarning", "ygb_br_injury_accident_warning", "INJURY_ACCIDENT_WARNING"),
    DEVICE_INSTALL_ORDER("deviceInstallOrder", "ygb_br_device_install_order", "DEVICE_INSTALL_ORDER"),
    DEVICE_REPAIR_ORDER("deviceRepairOrder", "ygb_br_device_repair_order", "DEVICE_REPAIR_ORDER"),
    DEVICE_INSPECT_PLAN("deviceInspectPlan", "ygb_br_device_inspect_plan", "DEVICE_INSPECT_PLAN"),
    DEVICE_GEOFENCE("deviceGeofence", "ygb_br_device_geofence", "DEVICE_GEOFENCE"),
    PLATFORM_DOCUMENT("platformDocument", "ygb_br_platform_document", "PLATFORM_DOCUMENT"),
    PLATFORM_EXCHANGE("platformExchange", "ygb_br_platform_exchange", "PLATFORM_EXCHANGE"),
    PLATFORM_SECURITY_AUDIT("platformSecurityAudit", "ygb_br_platform_security_audit", "PLATFORM_SECURITY_AUDIT"),
    PLATFORM_BACKUP("platformBackup", "ygb_br_platform_backup", "PLATFORM_BACKUP"),
    PERSON_BLACKLIST("personBlacklist", "ygb_br_person_blacklist", "PERSON_BLACKLIST"),
    PERSON_CERTIFICATE("personCertificate", "ygb_br_person_certificate", "PERSON_CERTIFICATE"),
    PERSON_TRAINING("personTraining", "ygb_br_person_training", "PERSON_TRAINING"),
    PERSON_HIGH_RISK_POST("personHighRiskPost", "ygb_br_person_high_risk_post", "PERSON_HIGH_RISK_POST"),
    PERSON_RISK_POST("personRiskPost", "ygb_br_person_risk_post", "PERSON_RISK_POST"),
    PERSON_EXPERT("personExpert", "ygb_br_person_expert", "PERSON_EXPERT"),
    NEWFORM_TRAINING("newformTraining", "ygb_br_newform_training", "NEWFORM_TRAINING"),
    OCCUPATION_PREVENTION("occupationPrevention", "ygb_br_occupation_prevention", "OCCUPATION_PREVENTION"),
    OCCUPATION_HEALTH_ARCHIVE("occupationHealthArchive", "ygb_br_occupation_health_archive", "OCCUPATION_HEALTH_ARCHIVE"),
    OPERATION_ENTERPRISE_REVIEW("operationEnterpriseReview", "ygb_br_operation_enterprise_review", "OPERATION_ENTERPRISE_REVIEW"),
    OPERATION_MESSAGE("operationMessage", "ygb_br_operation_message", "OPERATION_MESSAGE"),
    OPERATION_CHIP_DISPATCH("operationChipDispatch", "ygb_br_operation_chip_dispatch", "OPERATION_CHIP_DISPATCH"),
    OPERATION_MAINTENANCE_STATS("operationMaintenanceStats", "ygb_br_operation_maintenance_stats", "OPERATION_MAINTENANCE_STATS"),
    PREVENTION_PUBLICITY("preventionPublicity", "ygb_br_prevention_publicity", "PREVENTION_PUBLICITY"),
    PREVENTION_TRAINING("preventionTraining", "ygb_br_prevention_training", "PREVENTION_TRAINING"),
    PREVENTION_AI("preventionAi", "ygb_br_prevention_ai", "PREVENTION_AI"),
    OPERATION_JOB_CATEGORY("operationJobCategory", "ygb_br_operation_job_category", "OPERATION_JOB_CATEGORY"),
    OPERATION_RECRUIT_STATS("operationRecruitStats", "ygb_br_operation_recruit_stats", "OPERATION_RECRUIT_STATS"),
    UNION_ORG("unionOrg", "ygb_br_union_org", "UNION_ORG"),
    UNION_SUPERVISION("unionSupervision", "ygb_br_union_supervision", "UNION_SUPERVISION"),
    UNION_LEGAL_AID("unionLegalAid", "ygb_br_union_legal_aid", "UNION_LEGAL_AID"),
    UNION_NEGOTIATION("unionNegotiation", "ygb_br_union_negotiation", "UNION_NEGOTIATION"),
    UNION_PREVENTION_SUPERVISION("unionPreventionSupervision", "ygb_br_union_prevention_supervision", "UNION_PREVENTION_SUPERVISION");

    private static final Map<String, YgbBusinessRecordModule> BY_CODE = Arrays.stream(values())
        .collect(Collectors.toUnmodifiableMap(YgbBusinessRecordModule::getModuleCode, Function.identity()));

    private final String moduleCode;

    private final String tableName;

    private final String legacyRecordType;

    YgbBusinessRecordModule(String moduleCode, String tableName, String legacyRecordType)
    {
        this.moduleCode = moduleCode;
        this.tableName = tableName;
        this.legacyRecordType = legacyRecordType;
    }

    public String getModuleCode()
    {
        return moduleCode;
    }

    public String getTableName()
    {
        return tableName;
    }

    public String getLegacyRecordType()
    {
        return legacyRecordType;
    }

    public static YgbBusinessRecordModule resolve(String moduleCode)
    {
        YgbBusinessRecordModule module = BY_CODE.get(moduleCode);
        if (module == null)
        {
            throw new ServiceException("Unsupported business module: " + moduleCode);
        }
        return module;
    }
}
