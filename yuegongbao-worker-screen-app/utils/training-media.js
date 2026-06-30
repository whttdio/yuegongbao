const COURSE_MEDIA = {
  'heatstroke-course': {
    coverUrl: 'https://images.unsplash.com/photo-1581092580497-e0d23cbdf1dc?auto=format&fit=crop&w=800&q=80',
    posterUrl: 'https://images.unsplash.com/photo-1581092580497-e0d23cbdf1dc?auto=format&fit=crop&w=1200&q=80',
    videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4'
  },
  'rights-course': {
    coverUrl: 'https://images.unsplash.com/photo-1450101499163-c8848c66ca85?auto=format&fit=crop&w=800&q=80',
    posterUrl: 'https://images.unsplash.com/photo-1450101499163-c8848c66ca85?auto=format&fit=crop&w=1200&q=80',
    videoUrl: 'https://www.w3schools.com/html/movie.mp4'
  },
  'course-1': {
    coverUrl: 'https://images.unsplash.com/photo-1581092580497-e0d23cbdf1dc?auto=format&fit=crop&w=800&q=80',
    posterUrl: 'https://images.unsplash.com/photo-1581092580497-e0d23cbdf1dc?auto=format&fit=crop&w=1200&q=80',
    videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4'
  },
  'course-2': {
    coverUrl: 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?auto=format&fit=crop&w=800&q=80',
    posterUrl: 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?auto=format&fit=crop&w=1200&q=80',
    videoUrl: 'https://www.w3schools.com/html/movie.mp4'
  },
  'course-3': {
    coverUrl: 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=800&q=80',
    posterUrl: 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=1200&q=80',
    videoUrl: 'https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4'
  }
}

function isBrokenMediaUrl(url) {
  const value = String(url || '').trim()
  if (!value) {
    return true
  }
  return value.includes('cdn.ygb.local') || value.includes('samplelib.com')
}

export function resolveCourseCoverUrl(item = {}) {
  const defaults = COURSE_MEDIA[item.courseKey] || {}
  const coverUrl = item.coverUrl || item.posterUrl || defaults.coverUrl || ''
  return isBrokenMediaUrl(coverUrl) ? (defaults.coverUrl || '') : coverUrl
}

export function normalizeCourseMedia(item = {}) {
  const defaults = COURSE_MEDIA[item.courseKey]
  if (!defaults) {
    return {
      ...item,
      coverUrl: isBrokenMediaUrl(item.coverUrl) ? '' : (item.coverUrl || item.posterUrl || '')
    }
  }
  return {
    ...item,
    coverUrl: isBrokenMediaUrl(item.coverUrl) ? defaults.coverUrl : (item.coverUrl || defaults.coverUrl),
    posterUrl: isBrokenMediaUrl(item.posterUrl) ? defaults.posterUrl : (item.posterUrl || defaults.posterUrl),
    videoUrl: isBrokenMediaUrl(item.videoUrl) ? defaults.videoUrl : (item.videoUrl || defaults.videoUrl)
  }
}

export function normalizeCourseMediaList(rows = []) {
  return (rows || []).map((item) => normalizeCourseMedia(item))
}
