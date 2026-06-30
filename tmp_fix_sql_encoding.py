from pathlib import Path

FILE_PATHS = [
    Path('sql/sys_menu.sql'),
    Path('sql/ygb_phase46_p4_union_enterprise_citizen.sql'),
]

# Common garbled UTF-8-as-latin1 patterns observed in seed files
PATTERNS = [
    ('20.1 ä¼±ä¸ä»ªè¡¨çï¼æ°ï¼', '20.1 企业仪表盘（新）'),
    ('21.1 ææ°å°å¾ï¼å±ç¤ºé¡µï¼', '21.1 暖新地图（展示页）'),
    ('21.2 å¹è®­è¯¾ç¨ï¼å±ç¤ºé¡µï¼', '21.2 培训课程（展示页）'),
    ('21.3 æ³è§åºï¼å±ç¤ºé¡µï¼', '21.3 法规库（展示页）'),
    ('21.4 äºå©åï¼å±ç¤ºé¡µï¼', '21.4 互助圈（展示页）'),
    ('21.5 æèç¨å·¥å¸åºï¼å±ç¤ºé¡µï¼', '21.5 招聘用工市场（展示页）'),
]

for fp in FILE_PATHS:
    content = fp.read_text(encoding='utf-8')
    original = content
    for old, new in PATTERNS:
        content = content.replace(old, new)
    if content != original:
        fp.write_text(content, encoding='utf-8')
        print(f'Fixed {fp.name}')
    else:
        print(f'No changes {fp.name}')
