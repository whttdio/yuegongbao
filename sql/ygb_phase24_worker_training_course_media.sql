-- 为培训课程补充可访问的封面图与示例视频（稳定外链）
UPDATE ygb_portal_content
SET cover_url = 'https://images.unsplash.com/photo-1581092580497-e0d23cbdf1dc?auto=format&fit=crop&w=1200&q=80',
    extra_json = JSON_SET(
        COALESCE(NULLIF(extra_json, ''), '{}'),
        '$.videoUrl', 'https://www.w3schools.com/html/mov_bbb.mp4',
        '$.posterUrl', 'https://images.unsplash.com/photo-1581092580497-e0d23cbdf1dc?auto=format&fit=crop&w=1200&q=80'
    )
WHERE portal_code = 'ygb'
  AND section_code = 'worker_training_course'
  AND category_code = 'heatstroke-course';

UPDATE ygb_portal_content
SET cover_url = 'https://images.unsplash.com/photo-1450101499163-c8848c66ca85?auto=format&fit=crop&w=1200&q=80',
    extra_json = JSON_SET(
        COALESCE(NULLIF(extra_json, ''), '{}'),
        '$.videoUrl', 'https://www.w3schools.com/html/movie.mp4',
        '$.posterUrl', 'https://images.unsplash.com/photo-1450101499163-c8848c66ca85?auto=format&fit=crop&w=1200&q=80'
    )
WHERE portal_code = 'ygb'
  AND section_code = 'worker_training_course'
  AND category_code = 'rights-course';

UPDATE ygb_portal_content
SET cover_url = 'https://images.unsplash.com/photo-1581092580497-e0d23cbdf1dc?auto=format&fit=crop&w=1200&q=80',
    extra_json = JSON_SET(
        COALESCE(NULLIF(extra_json, ''), '{}'),
        '$.videoUrl', 'https://www.w3schools.com/html/mov_bbb.mp4',
        '$.posterUrl', 'https://images.unsplash.com/photo-1581092580497-e0d23cbdf1dc?auto=format&fit=crop&w=1200&q=80'
    )
WHERE portal_code = 'ygb'
  AND section_code = 'worker_training_course'
  AND category_code = 'course-1';

UPDATE ygb_portal_content
SET cover_url = 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?auto=format&fit=crop&w=1200&q=80',
    extra_json = JSON_SET(
        COALESCE(NULLIF(extra_json, ''), '{}'),
        '$.videoUrl', 'https://www.w3schools.com/html/movie.mp4',
        '$.posterUrl', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?auto=format&fit=crop&w=1200&q=80'
    )
WHERE portal_code = 'ygb'
  AND section_code = 'worker_training_course'
  AND category_code = 'course-2';

UPDATE ygb_portal_content
SET cover_url = 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=1200&q=80',
    extra_json = JSON_SET(
        COALESCE(NULLIF(extra_json, ''), '{}'),
        '$.videoUrl', 'https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4',
        '$.posterUrl', 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=1200&q=80'
    )
WHERE portal_code = 'ygb'
  AND section_code = 'worker_training_course'
  AND category_code = 'course-3';
