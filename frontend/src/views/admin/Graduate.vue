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
    <!-- 页面标题 -->
    <div class="page-title">
      <div class="page-title__main">
        <el-icon class="page-title__icon"><UserFilled /></el-icon>
        <div>
          <h2 class="page-title__text">毕业生管理<span v-if="isTeacher" class="dept-scope">（{{ teacherDeptName || '本院系' }}）</span></h2>
        </div>
      </div>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryForm" size="default">
        <el-form-item v-if="isTeacher" label="所属院系">
          <el-input :model-value="teacherDeptName || '本院系'" disabled style="width: 160px" />
        </el-form-item>
        <el-form-item v-else label="所属院系">
          <el-select
            v-model="queryForm.deptId"
            placeholder="请选择院系"
            clearable
            style="width: 160px"
            @change="onDeptChange"
          >
            <el-option
              v-for="d in deptList"
              :key="d.id"
              :label="d.name"
              :value="d.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属专业">
          <el-select
            v-model="queryForm.majorId"
            placeholder="请选择专业"
            clearable
            style="width: 160px"
          >
            <el-option
              v-for="m in majorList"
              :key="m.id"
              :label="m.name"
              :value="m.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="就业状态">
          <el-select
            v-model="queryForm.status"
            placeholder="请选择"
            clearable
            style="width: 140px"
          >
            <el-option label="待就业" value="待就业" />
            <el-option label="已签约" value="已签约" />
            <el-option label="升学" value="升学" />
            <el-option label="灵活就业" value="灵活就业" />
            <el-option label="创业" value="创业" />
            <el-option label="出国" value="出国" />
            <el-option label="暂不就业" value="暂不就业" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input
            v-model="queryForm.keyword"
            placeholder="学号/姓名"
            clearable
            style="width: 160px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span><el-icon style="vertical-align: -2px; margin-right: 6px"><List /></el-icon>毕业生列表</span>
          <div class="header-actions" v-if="!isTeacher">
            <el-button type="primary" @click="handleAdd">
              <el-icon style="margin-right: 4px"><Plus /></el-icon>新增毕业生
            </el-button>
            <el-button type="primary" plain @click="importVisible = true">
              <el-icon style="margin-right: 4px"><Upload /></el-icon>批量导入
            </el-button>
            <el-button type="success" @click="handleExport">
              <el-icon style="margin-right: 4px"><Download /></el-icon>导出
            </el-button>
            <el-button type="warning" plain @click="openTrackSettings">
              <el-icon style="margin-right: 4px"><AlarmClock /></el-icon>跟踪设置
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="studentNumber" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="70" align="center" />
        <el-table-column prop="deptName" label="院系" width="130" />
        <el-table-column prop="majorName" label="专业" width="130" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="就业状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" effect="light" size="small" round>
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="账号状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.graduateStatus === 1 ? 'success' : 'danger'" size="small">
              {{ row.graduateStatus === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column :label="isTeacher ? '操作' : '操作'" :width="isTeacher ? 260 : 400" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="info" link size="small" @click="handleDetail(row)">
              <el-icon style="vertical-align: -2px"><Document /></el-icon>详情
            </el-button>
            <el-button type="warning" link size="small" @click="handleEmploymentHistory(row)">
              <el-icon style="vertical-align: -2px"><Tickets /></el-icon>就业历史
            </el-button>
            <el-button type="success" link size="small" @click="openTrackDetail(row)">
              <el-icon style="vertical-align: -2px"><AlarmClock /></el-icon>跟踪
            </el-button>
            <template v-if="!isTeacher">
              <el-button type="primary" link size="small" @click="handleEdit(row)">
                <el-icon style="vertical-align: -2px"><Edit /></el-icon>编辑
              </el-button>
              <el-popconfirm
                :title="row.graduateStatus === 1 ? '确定要禁用该毕业生账号吗？' : '确定要启用该毕业生账号吗？'"
                confirm-button-text="确定"
                cancel-button-text="取消"
                @confirm="handleToggleStatus(row)"
              >
                <template #reference>
                  <el-button
                    :type="row.graduateStatus === 1 ? 'warning' : 'success'"
                    link
                    size="small"
                  >
                    <el-icon style="vertical-align: -2px"><Switch /></el-icon>{{ row.graduateStatus === 1 ? '禁用' : '启用' }}
                  </el-button>
                </template>
              </el-popconfirm>
            </template>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="暂无数据" />
        </template>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑毕业生' : '新增毕业生'"
      width="550px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="90px"
      >
        <el-form-item label="学号" prop="studentNumber">
          <el-input
            v-model="formData.studentNumber"
            placeholder="请输入学号"
            maxlength="20"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" maxlength="20" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="formData.gender" placeholder="请选择" style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属院系" prop="deptId">
          <el-select
            v-model="formData.deptId"
            placeholder="请选择院系"
            style="width: 100%"
            @change="onFormDeptChange"
          >
            <el-option
              v-for="d in deptList"
              :key="d.id"
              :label="d.name"
              :value="d.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属专业" prop="majorId">
          <el-select
            v-model="formData.majorId"
            placeholder="请先选择院系"
            style="width: 100%"
            @change="onFormMajorChange"
          >
            <el-option
              v-for="m in formMajorList"
              :key="m.id"
              :label="m.name"
              :value="m.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属班级" prop="classId">
          <el-select
            v-model="formData.classId"
            placeholder="请先选择专业"
            style="width: 100%"
          >
            <el-option
              v-for="c in formClassList"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="formData.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="formData.email" placeholder="请输入邮箱" maxlength="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框：基本信息 + 就业记录 + 审核进度 -->
    <el-dialog
      v-model="detailVisible"
      title="毕业生详情"
      width="880px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div v-loading="detailLoading">
        <!-- 1. 毕业生基本信息 -->
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="学号">{{ detailData?.basicInfo?.studentNumber }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ detailData?.basicInfo?.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ detailData?.basicInfo?.gender }}</el-descriptions-item>
          <el-descriptions-item label="院系">{{ detailData?.basicInfo?.deptName }}</el-descriptions-item>
          <el-descriptions-item label="专业">{{ detailData?.basicInfo?.majorName }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ detailData?.basicInfo?.className }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detailData?.basicInfo?.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="账号状态">
            <el-tag
              :type="detailData?.basicInfo?.graduateStatus === 1 ? 'success' : 'danger'"
              size="small"
            >
              {{ detailData?.basicInfo?.graduateStatus === 1 ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ detailData?.basicInfo?.createTime }}</el-descriptions-item>
        </el-descriptions>

        <!-- 2. 就业申请记录列表 -->
        <div class="detail-section-title">
          <el-icon><List /></el-icon> 就业申请记录（{{ detailData?.employmentRecords?.length || 0 }}）
        </div>
        <el-table
          :data="detailData?.employmentRecords || []"
          border
          size="small"
          max-height="260"
          style="width: 100%"
        >
          <el-table-column prop="id" label="申请ID" width="80" align="center" />
          <el-table-column prop="destinationName" label="就业类型" width="100" align="center">
            <template #default="{ row }">
              <el-tag size="small" round :type="destTagType(row.destination)">
                {{ row.destinationName }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="companyName" label="单位名称" min-width="160" show-overflow-tooltip>
            <template #default="{ row }">{{ row.companyName || '-' }}</template>
          </el-table-column>
          <el-table-column prop="position" label="岗位名称" min-width="120" show-overflow-tooltip>
            <template #default="{ row }">{{ row.position || '-' }}</template>
          </el-table-column>
          <el-table-column prop="salaryRange" label="薪资" width="100" align="center">
            <template #default="{ row }">{{ row.salaryRange || '-' }}</template>
          </el-table-column>
          <el-table-column prop="evidenceUrl" label="佐证材料" width="110" align="center">
            <template #default="{ row }">
              <el-link
                v-if="row.evidenceUrl"
                type="primary"
                :href="row.evidenceUrl"
                target="_blank"
                :underline="false"
              >
                查看材料
              </el-link>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="提交时间" width="160" />
        </el-table>

        <!-- 3. 最新申请审核进度 -->
        <div class="detail-section-title">
          <el-icon><Finished /></el-icon> 最新申请审核进度
        </div>
        <template v-if="detailData?.reviewProgress">
          <el-descriptions :column="2" border size="small" class="progress-desc">
            <el-descriptions-item label="审核状态">
              <el-tag :type="reviewTagType(detailData.reviewProgress.reviewStatus)" size="small">
                {{ detailData.reviewProgress.reviewStatusName }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="当前环节">{{ detailData.reviewProgress.stageName }}</el-descriptions-item>
            <el-descriptions-item label="教师意见">
              {{ detailData.reviewProgress.teacherComment || '暂未审核' }}
            </el-descriptions-item>
            <el-descriptions-item label="管理员意见">
              {{ detailData.reviewProgress.adminComment || '暂未审核' }}
            </el-descriptions-item>
            <el-descriptions-item label="教师审核时间">
              {{ detailData.reviewProgress.teacherReviewTime || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="管理员审核时间">
              {{ detailData.reviewProgress.adminReviewTime || '-' }}
            </el-descriptions-item>
          </el-descriptions>

          <el-steps :active="progressStep(detailData.reviewProgress)" align-center class="progress-steps">
            <el-step title="提交申请" :description="fmtTime(detailData.reviewProgress.createTime)" />
            <el-step
              title="教师审核"
              :description="detailData.reviewProgress.teacherReviewTime || '等待中'"
            />
            <el-step
              title="管理员审核"
              :description="detailData.reviewProgress.adminReviewTime || '等待中'"
            />
          </el-steps>
        </template>
        <el-empty v-else description="暂无就业申请记录" :image-size="80" />
      </div>
      <template #footer>
        <el-button type="primary" @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 就业历史对话框：分页展示就业申请记录 -->
    <el-dialog
      v-model="historyVisible"
      title="就业历史记录"
      width="880px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div v-loading="historyLoading">
        <el-descriptions :column="4" border size="small" class="history-desc">
          <el-descriptions-item label="学号">{{ historyStudent.studentNumber }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ historyStudent.name }}</el-descriptions-item>
          <el-descriptions-item label="院系">{{ historyStudent.deptName }}</el-descriptions-item>
          <el-descriptions-item label="专业">{{ historyStudent.majorName }}</el-descriptions-item>
        </el-descriptions>

        <el-table
          :data="historyData"
          border
          size="small"
          max-height="420"
          style="width: 100%"
        >
          <el-table-column prop="id" label="申请ID" width="80" align="center" />
          <el-table-column prop="destinationName" label="就业类型" width="110" align="center">
            <template #default="{ row }">
              <el-tag size="small" round :type="destTagType(row.destination)">
                {{ row.destinationName }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="companyName" label="单位名称" min-width="150" show-overflow-tooltip>
            <template #default="{ row }">{{ row.companyName || '-' }}</template>
          </el-table-column>
          <el-table-column prop="position" label="岗位名称" min-width="110" show-overflow-tooltip>
            <template #default="{ row }">{{ row.position || '-' }}</template>
          </el-table-column>
          <el-table-column prop="salaryRange" label="薪资" width="100" align="center">
            <template #default="{ row }">{{ row.salaryRange || '-' }}</template>
          </el-table-column>
          <el-table-column prop="reviewStatusName" label="审核状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag size="small" :type="reviewTagType(row.reviewStatus)">
                {{ row.reviewStatusName || '-' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="evidenceUrl" label="佐证材料" width="110" align="center">
            <template #default="{ row }">
              <el-link
                v-if="row.evidenceUrl"
                type="primary"
                :href="row.evidenceUrl"
                target="_blank"
                :underline="false"
              >
                查看材料
              </el-link>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="提交时间" width="160" />
        </el-table>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="historyQuery.page"
            v-model:page-size="historyQuery.size"
            :total="historyTotal"
            :page-sizes="[5, 10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="fetchHistory"
            @current-change="fetchHistory"
          />
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="historyVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 跟踪设置对话框：按毕业年份(届)设置跟踪年限 -->
    <el-dialog
      v-model="trackSettingsVisible"
      title="毕业生跟踪设置（按毕业年份）"
      width="680px"
      :close-on-click-modal="false"
    >
      <div v-loading="trackSettingsLoading">
        <el-alert
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 14px"
          title="按毕业年份设置跟踪年限（3年/5年）。系统按毕业年份自动计算每个毕业生的跟踪进度，并基于审核通过的就业记录按年度自动聚合就业状态变化。"
        />
        <el-table :data="trackSettings" border size="small" style="width: 100%">
          <el-table-column prop="graduateYear" label="毕业年份" width="140" align="center" />
          <el-table-column prop="studentCount" label="学生人数" width="120" align="center" />
          <el-table-column label="跟踪年限" width="180" align="center">
            <template #default="{ row }">
              <el-select v-model="row.trackYears" style="width: 110px">
                <el-option label="3 年" :value="3" />
                <el-option label="5 年" :value="5" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="跟踪区间" min-width="150">
            <template #default="{ row }">
              {{ row.graduateYear }} ~ {{ Number(row.graduateYear) + row.trackYears - 1 }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center">
            <template #default="{ row }">
              <el-button type="primary" link size="small" :loading="row.saving" @click="saveTrackSetting(row)">
                保存
              </el-button>
              <el-popconfirm
                title="确定恢复该届为默认 3 年？"
                confirm-button-text="确定"
                cancel-button-text="取消"
                @confirm="removeTrackSetting(row)"
              >
                <template #reference>
                  <el-button type="danger" link size="small">重置</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="暂无跟踪设置，可先新增一届" :image-size="60" />
          </template>
        </el-table>

        <!-- 新增一届跟踪设置 -->
        <div class="track-add-row">
          <el-input-number v-model="trackNewYear" :min="2000" :max="2100" controls-position="right" style="width: 160px" />
          <el-select v-model="trackNewYears" style="width: 110px">
            <el-option label="3 年" :value="3" />
            <el-option label="5 年" :value="5" />
          </el-select>
          <el-button type="primary" plain @click="addTrackSetting">添加该届</el-button>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="trackSettingsVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 跟踪详情对话框：跟踪进度 + 年度就业状态时间线 -->
    <el-dialog
      v-model="trackVisible"
      :title="'跟踪详情 - ' + (trackData?.name || '')"
      width="760px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div v-loading="trackLoading">
        <template v-if="trackData">
          <el-descriptions :column="3" border size="small" class="track-desc">
            <el-descriptions-item label="学号">{{ trackData.studentNumber }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ trackData.name }}</el-descriptions-item>
            <el-descriptions-item label="毕业年份">{{ trackData.graduateYear }}</el-descriptions-item>
            <el-descriptions-item label="跟踪年限">{{ trackData.trackYears }} 年</el-descriptions-item>
            <el-descriptions-item label="跟踪区间">
              {{ trackData.startYear }} ~ {{ trackData.endYear }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="trackData.status === 'FINISHED' ? 'info' : 'success'" size="small">
                {{ trackData.statusName }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>

          <!-- 跟踪进度 -->
          <div class="track-progress-block">
            <div class="detail-section-title">
              <el-icon><AlarmClock /></el-icon> 跟踪进度（已跟踪 {{ trackData.trackedYears }} 年 / 共 {{ trackData.trackYears }} 年）
            </div>
            <el-progress
              :percentage="Math.round(trackData.progress * 100)"
              :status="trackData.status === 'FINISHED' ? 'success' : ''"
              :stroke-width="18"
              :text-inside="true"
            >
              <span v-if="trackData.status === 'FINISHED'">跟踪已结束</span>
              <span v-else>剩余 {{ trackData.remainingYears }} 年（截至 {{ trackData.endYear }}）</span>
            </el-progress>
          </div>

          <!-- 年度就业状态时间线（就业记录自动跟踪） -->
          <div class="detail-section-title">
            <el-icon><Tickets /></el-icon> 年度就业状态（按审核通过的就业记录自动跟踪）
          </div>
          <el-timeline v-if="trackData.yearRecords && trackData.yearRecords.length" class="track-timeline">
            <el-timeline-item
              v-for="rec in trackData.yearRecords"
              :key="rec.year"
              :timestamp="rec.year + ' 年 · ' + (rec.recordTime ? fmtTime(rec.recordTime) : '')"
              placement="top"
              :type="rec.destination === '签约就业' ? 'success' : 'primary'"
            >
              <el-card shadow="never" class="track-year-card">
                <div class="track-year-line">
                  <el-tag size="small" round :type="destTagType(rec.destination)">{{ rec.destinationName }}</el-tag>
                  <span v-if="rec.companyName" class="track-company">{{ rec.companyName }}</span>
                  <span v-if="rec.position" class="track-position">{{ rec.position }}</span>
                </div>
                <div v-if="rec.city || rec.salaryRange" class="track-year-sub">
                  <span v-if="rec.city">城市：{{ rec.city }}</span>
                  <span v-if="rec.salaryRange">薪资：{{ rec.salaryRange }}</span>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="跟踪期内暂无审核通过的就业记录" :image-size="80" />
        </template>
      </div>
      <template #footer>
        <el-button type="primary" @click="trackVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <ImportDialog
      v-model="importVisible"
      title="毕业生批量导入"
      :columns="importColumns"
      :template-rows="templateRows"
      :build-payload="buildImportPayload"
      :submit-one="submitImport"
      @success="fetchData"
    />
  </div>
</template>

<script setup lang="ts">
/**
 * 毕业生管理页面（管理员 / 教师）
 * - 按院系/专业/班级/学号/姓名筛选毕业生，分页展示
 * - 支持新增、编辑、启用/禁用毕业生账号、CSV 批量导入与导出
 * - 详情弹窗集成：基本信息 + 就业历史 + 审核进度 + 跟踪设置/跟踪记录
 * - 教师角色只读查看本院系毕业生（数据隔离由后端强制）
 */
import { ref, reactive, watch, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus, Edit, Switch, Download, Document, Finished, Tickets, AlarmClock, Upload } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { getTeacherDashboardStat } from '@/api/dashboard'
import { getDepartmentList, type DepartmentSimpleVO } from '@/api/department'
import { getMajorList, type MajorSimpleVO } from '@/api/major'
import { getClassList, type ClassSimpleVO } from '@/api/class'
import { downloadCsv } from '@/api/request'
import {
  getGraduatePage,
  getGraduateDetail,
  getGraduateEmploymentHistory,
  createGraduate,
  updateGraduate,
  toggleGraduateStatus,
  type GraduateVO,
  type GraduateDetailVO,
  type EmploymentRecordVO,
  type ReviewProgressVO,
  type GraduateCreateDTO,
  type GraduateQueryDTO
} from '@/api/graduate'
import {
  getTrackSettings,
  updateTrackSetting,
  deleteTrackSetting,
  getGraduateTrack,
  type TrackSettingVO,
  type TrackDetailVO
} from '@/api/track'
import ImportDialog, { type ImportColumn } from '@/components/ImportDialog.vue'

// 表格与加载状态
const loading = ref(false)
const submitLoading = ref(false)
const total = ref(0)
const tableData = ref<GraduateVO[]>([])

// 角色权限：教师仅可只读查看本院系毕业生（数据隔离由后端强制）
const authStore = useAuthStore()
const isTeacher = computed(() => authStore.userInfo?.role === 'TEACHER')
const teacherDeptName = ref('')
const teacherDeptId = ref<number | undefined>(undefined)

const deptList = ref<DepartmentSimpleVO[]>([])
const majorList = ref<MajorSimpleVO[]>([])

const formMajorList = ref<MajorSimpleVO[]>([])
const formClassList = ref<ClassSimpleVO[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()

// 详情弹窗状态
const detailVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<GraduateDetailVO | null>(null)

// 就业历史弹窗状态
const historyVisible = ref(false)
const historyLoading = ref(false)
const historyId = ref<number | null>(null)
const historyData = ref<EmploymentRecordVO[]>([])
const historyTotal = ref(0)
const historyQuery = reactive({ page: 1, size: 10 })
const historyStudent = reactive({
  studentNumber: '',
  name: '',
  deptName: '',
  majorName: ''
})

// 跟踪设置弹窗状态
const trackSettingsVisible = ref(false)
const trackSettingsLoading = ref(false)
const trackSettings = ref<TrackSettingVO[]>([])
const trackNewYear = ref(new Date().getFullYear())
const trackNewYears = ref(3)

// 跟踪详情弹窗状态
const trackVisible = ref(false)
const trackLoading = ref(false)
const trackData = ref<TrackDetailVO | null>(null)

const queryForm = reactive<GraduateQueryDTO>({
  deptId: undefined,
  majorId: undefined,
  keyword: '',
  status: '',
  page: 1,
  size: 10
})

const formData = reactive<any>({
  studentNumber: '',
  name: '',
  gender: '男',
  deptId: undefined,
  majorId: undefined,
  classId: undefined,
  phone: '',
  email: ''
})

const formRules: FormRules = {
  studentNumber: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择所属院系', trigger: 'change' }],
  majorId: [{ required: true, message: '请选择所属专业', trigger: 'change' }],
  classId: [{ required: true, message: '请选择所属班级', trigger: 'change' }]
}

const EMPLOYMENT_STATUS_OPTIONS = [
  { value: '待就业', label: '待就业', type: 'info' },
  { value: '已签约', label: '已签约', type: 'primary' },
  { value: '升学', label: '升学', type: 'warning' },
  { value: '灵活就业', label: '灵活就业', type: 'success' },
  { value: '创业', label: '创业', type: 'warning' },
  { value: '出国', label: '出国', type: 'warning' },
  { value: '暂不就业', label: '暂不就业', type: 'info' }
]

function statusLabel(status: string): string {
  return EMPLOYMENT_STATUS_OPTIONS.find((o) => o.value === status)?.label || '待就业'
}

function statusType(status: string): any {
  return EMPLOYMENT_STATUS_OPTIONS.find((o) => o.value === status)?.type || 'info'
}

// 批量导入：按名称匹配 院系/专业/班级 ID
const importVisible = ref(false)
const allMajorList = ref<MajorSimpleVO[]>([])
const allClassList = ref<ClassSimpleVO[]>([])
const importColumns: ImportColumn[] = [
  { prop: 'studentNumber', label: '学号', required: true, width: 14, text: true },
  { prop: 'name', label: '姓名', required: true, width: 12 },
  { prop: 'gender', label: '性别', width: 8 },
  { prop: 'deptName', label: '所属院系', required: true, width: 18, aliases: ['院系'] },
  { prop: 'majorName', label: '所属专业', required: true, width: 22, aliases: ['专业'] },
  { prop: 'className', label: '所属班级', required: true, width: 18, aliases: ['班级'] },
  { prop: 'phone', label: '手机号', width: 15, text: true },
  { prop: 'email', label: '邮箱', width: 24 }
]
const templateRows = [
  {
    studentNumber: '20260001',
    name: '张三',
    gender: '男',
    deptName: '计算机学院',
    majorName: '计算机科学与技术',
    className: '计算机2301班',
    phone: '13800138000',
    email: 'zhangsan@example.com'
  }
]

watch(importVisible, async (v) => {
  if (v && !allMajorList.value.length) {
    allMajorList.value = await loadMajorList()
    try {
      const res = await getClassList()
      allClassList.value = res.data
    } catch {
      allClassList.value = []
    }
  }
})

async function buildImportPayload(data: Record<string, string>) {
  const deptName = data.deptName?.trim()
  const dept = deptList.value.find((d) => d.name === deptName)
  if (!dept) return { error: `院系「${deptName}」不存在，请先确认院系名称` }
  const majorName = data.majorName?.trim()
  const major = allMajorList.value.find((m) => m.name === majorName)
  if (!major) return { error: `专业「${majorName}」不存在，请先确认专业名称` }
  const className = data.className?.trim()
  const cls = allClassList.value.find((c) => c.name === className && c.majorId === major.id)
  if (!cls) return { error: `班级「${className}」在专业「${majorName}」下不存在` }
  const payload: any = {
    ...data,
    deptId: dept.id,
    majorId: major.id,
    classId: cls.id
  }
  delete payload.deptName
  delete payload.majorName
  delete payload.className
  if (!payload.gender) payload.gender = '男'
  else if (payload.gender !== '男' && payload.gender !== '女') {
    return { error: `性别「${payload.gender}」不合法，请填写 男 或 女` }
  }
  if (!payload.phone) delete payload.phone
  if (!payload.email) delete payload.email
  return { payload }
}

async function submitImport(payload: any) {
  await createGraduate(payload)
}

async function loadDeptList() {
  try {
    const res = await getDepartmentList()
    deptList.value = res.data
  } catch { /* ignore */ }
}

async function loadMajorList(deptId?: number) {
  try {
    const res = await getMajorList(deptId)
    return res.data
  } catch { return [] }
}

async function onDeptChange(deptId?: number) {
  queryForm.majorId = undefined
  majorList.value = await loadMajorList(deptId)
  fetchData()
}

async function onFormDeptChange(deptId?: number) {
  formData.majorId = undefined
  formData.classId = undefined
  formMajorList.value = await loadMajorList(deptId)
  formClassList.value = []
}

async function onFormMajorChange(majorId?: number) {
  formData.classId = undefined
  if (majorId) {
    try {
      const res = await getClassList(majorId)
      formClassList.value = res.data
    } catch {
      formClassList.value = []
    }
  } else {
    formClassList.value = []
  }
}

async function fetchData() {
  loading.value = true
  try {
    // 初始化major下拉
    if (queryForm.deptId && majorList.value.length === 0) {
      majorList.value = await loadMajorList(queryForm.deptId)
    }
    const res = await getGraduatePage(queryForm)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.page = 1
  fetchData()
}

function handleReset() {
  queryForm.deptId = undefined
  queryForm.majorId = undefined
  queryForm.status = ''
  queryForm.keyword = ''
  queryForm.page = 1
  // 教师锁定本院系：重置后专业下拉仍为本院系专业
  if (!isTeacher.value) majorList.value = []
  fetchData()
}

// 导出毕业生信息（文档2.3导出按钮）
async function handleExport() {
  try {
    await downloadCsv('/export/graduates', {
      keyword: queryForm.keyword,
      status: queryForm.status,
      majorId: queryForm.majorId,
      deptId: queryForm.deptId,
    })
    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

async function handleAdd() {
  isEdit.value = false
  editId.value = null
  formData.studentNumber = ''
  formData.name = ''
  formData.gender = '男'
  formData.deptId = undefined
  formData.majorId = undefined
  formData.classId = undefined
  formData.phone = ''
  formData.email = ''
  formMajorList.value = []
  formClassList.value = []
  dialogVisible.value = true
  formRef.value?.resetFields()
}

// 就业类型标签样式（后端 destination 存中文：签约就业/升学/出国/创业/灵活就业/待就业）
function destTagType(destination: string): any {
  switch (destination) {
    case '签约就业':
      return 'success'
    case '升学':
    case '出国':
      return 'warning'
    case '灵活就业':
    case '创业':
      return 'primary'
    default:
      return 'info'
  }
}

// 审核状态标签样式（后端 reviewStatus：PENDING/FIRST_PASSED/FIRST_REJECTED/APPROVED/FINAL_REJECTED）
function reviewTagType(reviewStatus: string): any {
  switch (reviewStatus) {
    case 'APPROVED':
      return 'success'
    case 'FIRST_REJECTED':
    case 'FINAL_REJECTED':
      return 'danger'
    case 'FIRST_PASSED':
      return 'primary'
    default:
      return 'warning'
  }
}

// 审核进度步数：提交(0) -> 教师审核(1) -> 管理员审核(2) -> 完成(3)
function progressStep(progress: ReviewProgressVO): number {
  if (!progress) return 0
  switch (progress.stage) {
    case 'TEACHER_REVIEW':
      return 1
    case 'ADMIN_REVIEW':
      return 2
    case 'COMPLETED':
      return 3
    default:
      return 0
  }
}

function fmtTime(t?: string): string {
  return t ? String(t).replace('T', ' ').slice(0, 16) : '等待中'
}

// 打开毕业生详情
async function handleDetail(row: GraduateVO) {
  detailVisible.value = true
  detailLoading.value = true
  detailData.value = null
  try {
    const res = await getGraduateDetail(row.id)
    detailData.value = res.data
  } catch (e: any) {
    ElMessage.error(e?.message || '获取详情失败')
    detailVisible.value = false
  } finally {
    detailLoading.value = false
  }
}

// 分页获取就业申请历史记录
async function fetchHistory() {
  if (!historyId.value) return
  historyLoading.value = true
  try {
    const res = await getGraduateEmploymentHistory(historyId.value, {
      page: historyQuery.page,
      size: historyQuery.size
    })
    historyData.value = res.data.records
    historyTotal.value = res.data.total
  } catch (e: any) {
    ElMessage.error(e?.message || '获取就业历史失败')
  } finally {
    historyLoading.value = false
  }
}

// 打开就业历史弹窗
async function handleEmploymentHistory(row: GraduateVO) {
  historyId.value = row.id
  historyStudent.studentNumber = row.studentNumber
  historyStudent.name = row.name
  historyStudent.deptName = row.deptName
  historyStudent.majorName = row.majorName
  historyQuery.page = 1
  historyVisible.value = true
  fetchHistory()
}

// ---- 跟踪设置 ----

async function openTrackSettings() {
  trackSettingsVisible.value = true
  fetchTrackSettings()
}

async function fetchTrackSettings() {
  trackSettingsLoading.value = true
  try {
    const res = await getTrackSettings()
    trackSettings.value = res.data.map((s) => ({ ...s, saving: false }))
  } catch (e: any) {
    ElMessage.error(e?.message || '获取跟踪设置失败')
  } finally {
    trackSettingsLoading.value = false
  }
}

async function saveTrackSetting(row: TrackSettingVO & { saving?: boolean }) {
  row.saving = true
  try {
    await updateTrackSetting({ graduateYear: row.graduateYear, trackYears: row.trackYears })
    ElMessage.success(row.graduateYear + ' 届跟踪年限已保存为 ' + row.trackYears + ' 年')
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    row.saving = false
  }
}

async function removeTrackSetting(row: TrackSettingVO) {
  try {
    await deleteTrackSetting(row.graduateYear)
    ElMessage.success(row.graduateYear + ' 届已恢复默认 3 年')
    fetchTrackSettings()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}

async function addTrackSetting() {
  const year = String(trackNewYear.value)
  if (trackSettings.value.some((s) => s.graduateYear === year)) {
    ElMessage.warning(year + ' 届已存在，请直接修改后保存')
    return
  }
  try {
    await updateTrackSetting({ graduateYear: year, trackYears: trackNewYears.value })
    ElMessage.success(year + ' 届已添加（' + trackNewYears.value + ' 年跟踪）')
    fetchTrackSettings()
  } catch (e: any) {
    ElMessage.error(e?.message || '添加失败')
  }
}

// ---- 跟踪详情 ----

async function openTrackDetail(row: GraduateVO) {
  trackVisible.value = true
  trackLoading.value = true
  trackData.value = null
  try {
    const res = await getGraduateTrack(row.id)
    trackData.value = res.data
  } catch (e: any) {
    ElMessage.error(e?.message || '获取跟踪详情失败')
    trackVisible.value = false
  } finally {
    trackLoading.value = false
  }
}

async function handleEdit(row: GraduateVO) {
  isEdit.value = true
  editId.value = row.id
  formData.studentNumber = row.studentNumber
  formData.name = row.name
  formData.gender = row.gender
  formData.deptId = row.deptId
  formMajorList.value = await loadMajorList(row.deptId)
  formData.majorId = row.majorId
  formClassList.value = (await getClassList(row.majorId)).data
  formData.classId = row.classId
  formData.phone = row.phone
  formData.email = row.email
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const payload: GraduateCreateDTO = {
      studentNumber: formData.studentNumber,
      name: formData.name,
      gender: formData.gender,
      deptId: formData.deptId,
      majorId: formData.majorId,
      classId: formData.classId,
      phone: formData.phone,
      email: formData.email
    }
    if (isEdit.value && editId.value) {
      await updateGraduate(editId.value, { ...payload, id: editId.value })
      ElMessage.success('更新成功')
    } else {
      await createGraduate(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

async function handleToggleStatus(row: GraduateVO) {
  try {
    await toggleGraduateStatus(row.id)
    ElMessage.success(row.graduateStatus === 1 ? '已禁用' : '已启用')
    fetchData()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  }
}

onMounted(async () => {
  await loadDeptList()
  if (isTeacher.value) {
    // 教师：锁定本院系，专业下拉仅加载本院系专业
    try {
      const stat = await getTeacherDashboardStat()
      teacherDeptName.value = stat.data.deptName
      teacherDeptId.value = stat.data.deptId
      majorList.value = await loadMajorList(stat.data.deptId)
    } catch { /* ignore */ }
  }
  fetchData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 16px;
}
.table-card {
  margin-bottom: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
.detail-section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  margin: 16px 0 10px;
}
.history-desc {
  margin-bottom: 16px;
}
.progress-desc {
  margin-bottom: 16px;
}
.progress-steps {
  margin-top: 8px;
}
.dept-scope {
  font-size: 14px;
  font-weight: normal;
  color: #909399;
  margin-left: 8px;
}
.track-add-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 16px;
}
.track-desc {
  margin-bottom: 12px;
}
.track-progress-block {
  margin-bottom: 12px;
}
.track-timeline {
  padding-left: 4px;
}
.track-year-card {
  margin-bottom: 4px;
}
.track-year-line {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.track-company {
  font-weight: 600;
  color: #303133;
}
.track-position {
  color: #606266;
}
.track-year-sub {
  display: flex;
  gap: 16px;
  margin-top: 6px;
  color: #909399;
  font-size: 13px;
}
</style>
