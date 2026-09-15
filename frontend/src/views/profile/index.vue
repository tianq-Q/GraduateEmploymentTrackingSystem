<!--
MIT License

Copyright (c) 2026 Employment Tracking System

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->

<template>
  <div class="page-container">
    <h2>个人中心</h2>
    <p>查看并维护您的账户资料。</p>

    <!-- 学生/毕业生：档案信息（学号/姓名/性别/学院/专业/班级/电话/邮箱） -->
    <el-descriptions v-if="isStudent" :column="1" border style="max-width: 560px; margin-top: 16px">
      <el-descriptions-item label="学号">
        {{ profile?.studentNumber ?? auth.userInfo?.username ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="姓名">
        {{ profile?.name ?? auth.userInfo?.realName ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="性别">
        {{ profile?.gender ?? auth.userInfo?.gender ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="学院">
        {{ profile?.deptName ?? auth.userInfo?.deptName ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="专业">
        {{ profile?.majorName ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="班级">
        {{ profile?.className ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="电话">
        {{ profile?.phone ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="邮箱">
        {{ profile?.email ?? '-' }}
      </el-descriptions-item>
    </el-descriptions>

    <!-- 学生：我的毕业生跟踪记录 -->
    <template v-if="isStudent">
      <div v-if="myTrack" class="track-section">
        <div class="track-section__header">
          <el-icon class="track-section__icon"><AlarmClock /></el-icon>
          <span>我的毕业生跟踪记录</span>
          <el-tag size="small" :type="myTrack.status === 'FINISHED' ? 'info' : 'success'" style="margin-left: 10px">
            {{ myTrack.statusName }}
          </el-tag>
        </div>
        <div class="track-section__desc">
          {{ myTrack.graduateYear }} 届毕业生，跟踪期 {{ myTrack.startYear }} ~ {{ myTrack.endYear }}（共 {{ myTrack.trackYears }} 年），
          已跟踪 {{ myTrack.trackedYears }} 年
          <template v-if="myTrack.status !== 'FINISHED'">，剩余 {{ myTrack.remainingYears }} 年</template>
        </div>
        <el-progress
          :percentage="Math.round(myTrack.progress * 100)"
          :stroke-width="16"
          :text-inside="true"
          style="max-width: 560px; margin-bottom: 16px"
        >
          <span v-if="myTrack.status === 'FINISHED'">跟踪已结束</span>
          <span v-else>跟踪进度 {{ Math.round(myTrack.progress * 100) }}%</span>
        </el-progress>
        <div class="track-section__subtitle">年度就业状态（基于您的就业记录自动更新）</div>
        <el-timeline v-if="myTrack.yearRecords && myTrack.yearRecords.length" class="track-section__timeline">
          <el-timeline-item
            v-for="rec in myTrack.yearRecords"
            :key="rec.year"
            :timestamp="rec.year + ' 年'"
            placement="top"
            :type="rec.destination === '签约就业' ? 'success' : 'primary'"
          >
            <div class="track-section__line">
              <el-tag size="small" round>{{ rec.destinationName }}</el-tag>
              <span v-if="rec.companyName" class="track-section__company">{{ rec.companyName }}</span>
              <span v-if="rec.position" class="track-section__position">{{ rec.position }}</span>
            </div>
            <div v-if="rec.city || rec.salaryRange" class="track-section__sub">
              <span v-if="rec.city">城市：{{ rec.city }}</span>
              <span v-if="rec.salaryRange">薪资：{{ rec.salaryRange }}</span>
            </div>
          </el-timeline-item>
        </el-timeline>
        <el-text v-else type="info" size="small">跟踪期内暂无审核通过的就业记录。</el-text>
      </div>
    </template>

    <!-- 教师/管理员：账户信息（工号/姓名/性别/学院/专业/班级/角色） -->
    <el-descriptions v-else :column="1" border style="max-width: 560px; margin-top: 16px">
      <el-descriptions-item label="工号">
        {{ auth.userInfo?.username ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="姓名">
        {{ auth.userInfo?.realName ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="性别">
        {{ auth.userInfo?.gender ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="学院">
        {{ auth.userInfo?.deptName ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="专业">
        -
      </el-descriptions-item>
      <el-descriptions-item label="班级">
        -
      </el-descriptions-item>
      <el-descriptions-item label="角色">
        <el-tag size="small">{{ roleName }}</el-tag>
      </el-descriptions-item>
    </el-descriptions>

    <div style="margin-top: 24px; display: flex; gap: 12px; flex-wrap: wrap">
      <el-button v-if="isStudent" type="primary" @click="openProfileDialog">修改资料</el-button>
      <el-button type="warning" plain @click="openPwdDialog">修改密码</el-button>
      <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
    </div>

    <!-- 修改资料弹窗（仅学生） -->
    <el-dialog v-model="profileDialogVisible" title="修改资料" width="480px">
      <el-form :model="profileForm" label-width="80px">
        <el-form-item label="学号">
          <el-input :model-value="profile?.studentNumber ?? '-'" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input :model-value="profile?.name ?? '-'" disabled />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="profileForm.gender" placeholder="请选择性别" style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="profileForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="profileDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingProfile" @click="handleUpdateProfile">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码弹窗（所有角色） -->
    <el-dialog v-model="pwdDialogVisible" title="修改密码" width="480px">
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="90px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="6-32 位" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingPwd" @click="handleChangePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { AlarmClock } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { getMyProfile, updateMyProfile, type GraduateVO } from '@/api/graduate'
import { getMyTrack, type TrackDetailVO } from '@/api/track'
import { changePassword } from '@/api/auth'

const router = useRouter()
const auth = useAuthStore()

const profile = ref<GraduateVO | null>(null)
const isStudent = computed(() => auth.userInfo?.role === 'GRADUATE')

// 我的毕业生跟踪记录
const myTrack = ref<TrackDetailVO | null>(null)

const roleNameMap: Record<string, string> = {
  GRADUATE: '毕业生',
  TEACHER: '教师',
  COLLEGE_ADMIN: '院级管理员',
  SYSTEM_ADMIN: '系统管理员',
}
const roleName = computed(() => roleNameMap[auth.userInfo?.role ?? ''] ?? auth.userInfo?.role ?? '-')

onMounted(async () => {
  if (!isStudent.value) return
  try {
    const res = await getMyProfile()
    profile.value = res?.data ?? null
  } catch {
    profile.value = null
  }
  try {
    const res = await getMyTrack()
    myTrack.value = res?.data ?? null
  } catch {
    myTrack.value = null
  }
})

// ================= 修改资料 =================
const profileDialogVisible = ref(false)
const savingProfile = ref(false)
const profileForm = reactive({ phone: '', email: '', gender: '' })

function openProfileDialog() {
  profileForm.phone = profile.value?.phone ?? ''
  profileForm.email = profile.value?.email ?? ''
  profileForm.gender = profile.value?.gender ?? auth.userInfo?.gender ?? ''
  profileDialogVisible.value = true
}

async function handleUpdateProfile() {
  savingProfile.value = true
  try {
    await updateMyProfile({ phone: profileForm.phone, email: profileForm.email, gender: profileForm.gender })
    ElMessage.success('资料修改成功')
    profileDialogVisible.value = false
    const res = await getMyProfile()
    profile.value = res?.data ?? null
    // 同步登录态中的性别，个人中心立即显示新值
    if (profile.value?.gender) {
      auth.updateUserInfo({ gender: profile.value.gender })
    }
  } catch {
    // 拦截器已弹出错误提示
  } finally {
    savingProfile.value = false
  }
}

// ================= 修改密码 =================
const pwdDialogVisible = ref(false)
const savingPwd = ref(false)
const pwdFormRef = ref<FormInstance>()
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 32, message: '新密码长度需在 6-32 位之间', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== pwdForm.newPassword) callback(new Error('两次输入的密码不一致'))
        else callback()
      },
      trigger: 'blur',
    },
  ],
}

function openPwdDialog() {
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
  pwdForm.confirmPassword = ''
  pwdDialogVisible.value = true
}

async function handleChangePassword() {
  try {
    await pwdFormRef.value?.validate()
  } catch {
    return
  }
  savingPwd.value = true
  try {
    await changePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    pwdDialogVisible.value = false
    auth.logout()
    router.push('/login')
  } catch {
    // 拦截器已弹出错误提示
  } finally {
    savingPwd.value = false
  }
}

function handleLogout() {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
    .then(() => {
      auth.logout()
      router.push('/login')
    })
    .catch(() => {})
}
</script>

<style scoped>
.track-section {
  max-width: 620px;
  margin-top: 24px;
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fafafa;
}
.track-section__header {
  display: flex;
  align-items: center;
  font-weight: 600;
  margin-bottom: 8px;
}
.track-section__icon {
  margin-right: 6px;
  color: #409eff;
}
.track-section__desc {
  color: #606266;
  font-size: 14px;
  margin-bottom: 12px;
}
.track-section__subtitle {
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
}
.track-section__timeline {
  padding-left: 4px;
}
.track-section__line {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.track-section__company {
  font-weight: 600;
  color: #303133;
}
.track-section__position {
  color: #606266;
}
.track-section__sub {
  display: flex;
  gap: 16px;
  margin-top: 4px;
  color: #909399;
  font-size: 13px;
}
</style>
