const EMPTY_WORKBENCH = Object.freeze({
  eyebrow: '',
  title: '',
  description: '',
  versionTag: '',
  phaseLabel: '',
  phaseValue: '',
  phaseDescription: '',
  modules: [],
  tasks: [],
  stack: [],
  policies: []
})

export const homeWorkbenchProps = {
  appTitle: {
    type: String,
    default: ''
  },
  workbench: {
    type: Object,
    default: () => ({ ...EMPTY_WORKBENCH })
  },
  homeLoading: {
    type: Boolean,
    default: false
  },
  currentRoleLabels: {
    type: Array,
    default: () => []
  },
  homeSummaryText: {
    type: String,
    default: ''
  },
  homeError: {
    type: String,
    default: ''
  },
  metricCards: {
    type: Array,
    default: () => []
  },
  portalExplanationTitle: {
    type: String,
    default: ''
  },
  portalExplanationDescription: {
    type: String,
    default: ''
  },
  portalExplanationItems: {
    type: Array,
    default: () => []
  },
  workspaceSectionTitle: {
    type: String,
    default: ''
  },
  workspaceSectionHint: {
    type: String,
    default: ''
  },
  availableQuickSections: {
    type: Array,
    default: () => []
  },
  activePlaybooks: {
    type: Array,
    default: () => []
  },
  focusPanels: {
    type: Array,
    default: () => []
  },
  queueSectionTitle: {
    type: String,
    default: ''
  },
  queueSectionHint: {
    type: String,
    default: ''
  },
  homeQueueSections: {
    type: Array,
    default: () => []
  }
}
