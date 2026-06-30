-- Phase 52: PC business menus use dedicated tables and APIs
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS ygb_br_social_supplement (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='socialSupplement????';

INSERT INTO ygb_br_social_supplement (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'socialSupplement', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'SOCIAL_SUPPLEMENT' AND NOT EXISTS (SELECT 1 FROM ygb_br_social_supplement t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_social_enrollment (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='socialEnrollment????';

INSERT INTO ygb_br_social_enrollment (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'socialEnrollment', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'SOCIAL_ENROLLMENT' AND NOT EXISTS (SELECT 1 FROM ygb_br_social_enrollment t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_aq_insurance_claim (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='aqInsuranceClaim????';

INSERT INTO ygb_br_aq_insurance_claim (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'aqInsuranceClaim', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'AQ_INSURANCE_CLAIM' AND NOT EXISTS (SELECT 1 FROM ygb_br_aq_insurance_claim t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_tax_invoice (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='taxInvoice????';

INSERT INTO ygb_br_tax_invoice (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'taxInvoice', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'TAX_INVOICE' AND NOT EXISTS (SELECT 1 FROM ygb_br_tax_invoice t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_tax_fund_flow (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='taxFundFlow????';

INSERT INTO ygb_br_tax_fund_flow (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'taxFundFlow', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'TAX_FUND_FLOW' AND NOT EXISTS (SELECT 1 FROM ygb_br_tax_fund_flow t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_tax_recovery (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='taxRecovery????';

INSERT INTO ygb_br_tax_recovery (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'taxRecovery', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'TAX_RECOVERY' AND NOT EXISTS (SELECT 1 FROM ygb_br_tax_recovery t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_three_nature_post (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='threeNaturePost????';

INSERT INTO ygb_br_three_nature_post (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'threeNaturePost', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'THREE_NATURE_POST' AND NOT EXISTS (SELECT 1 FROM ygb_br_three_nature_post t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_special_rectification (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='specialRectification????';

INSERT INTO ygb_br_special_rectification (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'specialRectification', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'SPECIAL_RECTIFICATION' AND NOT EXISTS (SELECT 1 FROM ygb_br_special_rectification t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_expansion_subsidy (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='expansionSubsidy????';

INSERT INTO ygb_br_expansion_subsidy (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'expansionSubsidy', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'EXPANSION_SUBSIDY' AND NOT EXISTS (SELECT 1 FROM ygb_br_expansion_subsidy t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_expansion_evaluation (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='expansionEvaluation????';

INSERT INTO ygb_br_expansion_evaluation (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'expansionEvaluation', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'EXPANSION_EVALUATION' AND NOT EXISTS (SELECT 1 FROM ygb_br_expansion_evaluation t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_enterprise_relation (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='enterpriseRelation????';

INSERT INTO ygb_br_enterprise_relation (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'enterpriseRelation', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'ENTERPRISE_RELATION' AND NOT EXISTS (SELECT 1 FROM ygb_br_enterprise_relation t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_enterprise_high_risk (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='enterpriseHighRisk????';

INSERT INTO ygb_br_enterprise_high_risk (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'enterpriseHighRisk', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'ENTERPRISE_HIGH_RISK' AND NOT EXISTS (SELECT 1 FROM ygb_br_enterprise_high_risk t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_enterprise_union (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='enterpriseUnion????';

INSERT INTO ygb_br_enterprise_union (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'enterpriseUnion', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'ENTERPRISE_UNION' AND NOT EXISTS (SELECT 1 FROM ygb_br_enterprise_union t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_credit_rule (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='creditRule????';

INSERT INTO ygb_br_credit_rule (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'creditRule', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'CREDIT_RULE' AND NOT EXISTS (SELECT 1 FROM ygb_br_credit_rule t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_credit_repair (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='creditRepair????';

INSERT INTO ygb_br_credit_repair (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'creditRepair', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'CREDIT_REPAIR' AND NOT EXISTS (SELECT 1 FROM ygb_br_credit_repair t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_credit_sanction (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='creditSanction????';

INSERT INTO ygb_br_credit_sanction (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'creditSanction', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'CREDIT_SANCTION' AND NOT EXISTS (SELECT 1 FROM ygb_br_credit_sanction t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_injury_person_monitor (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='injuryPersonMonitor????';

INSERT INTO ygb_br_injury_person_monitor (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'injuryPersonMonitor', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'INJURY_PERSON_MONITOR' AND NOT EXISTS (SELECT 1 FROM ygb_br_injury_person_monitor t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_injury_employer_monitor (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='injuryEmployerMonitor????';

INSERT INTO ygb_br_injury_employer_monitor (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'injuryEmployerMonitor', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'INJURY_EMPLOYER_MONITOR' AND NOT EXISTS (SELECT 1 FROM ygb_br_injury_employer_monitor t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_injury_region_monitor (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='injuryRegionMonitor????';

INSERT INTO ygb_br_injury_region_monitor (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'injuryRegionMonitor', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'INJURY_REGION_MONITOR' AND NOT EXISTS (SELECT 1 FROM ygb_br_injury_region_monitor t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_injury_occ_hazard_monitor (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='injuryOccHazardMonitor????';

INSERT INTO ygb_br_injury_occ_hazard_monitor (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'injuryOccHazardMonitor', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'INJURY_OCC_HAZARD_MONITOR' AND NOT EXISTS (SELECT 1 FROM ygb_br_injury_occ_hazard_monitor t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_injury_newform_hazard_monitor (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='injuryNewformHazardMonitor????';

INSERT INTO ygb_br_injury_newform_hazard_monitor (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'injuryNewformHazardMonitor', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'INJURY_NEWFORM_HAZARD_MONITOR' AND NOT EXISTS (SELECT 1 FROM ygb_br_injury_newform_hazard_monitor t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_injury_accident_warning (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='injuryAccidentWarning????';

INSERT INTO ygb_br_injury_accident_warning (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'injuryAccidentWarning', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'INJURY_ACCIDENT_WARNING' AND NOT EXISTS (SELECT 1 FROM ygb_br_injury_accident_warning t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_device_install_order (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='deviceInstallOrder????';

INSERT INTO ygb_br_device_install_order (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'deviceInstallOrder', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'DEVICE_INSTALL_ORDER' AND NOT EXISTS (SELECT 1 FROM ygb_br_device_install_order t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_device_repair_order (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='deviceRepairOrder????';

INSERT INTO ygb_br_device_repair_order (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'deviceRepairOrder', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'DEVICE_REPAIR_ORDER' AND NOT EXISTS (SELECT 1 FROM ygb_br_device_repair_order t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_device_inspect_plan (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='deviceInspectPlan????';

INSERT INTO ygb_br_device_inspect_plan (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'deviceInspectPlan', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'DEVICE_INSPECT_PLAN' AND NOT EXISTS (SELECT 1 FROM ygb_br_device_inspect_plan t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_device_geofence (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='deviceGeofence????';

INSERT INTO ygb_br_device_geofence (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'deviceGeofence', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'DEVICE_GEOFENCE' AND NOT EXISTS (SELECT 1 FROM ygb_br_device_geofence t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_platform_document (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='platformDocument????';

INSERT INTO ygb_br_platform_document (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'platformDocument', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'PLATFORM_DOCUMENT' AND NOT EXISTS (SELECT 1 FROM ygb_br_platform_document t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_platform_exchange (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='platformExchange????';

INSERT INTO ygb_br_platform_exchange (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'platformExchange', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'PLATFORM_EXCHANGE' AND NOT EXISTS (SELECT 1 FROM ygb_br_platform_exchange t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_platform_security_audit (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='platformSecurityAudit????';

INSERT INTO ygb_br_platform_security_audit (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'platformSecurityAudit', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'PLATFORM_SECURITY_AUDIT' AND NOT EXISTS (SELECT 1 FROM ygb_br_platform_security_audit t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_platform_backup (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='platformBackup????';

INSERT INTO ygb_br_platform_backup (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'platformBackup', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'PLATFORM_BACKUP' AND NOT EXISTS (SELECT 1 FROM ygb_br_platform_backup t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_person_blacklist (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='personBlacklist????';

INSERT INTO ygb_br_person_blacklist (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'personBlacklist', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'PERSON_BLACKLIST' AND NOT EXISTS (SELECT 1 FROM ygb_br_person_blacklist t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_person_certificate (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='personCertificate????';

INSERT INTO ygb_br_person_certificate (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'personCertificate', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'PERSON_CERTIFICATE' AND NOT EXISTS (SELECT 1 FROM ygb_br_person_certificate t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_person_training (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='personTraining????';

INSERT INTO ygb_br_person_training (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'personTraining', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'PERSON_TRAINING' AND NOT EXISTS (SELECT 1 FROM ygb_br_person_training t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_person_high_risk_post (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='personHighRiskPost????';

INSERT INTO ygb_br_person_high_risk_post (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'personHighRiskPost', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'PERSON_HIGH_RISK_POST' AND NOT EXISTS (SELECT 1 FROM ygb_br_person_high_risk_post t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_person_risk_post (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='personRiskPost????';

INSERT INTO ygb_br_person_risk_post (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'personRiskPost', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'PERSON_RISK_POST' AND NOT EXISTS (SELECT 1 FROM ygb_br_person_risk_post t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_person_expert (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='personExpert????';

INSERT INTO ygb_br_person_expert (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'personExpert', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'PERSON_EXPERT' AND NOT EXISTS (SELECT 1 FROM ygb_br_person_expert t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_newform_training (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='newformTraining????';

INSERT INTO ygb_br_newform_training (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'newformTraining', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'NEWFORM_TRAINING' AND NOT EXISTS (SELECT 1 FROM ygb_br_newform_training t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_occupation_prevention (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='occupationPrevention????';

INSERT INTO ygb_br_occupation_prevention (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'occupationPrevention', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'OCCUPATION_PREVENTION' AND NOT EXISTS (SELECT 1 FROM ygb_br_occupation_prevention t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_occupation_health_archive (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='occupationHealthArchive????';

INSERT INTO ygb_br_occupation_health_archive (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'occupationHealthArchive', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'OCCUPATION_HEALTH_ARCHIVE' AND NOT EXISTS (SELECT 1 FROM ygb_br_occupation_health_archive t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_operation_enterprise_review (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='operationEnterpriseReview????';

INSERT INTO ygb_br_operation_enterprise_review (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'operationEnterpriseReview', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'OPERATION_ENTERPRISE_REVIEW' AND NOT EXISTS (SELECT 1 FROM ygb_br_operation_enterprise_review t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_operation_message (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='operationMessage????';

INSERT INTO ygb_br_operation_message (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'operationMessage', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'OPERATION_MESSAGE' AND NOT EXISTS (SELECT 1 FROM ygb_br_operation_message t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_operation_chip_dispatch (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='operationChipDispatch????';

INSERT INTO ygb_br_operation_chip_dispatch (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'operationChipDispatch', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'OPERATION_CHIP_DISPATCH' AND NOT EXISTS (SELECT 1 FROM ygb_br_operation_chip_dispatch t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_operation_maintenance_stats (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='operationMaintenanceStats????';

INSERT INTO ygb_br_operation_maintenance_stats (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'operationMaintenanceStats', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'OPERATION_MAINTENANCE_STATS' AND NOT EXISTS (SELECT 1 FROM ygb_br_operation_maintenance_stats t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_prevention_publicity (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='preventionPublicity????';

INSERT INTO ygb_br_prevention_publicity (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'preventionPublicity', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'PREVENTION_PUBLICITY' AND NOT EXISTS (SELECT 1 FROM ygb_br_prevention_publicity t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_prevention_training (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='preventionTraining????';

INSERT INTO ygb_br_prevention_training (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'preventionTraining', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'PREVENTION_TRAINING' AND NOT EXISTS (SELECT 1 FROM ygb_br_prevention_training t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_prevention_ai (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='preventionAi????';

INSERT INTO ygb_br_prevention_ai (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'preventionAi', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'PREVENTION_AI' AND NOT EXISTS (SELECT 1 FROM ygb_br_prevention_ai t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_operation_job_category (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='operationJobCategory????';

INSERT INTO ygb_br_operation_job_category (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'operationJobCategory', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'OPERATION_JOB_CATEGORY' AND NOT EXISTS (SELECT 1 FROM ygb_br_operation_job_category t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_operation_recruit_stats (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='operationRecruitStats????';

INSERT INTO ygb_br_operation_recruit_stats (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'operationRecruitStats', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'OPERATION_RECRUIT_STATS' AND NOT EXISTS (SELECT 1 FROM ygb_br_operation_recruit_stats t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_union_org (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='unionOrg????';

INSERT INTO ygb_br_union_org (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'unionOrg', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'UNION_ORG' AND NOT EXISTS (SELECT 1 FROM ygb_br_union_org t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_union_supervision (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='unionSupervision????';

INSERT INTO ygb_br_union_supervision (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'unionSupervision', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'UNION_SUPERVISION' AND NOT EXISTS (SELECT 1 FROM ygb_br_union_supervision t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_union_legal_aid (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='unionLegalAid????';

INSERT INTO ygb_br_union_legal_aid (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'unionLegalAid', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'UNION_LEGAL_AID' AND NOT EXISTS (SELECT 1 FROM ygb_br_union_legal_aid t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_union_negotiation (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='unionNegotiation????';

INSERT INTO ygb_br_union_negotiation (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'unionNegotiation', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'UNION_NEGOTIATION' AND NOT EXISTS (SELECT 1 FROM ygb_br_union_negotiation t WHERE t.business_id = ygb_module_record.record_id);

CREATE TABLE IF NOT EXISTS ygb_br_union_prevention_supervision (
  business_id bigint not null auto_increment comment '??ID',
  module_code varchar(64) not null comment '????',
  business_no varchar(64) default '' comment '????',
  business_name varchar(255) not null comment '????',
  business_type varchar(64) default '' comment '????',
  stat_month varchar(16) default null comment '????',
  portal_code varchar(32) default 'ygb' comment '????',
  workflow_status varchar(32) default 'draft' comment '????',
  status char(1) default '0' comment '????',
  region_code varchar(32) default null comment '????',
  enterprise_id bigint default null comment '??ID',
  enterprise_name varchar(255) default null comment '????',
  person_id bigint default null comment '??ID',
  person_name varchar(128) default null comment '????',
  related_id bigint default null comment '??ID',
  related_code varchar(128) default null comment '????',
  source_label varchar(128) default null comment '??',
  risk_level varchar(16) default '0' comment '????',
  handle_result varchar(512) default null comment '????',
  owner_name varchar(128) default null comment '???',
  contact_phone varchar(32) default null comment '????',
  amount decimal(18,2) default null comment '??',
  quantity decimal(18,2) default null comment '??',
  event_time datetime default null comment '????',
  start_time datetime default null comment '????',
  end_time datetime default null comment '????',
  content_text text comment '????',
  attachment_url varchar(512) default null comment '????',
  sort_order int default 0 comment '??',
  remark varchar(500) default null comment '??',
  create_by varchar(64) default '' comment '???',
  create_time datetime default null comment '????',
  update_by varchar(64) default '' comment '???',
  update_time datetime default null comment '????',
  del_flag char(1) default '0' comment '????',
  primary key (business_id),
  key idx_module_code (module_code),
  key idx_portal_code (portal_code),
  key idx_region_code (region_code),
  key idx_enterprise_id (enterprise_id),
  key idx_person_id (person_id),
  key idx_stat_month (stat_month),
  key idx_workflow_status (workflow_status),
  key idx_del_flag (del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='unionPreventionSupervision????';

INSERT INTO ygb_br_union_prevention_supervision (business_id, module_code, business_no, business_name, business_type, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, risk_level, content_text, sort_order, remark, create_by, create_time, update_by, update_time, del_flag)
SELECT record_id, 'unionPreventionSupervision', related_code, record_name, category_code, stat_month, portal_code, workflow_status, status, region_code, enterprise_id, enterprise_name, person_id, person_name, related_id, related_code, source_label, warning_level, payload_json, sort_order, remark, create_by, create_time, update_by, update_time, del_flag
FROM ygb_module_record WHERE record_type = 'UNION_PREVENTION_SUPERVISION' AND NOT EXISTS (SELECT 1 FROM ygb_br_union_prevention_supervision t WHERE t.business_id = ygb_module_record.record_id);

-- Route compatibility: cockpit overview menus should load the real cockpit page.
UPDATE sys_menu SET component = 'ygb/cockpit/index', update_time = NOW(), remark = CONCAT(COALESCE(remark, ''), ' | phase52 route alias fixed') WHERE component = 'ygb/cockpit/overview/index';
UPDATE sys_menu SET component = 'azb/cockpit/index', update_time = NOW(), remark = CONCAT(COALESCE(remark, ''), ' | phase52 route alias fixed') WHERE component = 'azb/cockpit/overview/index';

-- Ensure every migrated business menu has a list permission and a complete button group.
UPDATE sys_menu SET perms = 'ygb:socialSupplement:list', update_time = NOW() WHERE component LIKE '%/socialSupplement/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000001, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:socialSupplement:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:socialSupplement:list' OR m.component LIKE '%/socialSupplement/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:socialSupplement:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000002, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:socialSupplement:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:socialSupplement:list' OR m.component LIKE '%/socialSupplement/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:socialSupplement:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000003, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:socialSupplement:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:socialSupplement:list' OR m.component LIKE '%/socialSupplement/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:socialSupplement:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000004, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:socialSupplement:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:socialSupplement:list' OR m.component LIKE '%/socialSupplement/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:socialSupplement:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000005, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:socialSupplement:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:socialSupplement:list' OR m.component LIKE '%/socialSupplement/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:socialSupplement:export');

UPDATE sys_menu SET perms = 'ygb:socialEnrollment:list', update_time = NOW() WHERE component LIKE '%/socialEnrollment/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000011, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:socialEnrollment:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:socialEnrollment:list' OR m.component LIKE '%/socialEnrollment/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:socialEnrollment:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000012, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:socialEnrollment:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:socialEnrollment:list' OR m.component LIKE '%/socialEnrollment/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:socialEnrollment:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000013, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:socialEnrollment:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:socialEnrollment:list' OR m.component LIKE '%/socialEnrollment/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:socialEnrollment:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000014, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:socialEnrollment:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:socialEnrollment:list' OR m.component LIKE '%/socialEnrollment/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:socialEnrollment:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000015, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:socialEnrollment:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:socialEnrollment:list' OR m.component LIKE '%/socialEnrollment/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:socialEnrollment:export');

UPDATE sys_menu SET perms = 'ygb:aqInsuranceClaim:list', update_time = NOW() WHERE component LIKE '%/aqInsuranceClaim/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000021, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:aqInsuranceClaim:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:aqInsuranceClaim:list' OR m.component LIKE '%/aqInsuranceClaim/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:aqInsuranceClaim:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000022, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:aqInsuranceClaim:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:aqInsuranceClaim:list' OR m.component LIKE '%/aqInsuranceClaim/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:aqInsuranceClaim:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000023, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:aqInsuranceClaim:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:aqInsuranceClaim:list' OR m.component LIKE '%/aqInsuranceClaim/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:aqInsuranceClaim:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000024, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:aqInsuranceClaim:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:aqInsuranceClaim:list' OR m.component LIKE '%/aqInsuranceClaim/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:aqInsuranceClaim:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000025, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:aqInsuranceClaim:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:aqInsuranceClaim:list' OR m.component LIKE '%/aqInsuranceClaim/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:aqInsuranceClaim:export');

UPDATE sys_menu SET perms = 'ygb:taxInvoice:list', update_time = NOW() WHERE component LIKE '%/taxInvoice/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000031, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:taxInvoice:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:taxInvoice:list' OR m.component LIKE '%/taxInvoice/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:taxInvoice:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000032, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:taxInvoice:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:taxInvoice:list' OR m.component LIKE '%/taxInvoice/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:taxInvoice:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000033, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:taxInvoice:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:taxInvoice:list' OR m.component LIKE '%/taxInvoice/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:taxInvoice:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000034, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:taxInvoice:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:taxInvoice:list' OR m.component LIKE '%/taxInvoice/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:taxInvoice:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000035, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:taxInvoice:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:taxInvoice:list' OR m.component LIKE '%/taxInvoice/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:taxInvoice:export');

UPDATE sys_menu SET perms = 'ygb:taxFundFlow:list', update_time = NOW() WHERE component LIKE '%/taxFundFlow/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000041, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:taxFundFlow:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:taxFundFlow:list' OR m.component LIKE '%/taxFundFlow/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:taxFundFlow:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000042, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:taxFundFlow:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:taxFundFlow:list' OR m.component LIKE '%/taxFundFlow/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:taxFundFlow:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000043, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:taxFundFlow:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:taxFundFlow:list' OR m.component LIKE '%/taxFundFlow/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:taxFundFlow:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000044, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:taxFundFlow:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:taxFundFlow:list' OR m.component LIKE '%/taxFundFlow/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:taxFundFlow:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000045, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:taxFundFlow:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:taxFundFlow:list' OR m.component LIKE '%/taxFundFlow/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:taxFundFlow:export');

UPDATE sys_menu SET perms = 'ygb:taxRecovery:list', update_time = NOW() WHERE component LIKE '%/taxRecovery/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000051, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:taxRecovery:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:taxRecovery:list' OR m.component LIKE '%/taxRecovery/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:taxRecovery:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000052, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:taxRecovery:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:taxRecovery:list' OR m.component LIKE '%/taxRecovery/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:taxRecovery:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000053, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:taxRecovery:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:taxRecovery:list' OR m.component LIKE '%/taxRecovery/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:taxRecovery:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000054, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:taxRecovery:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:taxRecovery:list' OR m.component LIKE '%/taxRecovery/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:taxRecovery:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000055, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:taxRecovery:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:taxRecovery:list' OR m.component LIKE '%/taxRecovery/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:taxRecovery:export');

UPDATE sys_menu SET perms = 'ygb:threeNaturePost:list', update_time = NOW() WHERE component LIKE '%/threeNaturePost/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000061, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:threeNaturePost:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:threeNaturePost:list' OR m.component LIKE '%/threeNaturePost/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:threeNaturePost:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000062, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:threeNaturePost:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:threeNaturePost:list' OR m.component LIKE '%/threeNaturePost/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:threeNaturePost:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000063, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:threeNaturePost:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:threeNaturePost:list' OR m.component LIKE '%/threeNaturePost/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:threeNaturePost:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000064, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:threeNaturePost:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:threeNaturePost:list' OR m.component LIKE '%/threeNaturePost/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:threeNaturePost:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000065, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:threeNaturePost:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:threeNaturePost:list' OR m.component LIKE '%/threeNaturePost/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:threeNaturePost:export');

UPDATE sys_menu SET perms = 'ygb:specialRectification:list', update_time = NOW() WHERE component LIKE '%/specialRectification/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000071, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:specialRectification:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:specialRectification:list' OR m.component LIKE '%/specialRectification/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:specialRectification:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000072, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:specialRectification:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:specialRectification:list' OR m.component LIKE '%/specialRectification/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:specialRectification:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000073, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:specialRectification:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:specialRectification:list' OR m.component LIKE '%/specialRectification/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:specialRectification:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000074, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:specialRectification:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:specialRectification:list' OR m.component LIKE '%/specialRectification/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:specialRectification:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000075, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:specialRectification:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:specialRectification:list' OR m.component LIKE '%/specialRectification/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:specialRectification:export');

UPDATE sys_menu SET perms = 'ygb:expansionSubsidy:list', update_time = NOW() WHERE component LIKE '%/expansionSubsidy/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000081, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:expansionSubsidy:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:expansionSubsidy:list' OR m.component LIKE '%/expansionSubsidy/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:expansionSubsidy:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000082, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:expansionSubsidy:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:expansionSubsidy:list' OR m.component LIKE '%/expansionSubsidy/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:expansionSubsidy:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000083, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:expansionSubsidy:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:expansionSubsidy:list' OR m.component LIKE '%/expansionSubsidy/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:expansionSubsidy:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000084, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:expansionSubsidy:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:expansionSubsidy:list' OR m.component LIKE '%/expansionSubsidy/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:expansionSubsidy:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000085, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:expansionSubsidy:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:expansionSubsidy:list' OR m.component LIKE '%/expansionSubsidy/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:expansionSubsidy:export');

UPDATE sys_menu SET perms = 'ygb:expansionEvaluation:list', update_time = NOW() WHERE component LIKE '%/expansionEvaluation/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000091, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:expansionEvaluation:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:expansionEvaluation:list' OR m.component LIKE '%/expansionEvaluation/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:expansionEvaluation:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000092, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:expansionEvaluation:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:expansionEvaluation:list' OR m.component LIKE '%/expansionEvaluation/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:expansionEvaluation:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000093, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:expansionEvaluation:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:expansionEvaluation:list' OR m.component LIKE '%/expansionEvaluation/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:expansionEvaluation:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000094, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:expansionEvaluation:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:expansionEvaluation:list' OR m.component LIKE '%/expansionEvaluation/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:expansionEvaluation:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000095, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:expansionEvaluation:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:expansionEvaluation:list' OR m.component LIKE '%/expansionEvaluation/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:expansionEvaluation:export');

UPDATE sys_menu SET perms = 'ygb:enterpriseRelation:list', update_time = NOW() WHERE component LIKE '%/enterpriseRelation/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000101, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:enterpriseRelation:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:enterpriseRelation:list' OR m.component LIKE '%/enterpriseRelation/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:enterpriseRelation:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000102, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:enterpriseRelation:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:enterpriseRelation:list' OR m.component LIKE '%/enterpriseRelation/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:enterpriseRelation:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000103, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:enterpriseRelation:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:enterpriseRelation:list' OR m.component LIKE '%/enterpriseRelation/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:enterpriseRelation:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000104, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:enterpriseRelation:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:enterpriseRelation:list' OR m.component LIKE '%/enterpriseRelation/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:enterpriseRelation:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000105, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:enterpriseRelation:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:enterpriseRelation:list' OR m.component LIKE '%/enterpriseRelation/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:enterpriseRelation:export');

UPDATE sys_menu SET perms = 'ygb:enterpriseHighRisk:list', update_time = NOW() WHERE component LIKE '%/enterpriseHighRisk/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000111, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:enterpriseHighRisk:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:enterpriseHighRisk:list' OR m.component LIKE '%/enterpriseHighRisk/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:enterpriseHighRisk:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000112, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:enterpriseHighRisk:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:enterpriseHighRisk:list' OR m.component LIKE '%/enterpriseHighRisk/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:enterpriseHighRisk:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000113, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:enterpriseHighRisk:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:enterpriseHighRisk:list' OR m.component LIKE '%/enterpriseHighRisk/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:enterpriseHighRisk:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000114, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:enterpriseHighRisk:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:enterpriseHighRisk:list' OR m.component LIKE '%/enterpriseHighRisk/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:enterpriseHighRisk:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000115, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:enterpriseHighRisk:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:enterpriseHighRisk:list' OR m.component LIKE '%/enterpriseHighRisk/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:enterpriseHighRisk:export');

UPDATE sys_menu SET perms = 'ygb:enterpriseUnion:list', update_time = NOW() WHERE component LIKE '%/enterpriseUnion/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000121, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:enterpriseUnion:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:enterpriseUnion:list' OR m.component LIKE '%/enterpriseUnion/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:enterpriseUnion:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000122, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:enterpriseUnion:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:enterpriseUnion:list' OR m.component LIKE '%/enterpriseUnion/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:enterpriseUnion:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000123, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:enterpriseUnion:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:enterpriseUnion:list' OR m.component LIKE '%/enterpriseUnion/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:enterpriseUnion:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000124, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:enterpriseUnion:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:enterpriseUnion:list' OR m.component LIKE '%/enterpriseUnion/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:enterpriseUnion:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000125, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:enterpriseUnion:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:enterpriseUnion:list' OR m.component LIKE '%/enterpriseUnion/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:enterpriseUnion:export');

UPDATE sys_menu SET perms = 'ygb:creditRule:list', update_time = NOW() WHERE component LIKE '%/creditRule/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000131, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:creditRule:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:creditRule:list' OR m.component LIKE '%/creditRule/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:creditRule:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000132, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:creditRule:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:creditRule:list' OR m.component LIKE '%/creditRule/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:creditRule:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000133, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:creditRule:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:creditRule:list' OR m.component LIKE '%/creditRule/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:creditRule:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000134, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:creditRule:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:creditRule:list' OR m.component LIKE '%/creditRule/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:creditRule:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000135, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:creditRule:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:creditRule:list' OR m.component LIKE '%/creditRule/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:creditRule:export');

UPDATE sys_menu SET perms = 'ygb:creditRepair:list', update_time = NOW() WHERE component LIKE '%/creditRepair/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000141, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:creditRepair:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:creditRepair:list' OR m.component LIKE '%/creditRepair/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:creditRepair:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000142, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:creditRepair:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:creditRepair:list' OR m.component LIKE '%/creditRepair/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:creditRepair:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000143, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:creditRepair:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:creditRepair:list' OR m.component LIKE '%/creditRepair/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:creditRepair:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000144, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:creditRepair:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:creditRepair:list' OR m.component LIKE '%/creditRepair/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:creditRepair:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000145, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:creditRepair:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:creditRepair:list' OR m.component LIKE '%/creditRepair/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:creditRepair:export');

UPDATE sys_menu SET perms = 'ygb:creditSanction:list', update_time = NOW() WHERE component LIKE '%/creditSanction/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000151, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:creditSanction:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:creditSanction:list' OR m.component LIKE '%/creditSanction/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:creditSanction:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000152, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:creditSanction:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:creditSanction:list' OR m.component LIKE '%/creditSanction/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:creditSanction:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000153, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:creditSanction:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:creditSanction:list' OR m.component LIKE '%/creditSanction/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:creditSanction:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000154, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:creditSanction:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:creditSanction:list' OR m.component LIKE '%/creditSanction/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:creditSanction:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000155, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:creditSanction:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:creditSanction:list' OR m.component LIKE '%/creditSanction/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:creditSanction:export');

UPDATE sys_menu SET perms = 'ygb:injuryPersonMonitor:list', update_time = NOW() WHERE component LIKE '%/injuryPersonMonitor/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000161, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryPersonMonitor:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryPersonMonitor:list' OR m.component LIKE '%/injuryPersonMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryPersonMonitor:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000162, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryPersonMonitor:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryPersonMonitor:list' OR m.component LIKE '%/injuryPersonMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryPersonMonitor:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000163, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryPersonMonitor:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryPersonMonitor:list' OR m.component LIKE '%/injuryPersonMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryPersonMonitor:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000164, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryPersonMonitor:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryPersonMonitor:list' OR m.component LIKE '%/injuryPersonMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryPersonMonitor:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000165, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryPersonMonitor:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryPersonMonitor:list' OR m.component LIKE '%/injuryPersonMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryPersonMonitor:export');

UPDATE sys_menu SET perms = 'ygb:injuryEmployerMonitor:list', update_time = NOW() WHERE component LIKE '%/injuryEmployerMonitor/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000171, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryEmployerMonitor:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryEmployerMonitor:list' OR m.component LIKE '%/injuryEmployerMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryEmployerMonitor:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000172, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryEmployerMonitor:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryEmployerMonitor:list' OR m.component LIKE '%/injuryEmployerMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryEmployerMonitor:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000173, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryEmployerMonitor:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryEmployerMonitor:list' OR m.component LIKE '%/injuryEmployerMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryEmployerMonitor:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000174, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryEmployerMonitor:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryEmployerMonitor:list' OR m.component LIKE '%/injuryEmployerMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryEmployerMonitor:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000175, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryEmployerMonitor:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryEmployerMonitor:list' OR m.component LIKE '%/injuryEmployerMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryEmployerMonitor:export');

UPDATE sys_menu SET perms = 'ygb:injuryRegionMonitor:list', update_time = NOW() WHERE component LIKE '%/injuryRegionMonitor/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000181, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryRegionMonitor:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryRegionMonitor:list' OR m.component LIKE '%/injuryRegionMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryRegionMonitor:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000182, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryRegionMonitor:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryRegionMonitor:list' OR m.component LIKE '%/injuryRegionMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryRegionMonitor:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000183, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryRegionMonitor:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryRegionMonitor:list' OR m.component LIKE '%/injuryRegionMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryRegionMonitor:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000184, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryRegionMonitor:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryRegionMonitor:list' OR m.component LIKE '%/injuryRegionMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryRegionMonitor:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000185, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryRegionMonitor:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryRegionMonitor:list' OR m.component LIKE '%/injuryRegionMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryRegionMonitor:export');

UPDATE sys_menu SET perms = 'ygb:injuryOccHazardMonitor:list', update_time = NOW() WHERE component LIKE '%/injuryOccHazardMonitor/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000191, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryOccHazardMonitor:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryOccHazardMonitor:list' OR m.component LIKE '%/injuryOccHazardMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryOccHazardMonitor:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000192, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryOccHazardMonitor:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryOccHazardMonitor:list' OR m.component LIKE '%/injuryOccHazardMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryOccHazardMonitor:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000193, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryOccHazardMonitor:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryOccHazardMonitor:list' OR m.component LIKE '%/injuryOccHazardMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryOccHazardMonitor:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000194, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryOccHazardMonitor:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryOccHazardMonitor:list' OR m.component LIKE '%/injuryOccHazardMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryOccHazardMonitor:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000195, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryOccHazardMonitor:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryOccHazardMonitor:list' OR m.component LIKE '%/injuryOccHazardMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryOccHazardMonitor:export');

UPDATE sys_menu SET perms = 'ygb:injuryNewformHazardMonitor:list', update_time = NOW() WHERE component LIKE '%/injuryNewformHazardMonitor/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000201, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryNewformHazardMonitor:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryNewformHazardMonitor:list' OR m.component LIKE '%/injuryNewformHazardMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryNewformHazardMonitor:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000202, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryNewformHazardMonitor:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryNewformHazardMonitor:list' OR m.component LIKE '%/injuryNewformHazardMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryNewformHazardMonitor:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000203, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryNewformHazardMonitor:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryNewformHazardMonitor:list' OR m.component LIKE '%/injuryNewformHazardMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryNewformHazardMonitor:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000204, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryNewformHazardMonitor:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryNewformHazardMonitor:list' OR m.component LIKE '%/injuryNewformHazardMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryNewformHazardMonitor:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000205, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryNewformHazardMonitor:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryNewformHazardMonitor:list' OR m.component LIKE '%/injuryNewformHazardMonitor/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryNewformHazardMonitor:export');

UPDATE sys_menu SET perms = 'ygb:injuryAccidentWarning:list', update_time = NOW() WHERE component LIKE '%/injuryAccidentWarning/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000211, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryAccidentWarning:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryAccidentWarning:list' OR m.component LIKE '%/injuryAccidentWarning/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryAccidentWarning:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000212, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryAccidentWarning:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryAccidentWarning:list' OR m.component LIKE '%/injuryAccidentWarning/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryAccidentWarning:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000213, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryAccidentWarning:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryAccidentWarning:list' OR m.component LIKE '%/injuryAccidentWarning/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryAccidentWarning:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000214, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryAccidentWarning:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryAccidentWarning:list' OR m.component LIKE '%/injuryAccidentWarning/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryAccidentWarning:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000215, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:injuryAccidentWarning:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:injuryAccidentWarning:list' OR m.component LIKE '%/injuryAccidentWarning/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:injuryAccidentWarning:export');

UPDATE sys_menu SET perms = 'ygb:deviceInstallOrder:list', update_time = NOW() WHERE component LIKE '%/deviceInstallOrder/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000221, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceInstallOrder:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceInstallOrder:list' OR m.component LIKE '%/deviceInstallOrder/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceInstallOrder:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000222, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceInstallOrder:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceInstallOrder:list' OR m.component LIKE '%/deviceInstallOrder/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceInstallOrder:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000223, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceInstallOrder:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceInstallOrder:list' OR m.component LIKE '%/deviceInstallOrder/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceInstallOrder:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000224, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceInstallOrder:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceInstallOrder:list' OR m.component LIKE '%/deviceInstallOrder/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceInstallOrder:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000225, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceInstallOrder:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceInstallOrder:list' OR m.component LIKE '%/deviceInstallOrder/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceInstallOrder:export');

UPDATE sys_menu SET perms = 'ygb:deviceRepairOrder:list', update_time = NOW() WHERE component LIKE '%/deviceRepairOrder/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000231, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceRepairOrder:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceRepairOrder:list' OR m.component LIKE '%/deviceRepairOrder/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceRepairOrder:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000232, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceRepairOrder:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceRepairOrder:list' OR m.component LIKE '%/deviceRepairOrder/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceRepairOrder:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000233, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceRepairOrder:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceRepairOrder:list' OR m.component LIKE '%/deviceRepairOrder/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceRepairOrder:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000234, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceRepairOrder:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceRepairOrder:list' OR m.component LIKE '%/deviceRepairOrder/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceRepairOrder:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000235, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceRepairOrder:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceRepairOrder:list' OR m.component LIKE '%/deviceRepairOrder/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceRepairOrder:export');

UPDATE sys_menu SET perms = 'ygb:deviceInspectPlan:list', update_time = NOW() WHERE component LIKE '%/deviceInspectPlan/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000241, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceInspectPlan:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceInspectPlan:list' OR m.component LIKE '%/deviceInspectPlan/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceInspectPlan:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000242, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceInspectPlan:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceInspectPlan:list' OR m.component LIKE '%/deviceInspectPlan/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceInspectPlan:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000243, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceInspectPlan:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceInspectPlan:list' OR m.component LIKE '%/deviceInspectPlan/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceInspectPlan:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000244, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceInspectPlan:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceInspectPlan:list' OR m.component LIKE '%/deviceInspectPlan/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceInspectPlan:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000245, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceInspectPlan:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceInspectPlan:list' OR m.component LIKE '%/deviceInspectPlan/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceInspectPlan:export');

UPDATE sys_menu SET perms = 'ygb:deviceGeofence:list', update_time = NOW() WHERE component LIKE '%/deviceGeofence/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000251, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceGeofence:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceGeofence:list' OR m.component LIKE '%/deviceGeofence/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceGeofence:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000252, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceGeofence:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceGeofence:list' OR m.component LIKE '%/deviceGeofence/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceGeofence:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000253, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceGeofence:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceGeofence:list' OR m.component LIKE '%/deviceGeofence/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceGeofence:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000254, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceGeofence:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceGeofence:list' OR m.component LIKE '%/deviceGeofence/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceGeofence:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000255, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:deviceGeofence:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:deviceGeofence:list' OR m.component LIKE '%/deviceGeofence/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:deviceGeofence:export');

UPDATE sys_menu SET perms = 'ygb:platformDocument:list', update_time = NOW() WHERE component LIKE '%/platformDocument/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000261, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformDocument:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformDocument:list' OR m.component LIKE '%/platformDocument/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformDocument:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000262, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformDocument:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformDocument:list' OR m.component LIKE '%/platformDocument/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformDocument:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000263, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformDocument:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformDocument:list' OR m.component LIKE '%/platformDocument/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformDocument:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000264, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformDocument:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformDocument:list' OR m.component LIKE '%/platformDocument/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformDocument:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000265, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformDocument:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformDocument:list' OR m.component LIKE '%/platformDocument/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformDocument:export');

UPDATE sys_menu SET perms = 'ygb:platformExchange:list', update_time = NOW() WHERE component LIKE '%/platformExchange/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000271, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformExchange:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformExchange:list' OR m.component LIKE '%/platformExchange/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformExchange:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000272, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformExchange:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformExchange:list' OR m.component LIKE '%/platformExchange/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformExchange:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000273, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformExchange:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformExchange:list' OR m.component LIKE '%/platformExchange/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformExchange:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000274, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformExchange:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformExchange:list' OR m.component LIKE '%/platformExchange/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformExchange:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000275, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformExchange:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformExchange:list' OR m.component LIKE '%/platformExchange/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformExchange:export');

UPDATE sys_menu SET perms = 'ygb:platformSecurityAudit:list', update_time = NOW() WHERE component LIKE '%/platformSecurityAudit/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000281, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformSecurityAudit:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformSecurityAudit:list' OR m.component LIKE '%/platformSecurityAudit/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformSecurityAudit:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000282, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformSecurityAudit:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformSecurityAudit:list' OR m.component LIKE '%/platformSecurityAudit/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformSecurityAudit:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000283, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformSecurityAudit:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformSecurityAudit:list' OR m.component LIKE '%/platformSecurityAudit/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformSecurityAudit:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000284, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformSecurityAudit:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformSecurityAudit:list' OR m.component LIKE '%/platformSecurityAudit/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformSecurityAudit:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000285, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformSecurityAudit:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformSecurityAudit:list' OR m.component LIKE '%/platformSecurityAudit/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformSecurityAudit:export');

UPDATE sys_menu SET perms = 'ygb:platformBackup:list', update_time = NOW() WHERE component LIKE '%/platformBackup/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000291, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformBackup:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformBackup:list' OR m.component LIKE '%/platformBackup/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformBackup:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000292, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformBackup:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformBackup:list' OR m.component LIKE '%/platformBackup/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformBackup:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000293, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformBackup:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformBackup:list' OR m.component LIKE '%/platformBackup/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformBackup:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000294, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformBackup:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformBackup:list' OR m.component LIKE '%/platformBackup/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformBackup:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000295, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:platformBackup:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:platformBackup:list' OR m.component LIKE '%/platformBackup/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:platformBackup:export');

UPDATE sys_menu SET perms = 'ygb:personBlacklist:list', update_time = NOW() WHERE component LIKE '%/personBlacklist/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000301, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personBlacklist:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personBlacklist:list' OR m.component LIKE '%/personBlacklist/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personBlacklist:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000302, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personBlacklist:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personBlacklist:list' OR m.component LIKE '%/personBlacklist/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personBlacklist:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000303, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personBlacklist:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personBlacklist:list' OR m.component LIKE '%/personBlacklist/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personBlacklist:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000304, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personBlacklist:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personBlacklist:list' OR m.component LIKE '%/personBlacklist/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personBlacklist:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000305, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personBlacklist:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personBlacklist:list' OR m.component LIKE '%/personBlacklist/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personBlacklist:export');

UPDATE sys_menu SET perms = 'ygb:personCertificate:list', update_time = NOW() WHERE component LIKE '%/personCertificate/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000311, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personCertificate:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personCertificate:list' OR m.component LIKE '%/personCertificate/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personCertificate:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000312, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personCertificate:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personCertificate:list' OR m.component LIKE '%/personCertificate/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personCertificate:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000313, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personCertificate:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personCertificate:list' OR m.component LIKE '%/personCertificate/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personCertificate:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000314, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personCertificate:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personCertificate:list' OR m.component LIKE '%/personCertificate/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personCertificate:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000315, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personCertificate:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personCertificate:list' OR m.component LIKE '%/personCertificate/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personCertificate:export');

UPDATE sys_menu SET perms = 'ygb:personTraining:list', update_time = NOW() WHERE component LIKE '%/personTraining/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000321, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personTraining:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personTraining:list' OR m.component LIKE '%/personTraining/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personTraining:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000322, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personTraining:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personTraining:list' OR m.component LIKE '%/personTraining/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personTraining:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000323, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personTraining:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personTraining:list' OR m.component LIKE '%/personTraining/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personTraining:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000324, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personTraining:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personTraining:list' OR m.component LIKE '%/personTraining/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personTraining:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000325, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personTraining:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personTraining:list' OR m.component LIKE '%/personTraining/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personTraining:export');

UPDATE sys_menu SET perms = 'ygb:personHighRiskPost:list', update_time = NOW() WHERE component LIKE '%/personHighRiskPost/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000331, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personHighRiskPost:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personHighRiskPost:list' OR m.component LIKE '%/personHighRiskPost/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personHighRiskPost:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000332, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personHighRiskPost:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personHighRiskPost:list' OR m.component LIKE '%/personHighRiskPost/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personHighRiskPost:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000333, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personHighRiskPost:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personHighRiskPost:list' OR m.component LIKE '%/personHighRiskPost/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personHighRiskPost:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000334, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personHighRiskPost:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personHighRiskPost:list' OR m.component LIKE '%/personHighRiskPost/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personHighRiskPost:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000335, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personHighRiskPost:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personHighRiskPost:list' OR m.component LIKE '%/personHighRiskPost/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personHighRiskPost:export');

UPDATE sys_menu SET perms = 'ygb:personRiskPost:list', update_time = NOW() WHERE component LIKE '%/personRiskPost/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000341, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personRiskPost:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personRiskPost:list' OR m.component LIKE '%/personRiskPost/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personRiskPost:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000342, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personRiskPost:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personRiskPost:list' OR m.component LIKE '%/personRiskPost/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personRiskPost:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000343, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personRiskPost:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personRiskPost:list' OR m.component LIKE '%/personRiskPost/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personRiskPost:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000344, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personRiskPost:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personRiskPost:list' OR m.component LIKE '%/personRiskPost/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personRiskPost:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000345, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personRiskPost:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personRiskPost:list' OR m.component LIKE '%/personRiskPost/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personRiskPost:export');

UPDATE sys_menu SET perms = 'ygb:personExpert:list', update_time = NOW() WHERE component LIKE '%/personExpert/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000351, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personExpert:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personExpert:list' OR m.component LIKE '%/personExpert/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personExpert:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000352, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personExpert:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personExpert:list' OR m.component LIKE '%/personExpert/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personExpert:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000353, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personExpert:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personExpert:list' OR m.component LIKE '%/personExpert/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personExpert:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000354, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personExpert:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personExpert:list' OR m.component LIKE '%/personExpert/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personExpert:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000355, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:personExpert:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:personExpert:list' OR m.component LIKE '%/personExpert/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:personExpert:export');

UPDATE sys_menu SET perms = 'ygb:newformTraining:list', update_time = NOW() WHERE component LIKE '%/newformTraining/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000361, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:newformTraining:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:newformTraining:list' OR m.component LIKE '%/newformTraining/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:newformTraining:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000362, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:newformTraining:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:newformTraining:list' OR m.component LIKE '%/newformTraining/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:newformTraining:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000363, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:newformTraining:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:newformTraining:list' OR m.component LIKE '%/newformTraining/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:newformTraining:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000364, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:newformTraining:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:newformTraining:list' OR m.component LIKE '%/newformTraining/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:newformTraining:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000365, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:newformTraining:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:newformTraining:list' OR m.component LIKE '%/newformTraining/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:newformTraining:export');

UPDATE sys_menu SET perms = 'ygb:occupationPrevention:list', update_time = NOW() WHERE component LIKE '%/occupationPrevention/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000371, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:occupationPrevention:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:occupationPrevention:list' OR m.component LIKE '%/occupationPrevention/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:occupationPrevention:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000372, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:occupationPrevention:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:occupationPrevention:list' OR m.component LIKE '%/occupationPrevention/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:occupationPrevention:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000373, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:occupationPrevention:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:occupationPrevention:list' OR m.component LIKE '%/occupationPrevention/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:occupationPrevention:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000374, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:occupationPrevention:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:occupationPrevention:list' OR m.component LIKE '%/occupationPrevention/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:occupationPrevention:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000375, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:occupationPrevention:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:occupationPrevention:list' OR m.component LIKE '%/occupationPrevention/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:occupationPrevention:export');

UPDATE sys_menu SET perms = 'ygb:occupationHealthArchive:list', update_time = NOW() WHERE component LIKE '%/occupationHealthArchive/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000381, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:occupationHealthArchive:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:occupationHealthArchive:list' OR m.component LIKE '%/occupationHealthArchive/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:occupationHealthArchive:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000382, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:occupationHealthArchive:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:occupationHealthArchive:list' OR m.component LIKE '%/occupationHealthArchive/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:occupationHealthArchive:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000383, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:occupationHealthArchive:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:occupationHealthArchive:list' OR m.component LIKE '%/occupationHealthArchive/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:occupationHealthArchive:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000384, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:occupationHealthArchive:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:occupationHealthArchive:list' OR m.component LIKE '%/occupationHealthArchive/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:occupationHealthArchive:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000385, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:occupationHealthArchive:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:occupationHealthArchive:list' OR m.component LIKE '%/occupationHealthArchive/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:occupationHealthArchive:export');

UPDATE sys_menu SET perms = 'ygb:operationEnterpriseReview:list', update_time = NOW() WHERE component LIKE '%/operationEnterpriseReview/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000391, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationEnterpriseReview:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationEnterpriseReview:list' OR m.component LIKE '%/operationEnterpriseReview/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationEnterpriseReview:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000392, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationEnterpriseReview:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationEnterpriseReview:list' OR m.component LIKE '%/operationEnterpriseReview/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationEnterpriseReview:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000393, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationEnterpriseReview:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationEnterpriseReview:list' OR m.component LIKE '%/operationEnterpriseReview/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationEnterpriseReview:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000394, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationEnterpriseReview:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationEnterpriseReview:list' OR m.component LIKE '%/operationEnterpriseReview/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationEnterpriseReview:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000395, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationEnterpriseReview:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationEnterpriseReview:list' OR m.component LIKE '%/operationEnterpriseReview/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationEnterpriseReview:export');

UPDATE sys_menu SET perms = 'ygb:operationMessage:list', update_time = NOW() WHERE component LIKE '%/operationMessage/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000401, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationMessage:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationMessage:list' OR m.component LIKE '%/operationMessage/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationMessage:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000402, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationMessage:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationMessage:list' OR m.component LIKE '%/operationMessage/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationMessage:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000403, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationMessage:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationMessage:list' OR m.component LIKE '%/operationMessage/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationMessage:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000404, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationMessage:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationMessage:list' OR m.component LIKE '%/operationMessage/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationMessage:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000405, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationMessage:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationMessage:list' OR m.component LIKE '%/operationMessage/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationMessage:export');

UPDATE sys_menu SET perms = 'ygb:operationChipDispatch:list', update_time = NOW() WHERE component LIKE '%/operationChipDispatch/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000411, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationChipDispatch:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationChipDispatch:list' OR m.component LIKE '%/operationChipDispatch/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationChipDispatch:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000412, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationChipDispatch:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationChipDispatch:list' OR m.component LIKE '%/operationChipDispatch/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationChipDispatch:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000413, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationChipDispatch:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationChipDispatch:list' OR m.component LIKE '%/operationChipDispatch/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationChipDispatch:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000414, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationChipDispatch:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationChipDispatch:list' OR m.component LIKE '%/operationChipDispatch/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationChipDispatch:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000415, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationChipDispatch:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationChipDispatch:list' OR m.component LIKE '%/operationChipDispatch/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationChipDispatch:export');

UPDATE sys_menu SET perms = 'ygb:operationMaintenanceStats:list', update_time = NOW() WHERE component LIKE '%/operationMaintenanceStats/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000421, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationMaintenanceStats:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationMaintenanceStats:list' OR m.component LIKE '%/operationMaintenanceStats/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationMaintenanceStats:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000422, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationMaintenanceStats:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationMaintenanceStats:list' OR m.component LIKE '%/operationMaintenanceStats/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationMaintenanceStats:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000423, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationMaintenanceStats:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationMaintenanceStats:list' OR m.component LIKE '%/operationMaintenanceStats/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationMaintenanceStats:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000424, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationMaintenanceStats:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationMaintenanceStats:list' OR m.component LIKE '%/operationMaintenanceStats/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationMaintenanceStats:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000425, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationMaintenanceStats:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationMaintenanceStats:list' OR m.component LIKE '%/operationMaintenanceStats/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationMaintenanceStats:export');

UPDATE sys_menu SET perms = 'ygb:preventionPublicity:list', update_time = NOW() WHERE component LIKE '%/preventionPublicity/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000431, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:preventionPublicity:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:preventionPublicity:list' OR m.component LIKE '%/preventionPublicity/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:preventionPublicity:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000432, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:preventionPublicity:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:preventionPublicity:list' OR m.component LIKE '%/preventionPublicity/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:preventionPublicity:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000433, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:preventionPublicity:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:preventionPublicity:list' OR m.component LIKE '%/preventionPublicity/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:preventionPublicity:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000434, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:preventionPublicity:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:preventionPublicity:list' OR m.component LIKE '%/preventionPublicity/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:preventionPublicity:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000435, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:preventionPublicity:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:preventionPublicity:list' OR m.component LIKE '%/preventionPublicity/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:preventionPublicity:export');

UPDATE sys_menu SET perms = 'ygb:preventionTraining:list', update_time = NOW() WHERE component LIKE '%/preventionTraining/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000441, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:preventionTraining:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:preventionTraining:list' OR m.component LIKE '%/preventionTraining/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:preventionTraining:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000442, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:preventionTraining:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:preventionTraining:list' OR m.component LIKE '%/preventionTraining/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:preventionTraining:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000443, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:preventionTraining:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:preventionTraining:list' OR m.component LIKE '%/preventionTraining/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:preventionTraining:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000444, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:preventionTraining:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:preventionTraining:list' OR m.component LIKE '%/preventionTraining/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:preventionTraining:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000445, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:preventionTraining:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:preventionTraining:list' OR m.component LIKE '%/preventionTraining/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:preventionTraining:export');

UPDATE sys_menu SET perms = 'ygb:preventionAi:list', update_time = NOW() WHERE component LIKE '%/preventionAi/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000451, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:preventionAi:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:preventionAi:list' OR m.component LIKE '%/preventionAi/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:preventionAi:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000452, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:preventionAi:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:preventionAi:list' OR m.component LIKE '%/preventionAi/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:preventionAi:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000453, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:preventionAi:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:preventionAi:list' OR m.component LIKE '%/preventionAi/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:preventionAi:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000454, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:preventionAi:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:preventionAi:list' OR m.component LIKE '%/preventionAi/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:preventionAi:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000455, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:preventionAi:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:preventionAi:list' OR m.component LIKE '%/preventionAi/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:preventionAi:export');

UPDATE sys_menu SET perms = 'ygb:operationJobCategory:list', update_time = NOW() WHERE component LIKE '%/operationJobCategory/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000461, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationJobCategory:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationJobCategory:list' OR m.component LIKE '%/operationJobCategory/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationJobCategory:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000462, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationJobCategory:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationJobCategory:list' OR m.component LIKE '%/operationJobCategory/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationJobCategory:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000463, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationJobCategory:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationJobCategory:list' OR m.component LIKE '%/operationJobCategory/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationJobCategory:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000464, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationJobCategory:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationJobCategory:list' OR m.component LIKE '%/operationJobCategory/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationJobCategory:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000465, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationJobCategory:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationJobCategory:list' OR m.component LIKE '%/operationJobCategory/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationJobCategory:export');

UPDATE sys_menu SET perms = 'ygb:operationRecruitStats:list', update_time = NOW() WHERE component LIKE '%/operationRecruitStats/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000471, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationRecruitStats:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationRecruitStats:list' OR m.component LIKE '%/operationRecruitStats/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationRecruitStats:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000472, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationRecruitStats:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationRecruitStats:list' OR m.component LIKE '%/operationRecruitStats/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationRecruitStats:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000473, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationRecruitStats:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationRecruitStats:list' OR m.component LIKE '%/operationRecruitStats/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationRecruitStats:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000474, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationRecruitStats:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationRecruitStats:list' OR m.component LIKE '%/operationRecruitStats/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationRecruitStats:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000475, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:operationRecruitStats:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:operationRecruitStats:list' OR m.component LIKE '%/operationRecruitStats/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:operationRecruitStats:export');

UPDATE sys_menu SET perms = 'ygb:unionOrg:list', update_time = NOW() WHERE component LIKE '%/unionOrg/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000481, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionOrg:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionOrg:list' OR m.component LIKE '%/unionOrg/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionOrg:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000482, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionOrg:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionOrg:list' OR m.component LIKE '%/unionOrg/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionOrg:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000483, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionOrg:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionOrg:list' OR m.component LIKE '%/unionOrg/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionOrg:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000484, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionOrg:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionOrg:list' OR m.component LIKE '%/unionOrg/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionOrg:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000485, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionOrg:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionOrg:list' OR m.component LIKE '%/unionOrg/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionOrg:export');

UPDATE sys_menu SET perms = 'ygb:unionSupervision:list', update_time = NOW() WHERE component LIKE '%/unionSupervision/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000491, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionSupervision:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionSupervision:list' OR m.component LIKE '%/unionSupervision/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionSupervision:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000492, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionSupervision:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionSupervision:list' OR m.component LIKE '%/unionSupervision/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionSupervision:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000493, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionSupervision:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionSupervision:list' OR m.component LIKE '%/unionSupervision/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionSupervision:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000494, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionSupervision:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionSupervision:list' OR m.component LIKE '%/unionSupervision/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionSupervision:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000495, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionSupervision:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionSupervision:list' OR m.component LIKE '%/unionSupervision/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionSupervision:export');

UPDATE sys_menu SET perms = 'ygb:unionLegalAid:list', update_time = NOW() WHERE component LIKE '%/unionLegalAid/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000501, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionLegalAid:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionLegalAid:list' OR m.component LIKE '%/unionLegalAid/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionLegalAid:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000502, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionLegalAid:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionLegalAid:list' OR m.component LIKE '%/unionLegalAid/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionLegalAid:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000503, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionLegalAid:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionLegalAid:list' OR m.component LIKE '%/unionLegalAid/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionLegalAid:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000504, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionLegalAid:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionLegalAid:list' OR m.component LIKE '%/unionLegalAid/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionLegalAid:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000505, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionLegalAid:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionLegalAid:list' OR m.component LIKE '%/unionLegalAid/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionLegalAid:export');

UPDATE sys_menu SET perms = 'ygb:unionNegotiation:list', update_time = NOW() WHERE component LIKE '%/unionNegotiation/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000511, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionNegotiation:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionNegotiation:list' OR m.component LIKE '%/unionNegotiation/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionNegotiation:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000512, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionNegotiation:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionNegotiation:list' OR m.component LIKE '%/unionNegotiation/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionNegotiation:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000513, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionNegotiation:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionNegotiation:list' OR m.component LIKE '%/unionNegotiation/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionNegotiation:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000514, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionNegotiation:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionNegotiation:list' OR m.component LIKE '%/unionNegotiation/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionNegotiation:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000515, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionNegotiation:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionNegotiation:list' OR m.component LIKE '%/unionNegotiation/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionNegotiation:export');

UPDATE sys_menu SET perms = 'ygb:unionPreventionSupervision:list', update_time = NOW() WHERE component LIKE '%/unionPreventionSupervision/index' AND menu_type = 'C';
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000521, '??', m.menu_id, 1, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionPreventionSupervision:query', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionPreventionSupervision:list' OR m.component LIKE '%/unionPreventionSupervision/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionPreventionSupervision:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000522, '??', m.menu_id, 2, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionPreventionSupervision:add', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionPreventionSupervision:list' OR m.component LIKE '%/unionPreventionSupervision/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionPreventionSupervision:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000523, '??', m.menu_id, 3, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionPreventionSupervision:edit', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionPreventionSupervision:list' OR m.component LIKE '%/unionPreventionSupervision/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionPreventionSupervision:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000524, '??', m.menu_id, 4, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionPreventionSupervision:remove', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionPreventionSupervision:list' OR m.component LIKE '%/unionPreventionSupervision/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionPreventionSupervision:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, portal_scope, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 9000525, '??', m.menu_id, 5, '', '', '', '', 1, 0, 'F', '0', '0', m.portal_scope, 'ygb:unionPreventionSupervision:export', '#', 'admin', NOW(), 'admin', NOW(), 'phase52 business action permission'
FROM sys_menu m WHERE m.menu_type = 'C' AND (m.perms = 'ygb:unionPreventionSupervision:list' OR m.component LIKE '%/unionPreventionSupervision/index') AND NOT EXISTS (SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.perms = 'ygb:unionPreventionSupervision:export');
