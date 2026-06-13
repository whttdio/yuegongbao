set names utf8mb4;

update sys_menu
set menu_name = case menu_id
  when 2014 then '设备管理'
  when 4303 then '岗位审核'
  when 4304 then '简历管理'
  when 4305 then '广告轮播'
  when 4306 then '消息推送'
  when 4311 then '查询'
  when 4312 then '新增'
  when 4313 then '修改'
  when 4314 then '删除'
  when 4315 then '导出'
  when 4321 then '查询'
  when 4322 then '导出'
  when 4331 then '查询'
  when 4332 then '导出'
  when 4341 then '查询'
  when 4342 then '新增'
  when 4343 then '修改'
  when 4344 then '删除'
  when 4345 then '导出'
  when 4400 then '平台治理'
  when 4401 then '治理总览'
  when 4402 then '文档管理'
  when 4403 then '交换监控'
  when 4404 then '安全审计'
  when 4405 then '备份恢复'
  when 4411 then '查询'
  when 4412 then '新增'
  when 4413 then '修改'
  when 4414 then '删除'
  when 4415 then '导出'
  when 4421 then '查询'
  when 4422 then '新增'
  when 4423 then '修改'
  when 4424 then '删除'
  when 4425 then '导出'
  when 4431 then '查询'
  when 4432 then '新增'
  when 4433 then '修改'
  when 4434 then '删除'
  when 4435 then '导出'
  when 4441 then '查询'
  when 4442 then '新增'
  when 4443 then '修改'
  when 4444 then '删除'
  when 4445 then '导出'
  when 4460 then '赔付率监控'
  when 4461 then '查询'
  when 4462 then '新增'
  when 4463 then '修改'
  when 4464 then '删除'
  when 4465 then '导出'
  when 4471 then '职业伤害监测'
  when 4472 then '培训管理'
  when 4473 then '查询'
  when 4474 then '导出'
  when 4475 then '查询'
  when 4476 then '导出'
  when 4477 then '查询'
  when 4478 then '新增'
  when 4479 then '修改'
  when 4480 then '删除'
  when 4481 then '导出'
  when 4482 then '职业病预防项目'
  when 4483 then '职业健康档案'
  when 4484 then '查询'
  when 4485 then '导出'
  when 4486 then '查询'
  when 4487 then '导出'
  when 4491 then '黑名单'
  when 4492 then '培训监督'
  when 4493 then '高危岗位库'
  when 4494 then '风险岗位库'
  when 4495 then '专家库'
  when 4496 then '查询'
  when 4497 then '新增'
  when 4498 then '修改'
  when 4499 then '删除'
  when 4500 then '导出'
  when 4501 then '查询'
  when 4502 then '新增'
  when 4503 then '修改'
  when 4504 then '删除'
  when 4505 then '导出'
  when 4506 then '查询'
  when 4507 then '新增'
  when 4508 then '修改'
  when 4509 then '删除'
  when 4510 then '导出'
  when 4511 then '查询'
  when 4512 then '新增'
  when 4513 then '修改'
  when 4514 then '删除'
  when 4515 then '导出'
  when 4516 then '查询'
  when 4517 then '新增'
  when 4518 then '修改'
  when 4519 then '删除'
  when 4520 then '导出'
  when 4521 then '查询'
  when 4522 then '新增'
  when 4523 then '修改'
  when 4524 then '删除'
  when 4525 then '导出'
  when 4527 then '芯片设备'
  when 4528 then 'AI设备'
  when 4529 then '物联卡'
  when 4530 then '芯片库存'
  when 4531 then '电子围栏'
  when 4532 then '拆卸报警'
  when 4534 then '劳务派遣公司'
  when 4535 then '用工单位'
  when 4536 then '高危企业库'
  else menu_name
end
where menu_id in (
  2014,
  4303, 4304, 4305, 4306,
  4311, 4312, 4313, 4314, 4315,
  4321, 4322,
  4331, 4332,
  4341, 4342, 4343, 4344, 4345,
  4400, 4401, 4402, 4403, 4404, 4405,
  4411, 4412, 4413, 4414, 4415,
  4421, 4422, 4423, 4424, 4425,
  4431, 4432, 4433, 4434, 4435,
  4441, 4442, 4443, 4444, 4445,
  4460, 4461, 4462, 4463, 4464, 4465,
  4471, 4472, 4473, 4474, 4475, 4476, 4477, 4478, 4479, 4480, 4481,
  4482, 4483, 4484, 4485, 4486, 4487,
  4491, 4492, 4493, 4494, 4495,
  4496, 4497, 4498, 4499, 4500,
  4501, 4502, 4503, 4504, 4505,
  4506, 4507, 4508, 4509, 4510,
  4511, 4512, 4513, 4514, 4515,
  4516, 4517, 4518, 4519, 4520,
  4521, 4522, 4523, 4524, 4525,
  4527, 4528, 4529, 4530, 4531, 4532,
  4534, 4535, 4536
);
