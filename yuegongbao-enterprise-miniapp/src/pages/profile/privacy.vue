<template>
  <!-- 联调快照：企业小程序页面已纳入接口联调与真机验收台账 -->
  <view class="worker-page">
    <view class="worker-card">
      <view class="worker-title">隐私协议</view>
      <view class="worker-subtitle">
        本协议用于说明“阳光劳务”劳动者端在考勤、工资、社保、个税、培训、求职、投诉举报等场景中的信息处理规则。
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">1. 收集的信息</view>
      <view class="article-paragraph">
        1.1 为完成实名认证、本人信息展示和用工关系识别，平台会处理姓名、手机号、身份证脱敏信息、所属企业、工种等基础资料。
      </view>
      <view class="article-paragraph">
        1.2 为完成考勤打卡、培训学习、工资社保个税查询等服务，平台会处理打卡记录、培训进度、工资发放信息、参保状态、个税明细等业务数据。
      </view>
      <view class="article-paragraph">
        1.3 为完成投诉举报、法律咨询、工伤留痕等功能，平台会处理用户主动上传的图片、附件、描述内容和联系方式。
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">2. 信息使用方式</view>
      <view class="article-paragraph">
        2.1 仅用于提供劳动者本人查询、培训联动校验、维权协同、活动参与、工会服务等与平台业务直接相关的功能。
      </view>
      <view class="article-paragraph">
        2.2 平台不会将劳动者个人信息用于与用工保障无关的商业营销场景。
      </view>
      <view class="article-paragraph">
        2.3 涉及工资、身份证号、银行卡号等敏感信息时，页面默认按脱敏规则展示。
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">3. 信息存储与保护</view>
      <view class="article-paragraph">
        3.1 平台按最小必要原则保存业务数据，考勤、工资、投诉、咨询、上传附件等记录仅用于业务办理与审计留痕。
      </view>
      <view class="article-paragraph">
        3.2 上传的图片、附件会用于投诉举报、法律咨询、工伤证据或现场留痕，不得上传与业务无关的他人隐私材料。
      </view>
      <view class="article-paragraph">
        3.3 如发生账号异常、身份信息错误或资料误绑定，可通过帮助中心、法律咨询、工会热线等渠道发起核查。
      </view>
    </view>

    <view class="worker-card">
      <view class="worker-title">4. 用户权利</view>
      <view class="article-paragraph">
        4.1 用户可以查看本人档案、工资、社保、个税、培训、投递记录和上传归档记录。
      </view>
      <view class="article-paragraph">
        4.2 用户可以在“我的简历”“帮助中心”“设置”等页面维护部分资料、提交意见反馈或关闭消息通知。
      </view>
      <view class="article-paragraph">
        4.3 如需进一步处理个人信息相关问题，可优先通过帮助中心反馈，或拨打工会服务热线 12351。
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'

const privacyLastViewedAt = ref('')
const privacyLastMessage = ref('')

const privacySnapshotText = computed(() => {
  return [
    '## 隐私协议验收摘要',
    `- 最近查看：${privacyLastViewedAt.value || '-'}`,
    '- 章节数量：4 章 / 12 条说明',
    '- 覆盖主题：收集信息 / 使用方式 / 存储保护 / 用户权利',
    '- 关联入口：我的 / 设置 / 帮助中心',
    `- 说明：${privacyLastMessage.value || '-'}`,
    '- 链路关联：我的 / 设置 / 帮助中心 / 隐私协议'
  ].join('\n')
})

function loadSnapshot() {
  privacyLastViewedAt.value = new Date().toLocaleString()
  privacyLastMessage.value = '隐私协议页面已打开，可核对说明书与页面静态口径是否一致'
}

function copyText(content, successTitle) {
  if (!content) {
    uni.showToast({ title: '暂无可复制内容', icon: 'none' })
    return
  }
  uni.setClipboardData({
    data: content,
    success: () => uni.showToast({ title: successTitle, icon: 'none' }),
    fail: () => uni.showToast({ title: '复制失败，请改用截图', icon: 'none' })
  })
}

onShow(loadSnapshot)
</script>

<style lang="scss">
.worker-card + .worker-card {
  margin-top: 24rpx;
}

.article-paragraph {
  margin-top: 18rpx;
  font-size: 26rpx;
  line-height: 1.8;
  color: #183247;
}
</style>
