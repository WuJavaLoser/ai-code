<template>
  <div class="user-manage-page">
    <component :is="contextHolder" />

    <div class="page-content">
      <a-card title="用户列表" :bordered="false">
        <template #extra>
          <a-button type="primary" @click="handleAddUser">
            <PlusOutlined />
            添加用户
          </a-button>
        </template>

        <!-- 搜索栏 -->
        <div class="search-section">
          <a-row :gutter="16">
            <a-col :span="6">
              <a-input
                v-model:value="searchParams.userName"
                placeholder="请输入用户名称"
                allow-clear
                @pressEnter="handleSearch"
              >
                <template #prefix>
                  <UserOutlined />
                </template>
              </a-input>
            </a-col>
            <a-col :span="6">
              <a-input
                v-model:value="searchParams.userAccount"
                placeholder="请输入用户账号"
                allow-clear
                @pressEnter="handleSearch"
              >
                <template #prefix>
                  <IdcardOutlined />
                </template>
              </a-input>
            </a-col>
            <a-col :span="6">
              <a-select
                v-model:value="searchParams.userRole"
                placeholder="请选择用户角色"
                allow-clear
                style="width: 100%"
              >
                <a-select-option value="admin">管理员</a-select-option>
                <a-select-option value="user">普通用户</a-select-option>
              </a-select>
            </a-col>
            <a-col :span="6">
              <a-space>
                <a-button type="primary" @click="handleSearch" :loading="loading">
                  <SearchOutlined />
                  搜索
                </a-button>
                <a-button @click="handleReset">
                  <ReloadOutlined />
                  重置
                </a-button>
              </a-space>
            </a-col>
          </a-row>
        </div>

        <!-- 用户表格 -->
        <a-table
          :columns="columns"
          :data-source="userList"
          :loading="loading"
          :pagination="pagination"
          :scroll="{ x: 800 }"
          row-key="id"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'userAvatar'">
              <a-avatar :src="getUserAvatar(record)" :size="40">
                {{ record.userName?.charAt(0) || 'U' }}
              </a-avatar>
            </template>
            <template v-else-if="column.key === 'userRole'">
              <a-tag :color="getRoleColor(record.userRole)">
                {{ getRoleText(record.userRole) }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'createTime'">
              {{ formatDate(record.createTime) }}
            </template>
            <template v-else-if="column.key === 'action'">
              <a-space size="small">
                <a-button type="link" @click="handleViewDetail(record)" class="action-btn">
                  <EyeOutlined />
                  详情
                </a-button>
                <a-button type="link" @click="handleEdit(record)" class="action-btn">
                  <EditOutlined />
                  编辑
                </a-button>
                <a-popconfirm
                  title="确定要删除这个用户吗？"
                  ok-text="确定"
                  cancel-text="取消"
                  @confirm="handleDelete(record)"
                >
                  <a-button type="link" danger class="action-btn">
                    <DeleteOutlined />
                    删除
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </div>

    <!-- 添加/编辑用户弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalType === 'add' ? '添加用户' : '编辑用户'"
      width="800px"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
      :confirm-loading="modalLoading"
    >
      <a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="用户名称" name="userName">
              <a-input v-model:value="formData.userName" placeholder="请输入用户名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="用户账号" name="userAccount">
              <a-input
                v-model:value="formData.userAccount"
                placeholder="请输入用户账号"
                :disabled="modalType === 'edit'"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="用户角色" name="userRole">
              <a-select v-model:value="formData.userRole" placeholder="请选择用户角色">
                <a-select-option value="admin">管理员</a-select-option>
                <a-select-option value="user">普通用户</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="用户头像" name="userAvatar">
              <a-input v-model:value="formData.userAvatar" placeholder="请输入头像URL" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="用户简介" name="userProfile">
          <a-textarea v-model:value="formData.userProfile" placeholder="请输入用户简介" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 用户详情弹窗 -->
    <a-modal v-model:open="detailModalVisible" title="用户详情" width="700px" :footer="null">
      <a-descriptions :column="2" bordered v-if="currentUser">
        <a-descriptions-item label="用户ID">{{ currentUser.id }}</a-descriptions-item>
        <a-descriptions-item label="用户名称">{{ currentUser.userName }}</a-descriptions-item>
        <a-descriptions-item label="用户账号">{{ currentUser.userAccount }}</a-descriptions-item>
        <a-descriptions-item label="用户角色">
          <a-tag :color="getRoleColor(currentUser.userRole || '')">
            {{ getRoleText(currentUser.userRole || '') }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="用户头像" :span="2">
          <a-avatar :src="getUserAvatar(currentUser)" :size="60">
            {{ currentUser.userName?.charAt(0) || 'U' }}
          </a-avatar>
        </a-descriptions-item>
        <a-descriptions-item label="用户简介" :span="2">{{
          currentUser.userProfile || '暂无简介'
        }}</a-descriptions-item>
        <a-descriptions-item label="创建时间" :span="2">{{
          formatDate(currentUser.createTime)
        }}</a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  UserOutlined,
  IdcardOutlined,
  EyeOutlined,
  EditOutlined,
  DeleteOutlined,
} from '@ant-design/icons-vue'
import {
  listUserVoByPage,
  addUser,
  updateUser,
  deleteUser,
  getUserById,
} from '@/api/userController'

// 使用 message API
const [messageApi, contextHolder] = message.useMessage()

// 表格列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 100,
  },
  {
    title: '头像',
    key: 'userAvatar',
    width: 60,
  },
  {
    title: '用户名称',
    dataIndex: 'userName',
    key: 'userName',
    width: 120,
  },
  {
    title: '用户账号',
    dataIndex: 'userAccount',
    key: 'userAccount',
    width: 120,
  },
  {
    title: '用户角色',
    key: 'userRole',
    width: 90,
  },
  {
    title: '用户简介',
    dataIndex: 'userProfile',
    key: 'userProfile',
    ellipsis: true,
    width: 200,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 160,
  },
  {
    title: '操作',
    key: 'action',
    width: 220,
  },
]

// 响应式数据
const userList = ref<API.UserVo[]>([])
const loading = ref(false)
const modalVisible = ref(false)
const detailModalVisible = ref(false)
const modalLoading = ref(false)
const modalType = ref<'add' | 'edit'>('add')
const currentUser = ref<API.UserVo | null>(null)
const formRef = ref()

// 搜索参数
const searchParams = reactive<API.UserQueryDto>({
  pageNo: 1,
  pageSize: 10,
  userName: '',
  userAccount: '',
  userRole: '',
})

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`,
})

// 表单数据（统一结构，包含所有可能的字段）
const formData = reactive({
  id: undefined as number | undefined,
  userName: '',
  userAccount: '',
  userRole: '',
  userAvatar: '',
  userProfile: '',
})

// 表单验证规则
const formRules = {
  userName: [
    { required: true, message: '请输入用户名称', trigger: 'blur' },
    { min: 2, max: 50, message: '用户名称长度为2-50个字符', trigger: 'blur' },
  ],
  userAccount: [
    { required: true, message: '请输入用户账号', trigger: 'blur' },
    { min: 3, max: 20, message: '用户账号长度为3-20个字符', trigger: 'blur' },
  ],
  userRole: [{ required: true, message: '请选择用户角色', trigger: 'change' }],
}

// 获取用户头像
const getUserAvatar = (user: API.UserVo) => {
  if (user.userAvatar && user.userAvatar.trim() !== '') {
    return user.userAvatar
  }
  // 基于用户ID生成默认头像
  const userId = user.id || 1
  const avatarIndex = ((userId - 1) % 20) + 1
  const paddedIndex = avatarIndex.toString().padStart(2, '0')
  return `/unnamed_${paddedIndex}.png`
}

// 获取角色颜色
const getRoleColor = (role: string) => {
  const colorMap: Record<string, string> = {
    admin: 'red',
    user: 'blue',
  }
  return colorMap[role] || 'default'
}

// 获取角色文本
const getRoleText = (role: string) => {
  const textMap: Record<string, string> = {
    admin: '管理员',
    user: '普通用户',
  }
  return textMap[role] || role
}

// 获取用户列表
const fetchUserList = async (params?: Partial<API.UserQueryDto>) => {
  loading.value = true
  try {
    const queryParams = {
      ...searchParams,
      ...params,
    }

    console.log('查询参数:', queryParams)
    const response = await listUserVoByPage(queryParams)
    console.log('用户列表响应:', response)

    if (response && response.data && response.data.code === 0 && response.data.data) {
      userList.value = response.data.data.records || []
      pagination.total = response.data.data.total || 0
      pagination.current = response.data.data.pageNo || 1
    } else {
      messageApi.error(response?.data?.message || '获取用户列表失败')
    }
  } catch (error: any) {
    console.error('获取用户列表失败:', error)
    if (error?.code === 'ERR_NETWORK') {
      messageApi.error('网络连接失败，请检查网络或后端服务')
    } else {
      messageApi.error('获取用户列表失败：' + (error?.message || '未知错误'))
    }
  } finally {
    loading.value = false
  }
}

// 搜索用户
const handleSearch = () => {
  searchParams.pageNo = 1
  pagination.current = 1
  fetchUserList()
}

// 重置搜索
const handleReset = () => {
  searchParams.userName = ''
  searchParams.userAccount = ''
  searchParams.userRole = ''
  searchParams.pageNo = 1
  pagination.current = 1
  fetchUserList()
}

// 表格变化处理
const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  searchParams.pageNo = pag.current
  searchParams.pageSize = pag.pageSize
  fetchUserList()
}

// 添加用户
const handleAddUser = () => {
  modalType.value = 'add'
  modalVisible.value = true
  resetFormData()
}

// 编辑用户
const handleEdit = async (record: API.UserVo) => {
  modalType.value = 'edit'
  modalVisible.value = true

  // 获取用户详细信息
  try {
    const response = await getUserById({ id: record.id! })
    if (response && response.data && response.data.code === 0 && response.data.data) {
      const user = response.data.data
      formData.userName = user.userName || ''
      formData.userAccount = user.userAccount || ''
      formData.userRole = user.userRole || ''
      formData.userAvatar = user.userAvatar || ''
      formData.userProfile = user.userProfile || ''
      if (modalType.value === 'edit') {
        formData.id = user.id
      }
    }
  } catch (error: any) {
    console.error('获取用户详情失败:', error)
    messageApi.error('获取用户详情失败')
  }
}

// 查看用户详情
const handleViewDetail = async (record: API.UserVo) => {
  try {
    const response = await getUserById({ id: record.id! })
    if (response && response.data && response.data.code === 0 && response.data.data) {
      currentUser.value = response.data.data
      detailModalVisible.value = true
    }
  } catch (error: any) {
    console.error('获取用户详情失败:', error)
    messageApi.error('获取用户详情失败')
  }
}

// 删除用户
const handleDelete = async (record: API.UserVo) => {
  try {
    const response = await deleteUser({ id: record.id! })
    if (response && response.data && response.data.code === 0) {
      messageApi.success('删除用户成功')
      fetchUserList()
    } else {
      messageApi.error(response?.data?.message || '删除用户失败')
    }
  } catch (error: any) {
    console.error('删除用户失败:', error)
    messageApi.error('删除用户失败：' + (error?.message || '未知错误'))
  }
}

// 弹窗确定
const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    modalLoading.value = true

    if (modalType.value === 'add') {
      // 构造添加用户的数据
      const addData: API.UserAddDto = {
        userName: formData.userName,
        userAccount: formData.userAccount,
        userRole: formData.userRole,
        userAvatar: formData.userAvatar,
        userProfile: formData.userProfile,
      }
      const response = await addUser(addData)
      if (response && response.data && response.data.code === 0) {
        messageApi.success('添加用户成功')
        modalVisible.value = false
        fetchUserList()
      } else {
        messageApi.error(response?.data?.message || '添加用户失败')
      }
    } else {
      // 构造更新用户的数据
      const updateData: API.UserUpdateDto = {
        id: formData.id!,
        userName: formData.userName,
        userRole: formData.userRole,
        userAvatar: formData.userAvatar,
        userProfile: formData.userProfile,
      }
      const response = await updateUser(updateData)
      if (response && response.data && response.data.code === 0) {
        messageApi.success('更新用户成功')
        modalVisible.value = false
        fetchUserList()
      } else {
        messageApi.error(response?.data?.message || '更新用户失败')
      }
    }
  } catch (error: any) {
    console.error('操作失败:', error)
    if (error?.errorFields) {
      messageApi.error('请检查表单输入')
    } else {
      messageApi.error('操作失败：' + (error?.message || '未知错误'))
    }
  } finally {
    modalLoading.value = false
  }
}

// 弹窗取消
const handleModalCancel = () => {
  modalVisible.value = false
  resetFormData()
}

// 重置表单数据
const resetFormData = () => {
  formData.id = undefined
  formData.userName = ''
  formData.userAccount = ''
  formData.userRole = ''
  formData.userAvatar = ''
  formData.userProfile = ''
  formRef.value?.resetFields()
}

// 格式化日期，只显示日期部分（YYYY-MM-DD）
const formatDate = (dateString?: string) => {
  if (!dateString) return '-'

  try {
    // 处理 ISO 格式的日期字符串
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return dateString // 如果解析失败，返回原字符串

    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')

    return `${year}-${month}-${day}`
  } catch (error) {
    console.warn('日期格式化失败:', error)
    return dateString
  }
}

// 移动端滚动条优化
const enhanceMobileScrolling = () => {
  const isMobile = window.innerWidth <= 768
  if (!isMobile) return

  // 等待表格渲染完成
  setTimeout(() => {
    const tableWrappers = document.querySelectorAll('.ant-table-wrapper')

    tableWrappers.forEach((wrapper) => {
      const htmlWrapper = wrapper as HTMLElement

      // 添加触摸开始事件，使滚动条更明显
      htmlWrapper.addEventListener('touchstart', () => {
        htmlWrapper.classList.add('touch-active')

        // 临时显示滚动提示（仅在第一次触摸时显示）
        if (!htmlWrapper.hasAttribute('data-hint-shown')) {
          const scrollHint = document.createElement('div')
          scrollHint.innerHTML = '⬅️ 滑动查看更多内容 ➡️'
          scrollHint.style.cssText = `
            position: absolute;
            top: -35px;
            left: 50%;
            transform: translateX(-50%);
            background: rgba(64, 87, 109, 0.95);
            color: white;
            padding: 6px 16px;
            border-radius: 16px;
            font-size: 12px;
            font-weight: bold;
            z-index: 1000;
            box-shadow: 0 2px 8px rgba(0,0,0,0.2);
            animation: slideDown 0.3s ease, pulse 1s ease infinite;
          `
          htmlWrapper.appendChild(scrollHint)
          htmlWrapper.setAttribute('data-hint-shown', 'true')

          // 4秒后移除提示
          setTimeout(() => {
            if (scrollHint && scrollHint.parentNode) {
              scrollHint.style.animation = 'slideUp 0.3s ease'
              setTimeout(() => scrollHint.remove(), 300)
            }
          }, 4000)
        }
      })

      // 添加触摸结束事件
      htmlWrapper.addEventListener('touchend', () => {
        htmlWrapper.classList.remove('touch-active')
      })

      // 添加触摸移动事件，增强滚动反馈
      htmlWrapper.addEventListener('touchmove', () => {
        // 滚动时增加视觉反馈
        htmlWrapper.style.filter = 'brightness(1.05)'
      })

      // 添加滚动事件，显示滚动进度和重置滤镜
      htmlWrapper.addEventListener('scroll', (e) => {
        const target = e.target as HTMLElement
        if (!target) return

        const scrollLeft = target.scrollLeft
        const scrollWidth = target.scrollWidth
        const clientWidth = target.clientWidth
        const scrollPercentage = (scrollLeft / (scrollWidth - clientWidth)) * 100

        // 滚动时重置视觉效果
        htmlWrapper.style.filter = 'none'

        // 根据滚动位置提供视觉反馈
        if (scrollPercentage > 90) {
          // 接近末尾时显示提示
          if (!htmlWrapper.querySelector('.scroll-end-hint')) {
            const endHint = document.createElement('div')
            endHint.className = 'scroll-end-hint'
            endHint.innerHTML = '✓ 已到达末尾'
            endHint.style.cssText = `
              position: absolute;
              top: -25px;
              right: 10px;
              background: rgba(64, 87, 109, 0.9);
              color: white;
              padding: 3px 8px;
              border-radius: 8px;
              font-size: 10px;
              z-index: 1000;
            `
            htmlWrapper.appendChild(endHint)
            setTimeout(() => endHint && endHint.remove(), 2000)
          }
        }
      })
    })
  }, 500)
}

// 组件挂载时获取数据并优化移动端滚动
onMounted(() => {
  fetchUserList()
  enhanceMobileScrolling()

  // 监听窗口大小变化，重新优化滚动
  window.addEventListener('resize', enhanceMobileScrolling)
})

// 组件卸载时清理事件监听器
onUnmounted(() => {
  window.removeEventListener('resize', enhanceMobileScrolling)
})
</script>

<style scoped>
.user-manage-page {
  padding: 24px;
  background: #fffff0;
  min-height: 100vh;
  box-sizing: border-box;
}

.page-content {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.search-section {
  margin-bottom: 16px;
  padding: 12px 16px;
  background: #fafafa;
  border-radius: 6px;
  border: 1px solid #f0f0f0;
}

/* 表格样式优化 */
:deep(.ant-table) {
  background: #fff;
}

:deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
  color: #262626;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: #f5f5f5;
}

/* 头像样式 */
:deep(.ant-avatar) {
  border: 2px solid #f0f0f0;
  transition: all 0.3s;
}

:deep(.ant-avatar:hover) {
  border-color: #1890ff;
  transform: scale(1.1);
}

/* 标签样式 */
:deep(.ant-tag) {
  border-radius: 4px;
  font-weight: 500;
  border: none;
}

/* 按钮样式 */
:deep(.ant-btn-link) {
  padding: 4px 8px;
  height: auto;
  border-radius: 4px;
  transition: all 0.3s;
}

:deep(.ant-btn-link:hover) {
  background: rgba(24, 144, 255, 0.06);
}

:deep(.ant-btn-link.ant-btn-dangerous:hover) {
  background: rgba(255, 77, 79, 0.06);
}

/* 搜索区域样式 */
:deep(.ant-input-affix-wrapper) {
  border-radius: 6px;
  transition: all 0.3s;
}

:deep(.ant-input-affix-wrapper:hover) {
  border-color: #40a9ff;
}

:deep(.ant-select) {
  border-radius: 6px;
}

/* 弹窗样式 */
:deep(.ant-modal-content) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.ant-modal-header) {
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.ant-modal-title) {
  font-weight: 600;
  color: #262626;
}

/* 表单样式 */
:deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: #262626;
}

:deep(.ant-input) {
  border-radius: 6px;
}

:deep(.ant-input:focus) {
  border-color: #40a9ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

:deep(.ant-select-selector) {
  border-radius: 6px !important;
}

/* 描述列表样式 */
:deep(.ant-descriptions-bordered) {
  border-radius: 6px;
  overflow: hidden;
}

:deep(.ant-descriptions-bordered .ant-descriptions-item-label) {
  background: #fafafa;
  font-weight: 500;
}

/* 分页样式 */
:deep(.ant-pagination) {
  margin-top: 16px;
  text-align: right;
}

/* 操作按钮样式 */
.search-section :deep(.ant-btn) {
  border-radius: 6px;
}

.search-section :deep(.ant-btn-primary) {
  background: #1890ff;
  border-color: #1890ff;
}

.search-section :deep(.ant-btn-primary:hover) {
  background: #40a9ff;
  border-color: #40a9ff;
}

/* 卡片样式 */
:deep(.ant-card) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  width: 100% !important;
  max-width: none !important;
}

:deep(.ant-card-head) {
  border-bottom: 1px solid #f0f0f0;
}

:deep(.ant-card-head-title) {
  font-weight: 600;
  color: #262626;
}

:deep(.ant-card-body) {
  padding: 16px !important;
  overflow-x: auto !important;
}

/* 操作按钮样式 */
.ant-table-tbody tr td:last-child .ant-space {
  justify-content: flex-start;
  flex-wrap: nowrap;
  width: 100%;
  gap: 8px !important;
}

.action-btn {
  font-size: 13px !important;
  padding: 6px 12px !important;
  height: 32px !important;
  min-width: 60px !important;
  border-radius: 6px !important;
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
  font-weight: 500 !important;
  border: 1px solid transparent !important;
  transition: all 0.2s !important;
}

.action-btn .anticon {
  margin-right: 4px !important;
  font-size: 12px !important;
}

.action-btn:hover {
  background: rgba(24, 144, 255, 0.08) !important;
  border-color: rgba(24, 144, 255, 0.2) !important;
  color: #40a9ff !important;
}

.action-btn.ant-btn-dangerous {
  color: #ff4d4f !important;
}

.action-btn.ant-btn-dangerous:hover {
  color: #ff7875 !important;
  background: rgba(255, 77, 79, 0.08) !important;
  border-color: rgba(255, 77, 79, 0.2) !important;
}

/* 表格整体样式优化 */
:deep(.ant-table) {
  width: 100%;
}

:deep(.ant-table-container) {
  width: 100%;
}

:deep(.ant-table-content) {
  width: 100%;
  overflow-x: visible !important;
}

/* 确保表格在大屏幕上能够充分利用空间 */
:deep(.ant-table table) {
  width: 100%;
  min-width: 1100px;
  max-width: 100%;
}

/* 响应式设计 */
@media (max-width: 1150px) {
  /* 在较小屏幕上启用横向滚动 */
  :deep(.ant-table table) {
    min-width: 1100px;
  }

  :deep(.ant-table-content) {
    overflow-x: auto !important;
  }
}

@media (max-width: 768px) {
  .user-manage-page {
    padding: 12px;
    min-height: 100vh; /* 确保页面占满全屏 */
  }

  .page-content {
    min-height: calc(100vh - 40px); /* 增加卡片最小高度 */
    display: flex;
    flex-direction: column;
  }

  .search-section {
    padding: 12px; /* 增加搜索区域内边距 */
    margin-bottom: 16px; /* 增加底部间距 */
  }

  .search-section :deep(.ant-col) {
    margin-bottom: 10px; /* 稍微增加间距 */
  }

  /* 移动端表格横向滚动优化 */
  :deep(.ant-table-wrapper) {
    overflow-x: auto !important;
    -webkit-overflow-scrolling: touch; /* iOS 滑动优化 */
  }

  :deep(.ant-table) {
    font-size: 12px;
    min-width: 800px; /* 确保表格有足够宽度触发滚动 */
    flex: 1; /* 让表格占满可用空间 */
  }

  /* 增加表格行高，让内容更饱满 */
  :deep(.ant-table-tbody > tr) {
    height: 64px; /* 增加行高 */
  }

  :deep(.ant-table-tbody > tr > td) {
    height: 64px; /* 确保单元格高度 */
    vertical-align: middle; /* 垂直居中 */
    padding: 12px 6px !important; /* 增加内边距 */
  }

  :deep(.ant-table-thead > tr > th) {
    height: 48px; /* 增加表头高度 */
    padding: 12px 6px !important; /* 增加表头内边距 */
    font-size: 12px;
    white-space: nowrap; /* 防止表头换行 */
  }

  :deep(.ant-table-container) {
    overflow-x: auto !important;
    -webkit-overflow-scrolling: touch;
  }

  :deep(.ant-table-content) {
    overflow-x: auto !important;
    -webkit-overflow-scrolling: touch;
  }

  :deep(.ant-table table) {
    min-width: 800px !important; /* 保证表格宽度 */
  }

  /* 增强滚动区域的触摸响应 */
  :deep(.ant-table-wrapper) {
    padding-bottom: 4px; /* 为滚动条留出更多空间 */
    margin-bottom: 8px;
    position: relative;
    transition: box-shadow 0.3s ease;
  }

  /* 触摸时的强化样式 */
  :deep(.ant-table-wrapper.touch-active) {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
  }

  /* 添加滚动指示器 */
  :deep(.ant-table-wrapper)::after {
    content: '⬅️ 滑动查看 ➡️';
    position: absolute;
    bottom: -20px;
    left: 50%;
    transform: translateX(-50%);
    font-size: 10px;
    color: #666;
    white-space: nowrap;
    opacity: 0.7;
    animation: fadeInOut 3s ease-in-out infinite;
  }

  @keyframes fadeInOut {
    0%,
    100% {
      opacity: 0.3;
    }
    50% {
      opacity: 0.8;
    }
  }

  /* 滚动提示动画 */
  @keyframes slideDown {
    0% {
      opacity: 0;
      transform: translateX(-50%) translateY(-10px);
    }
    100% {
      opacity: 1;
      transform: translateX(-50%) translateY(0);
    }
  }

  @keyframes slideUp {
    0% {
      opacity: 1;
      transform: translateX(-50%) translateY(0);
    }
    100% {
      opacity: 0;
      transform: translateX(-50%) translateY(-10px);
    }
  }

  /* 表头和表体样式已在上方重新定义，保持内容不换行 */
  :deep(.ant-table-tbody > tr > td) {
    white-space: nowrap; /* 防止内容换行 */
  }

  /* 在移动端优化操作按钮 */
  .action-btn {
    font-size: 11px !important;
    padding: 4px 8px !important;
    height: 28px !important;
    min-width: 45px !important;
  }

  /* 移动端表格提示 */
  .page-content::before {
    content: '👉 左右滑动查看更多';
    display: block;
    text-align: center;
    font-size: 12px;
    color: #999;
    padding: 8px;
    background: #f0f8ff;
    border-radius: 4px;
    margin-bottom: 12px;
    border: 1px dashed #d0d7de;
  }
}

/* 超小屏幕优化 */
@media (max-width: 480px) {
  .user-manage-page {
    padding: 8px;
  }

  :deep(.ant-table) {
    min-width: 700px; /* 稍小一些但仍保持可读性 */
    font-size: 11px;
  }

  /* 超小屏幕使用统一的滚动条样式，无需重复定义 */

  /* 增强触摸区域 */
  :deep(.ant-table-wrapper) {
    padding-bottom: 8px;
    margin-bottom: 12px;
    border-bottom: 1px dashed #ccc; /* 添加分隔线 */
  }

  /* 更醒目的滚动提示 */
  :deep(.ant-table-wrapper)::after {
    content: '👆 左右滑动查看所有列 👆';
    bottom: -28px;
    font-size: 11px;
    color: #666;
    font-weight: bold;
    background: rgba(255, 255, 255, 0.9);
    padding: 2px 8px;
    border-radius: 12px;
    border: 1px solid #ddd;
  }

  /* 超小屏幕保持适中的行高 */
  :deep(.ant-table-tbody > tr) {
    height: 56px; /* 超小屏幕适中行高 */
  }

  :deep(.ant-table-thead > tr > th) {
    padding: 8px 4px;
    font-size: 11px;
    height: 40px; /* 表头高度 */
  }

  :deep(.ant-table-tbody > tr > td) {
    padding: 8px 4px;
    font-size: 11px;
    height: 56px; /* 单元格高度 */
    vertical-align: middle;
  }

  .action-btn {
    font-size: 10px !important;
    padding: 3px 6px !important;
    height: 24px !important;
    min-width: 40px !important;
  }

  /* 更明显的滚动指示 */
  .page-content::before {
    content: '📱 左右滑动查看完整表格';
    font-size: 11px;
    padding: 6px;
    background: linear-gradient(90deg, #e3f2fd, #f3e5f5);
    animation: pulse 2s infinite;
  }

  @keyframes pulse {
    0%,
    100% {
      opacity: 0.7;
    }
    50% {
      opacity: 1;
    }
  }

  /* 增加表格容器的触摸提示 */
  :deep(.ant-table-container) {
    position: relative;
  }

  :deep(.ant-table-container)::before {
    content: '👈👉';
    position: absolute;
    top: 50%;
    right: 10px;
    transform: translateY(-50%);
    font-size: 16px;
    color: rgba(255, 107, 107, 0.7);
    z-index: 1;
    animation: bounce 1.5s infinite;
    pointer-events: none;
  }

  @keyframes bounce {
    0%,
    100% {
      transform: translateY(-50%) scale(1);
    }
    50% {
      transform: translateY(-50%) scale(1.2);
    }
  }
}
</style>
