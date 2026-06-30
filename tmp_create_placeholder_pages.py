import os
from pathlib import Path

UI_DIR = Path(r'D:\JAVA\YueGongBao-Vue\YueGongBao-Vue\yuegongbao-ui\src\views')
INPUT_FILE = r'D:\JAVA\YueGongBao-Vue\YueGongBao-Vue\tmp_new_menu_components.txt'

PLACEHOLDER_TEMPLATE = '''<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">{module_name}</p>
        <h1 class="ygb-page__title">{page_title}</h1>
        <p class="ygb-page__desc">{desc}</p>
      </div>
    </section>

    <el-card shadow="never">
      <el-empty description="该功能页面正在建设中，敬请期待。">
        <template #image>
          <div style="font-size: 64px">🚧</div>
        </template>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup name="{route_name}">
// TODO: 按文档要求实现 {module_name} - {page_title}
</script>
'''

def parse_components(file_path):
    components = []
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()[1:]  # skip header
        for line in lines:
            line = line.strip()
            if not line:
                continue
            parts = line.split('\t')
            if len(parts) >= 3:
                menu_id = parts[0]
                menu_name = parts[1]
                component = parts[2]
                components.append((menu_id, menu_name, component))
    return components

def component_file_exists(component):
    if not component:
        return False
    # Component path may map to either:
    # - views/<component>.vue (e.g., ygb/enterprise/index -> views/ygb/enterprise/index.vue)
    # - views/<component>/index.vue (e.g., ygb/enterprisePortal/overview/index -> views/ygb/enterprisePortal/overview/index.vue)
    candidates = [
        UI_DIR / f'{component}.vue',
        UI_DIR / component / 'index.vue',
    ]
    return any(p.exists() for p in candidates)

def route_name_from_component(component):
    parts = component.split('/')
    name = ''.join(p.capitalize() for p in parts)
    return f'Ygb{name}'

def main():
    components = parse_components(INPUT_FILE)
    missing = []
    for menu_id, menu_name, component in components:
        file_path = component_to_file_path(component)
        if file_path and not file_path.exists():
            missing.append((menu_id, menu_name, component, file_path))

    print(f"Total components: {len(components)}")
    print(f"Missing files: {len(missing)}")

    # Try to infer module name from component path
    for menu_id, menu_name, component, file_path in missing:
        parts = component.split('/')
        module_name = parts[1] if len(parts) > 1 else ''
        page_title = menu_name
        desc = f"{module_name} 模块下的 {page_title} 功能页面。"
        route_name = route_name_from_component(component)
        content = PLACEHOLDER_TEMPLATE.format(
            module_name=module_name,
            page_title=page_title,
            desc=desc,
            route_name=route_name
        )
        file_path.parent.mkdir(parents=True, exist_ok=True)
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Created placeholder: {file_path}")

if __name__ == '__main__':
    main()
