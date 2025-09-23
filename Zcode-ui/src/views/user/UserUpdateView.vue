<template>
  <div class="update-container">
    <component :is="contextHolder" />
    <a-card class="update-screen">
      <div class="screen__content">
        <div class="form-header">
          <h2 class="form-title">个人信息编辑</h2>
          <p class="form-subtitle">完善您的个人资料，让其他人更好地了解您</p>
        </div>
        <a-form class="update-form" @submit.prevent="handleSubmit" autocomplete="off">
          <a-form-item class="update__field">
            <div class="inline-fields-container">
              <div class="inline-field">
                <label class="field-label">用户昵称</label>
                <a-input
                  class="update__input"
                  placeholder="请输入用户昵称"
                  v-model:value="updateForm.userName"
                  :disabled="loading"
                  autocomplete="off"
                />
              </div>
              <div class="inline-field">
                <label class="field-label">用户账号</label>
                <a-input
                  class="update__input"
                  placeholder="用户账号（不可修改）"
                  v-model:value="currentUser.userAccount"
                  disabled
                  autocomplete="off"
                />
              </div>
            </div>
          </a-form-item>
          <a-form-item class="update__field">
            <label class="field-label">用户头像</label>
            <div class="avatar-upload-section">
              <div class="avatar-container" @click="handleAvatarClick">
                <div v-if="updateForm.userAvatar" class="avatar-display">
                  <img :src="updateForm.userAvatar" alt="用户头像" class="avatar-image" />
                </div>
                <div v-else class="avatar-placeholder">
                  <div class="placeholder-content">点击上传头像</div>
                </div>
              </div>
              <div class="recommend-section">
                <a @click="showAvatarModal" class="recommend-link">推荐头像</a>
              </div>
            </div>
          </a-form-item>
          <a-form-item class="update__field">
            <label class="field-label">个人简介</label>
            <a-textarea
              class="update__textarea"
              placeholder="请输入个人简介（可选）"
              v-model:value="updateForm.userProfile"
              :disabled="loading"
              :rows="3"
              :max-length="200"
              show-count
            />
          </a-form-item>
          <a-button
            class="update__submit"
            type="primary"
            :loading="loading"
            block
            @click="handleSubmit"
          >
            {{ loading ? '保存中...' : '保存修改' }}
          </a-button>
        </a-form>
        <div class="back-section">
          <a @click="handleBack" class="back-link">返回首页</a>
        </div>
      </div>
      <div class="screen__background">
        <span class="screen__background__shape screen__background__shape4"></span>
        <span class="screen__background__shape screen__background__shape3"></span>
        <span class="screen__background__shape screen__background__shape2"></span>
        <span class="screen__background__shape screen__background__shape1"></span>
      </div>
    </a-card>
    
    <!-- 头像推荐弹窗 -->
    <a-modal
      v-model:open="avatarModalVisible"
      title="推荐头像"
      :footer="null"
      width="600px"
      centered
      class="avatar-modal"
    >
      <div 
        class="avatar-grid-modal" 
        @wheel="handleModalWheel"
        ref="avatarGridRef"
      >
        <div 
          v-for="avatar in recommendedAvatars" 
          :key="avatar"
          class="recommended-avatar-modal"
          @click="selectRecommendedAvatar(avatar)"
        >
          <img :src="avatar" :alt="avatar" />
        </div>
      </div>
    </a-modal>
    
    <!-- 隐藏的文件上传组件 -->
    <input
      ref="fileInputRef"
      type="file"
      accept="image/*"
      style="display: none"
      @change="handleFileSelect"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { updateUser, getLoginUser } from '@/api/userController'
import { useUserLoginStore } from '@/stores/UserLogin'

const router = useRouter()
const userLoginStore = useUserLoginStore()

// 使用 Ant Design Vue 的 message API
const [messageApi, contextHolder] = message.useMessage()

// 当前用户信息
const currentUser = ref<API.UserVo>({})

// 表单数据
const updateForm = reactive<API.UserUpdateDto>({
  userName: '',
  userAvatar: '',
  userProfile: '',
})

// 加载状态
const loading = ref(false)

// 文件列表
const fileList = ref([])

// 头像推荐弹窗显示状态
const avatarModalVisible = ref(false)

// 头像网格引用
const avatarGridRef = ref()

// 文件输入引用
const fileInputRef = ref()

// 推荐头像列表（20个）
const recommendedAvatars = ref([
  '/unnamed_1.png',
  '/unnamed_2.png',
  '/unnamed_3.png',
  '/unnamed_4.png',
  '/unnamed_5.png',
  '/unnamed_6.png',
  '/unnamed_7.png',
  '/unnamed_8.png',
  '/unnamed_9.png',
  '/unnamed_10.png',
  '/unnamed_11.png',
  '/unnamed_12.png',
  '/unnamed_13.png',
  '/unnamed_14.png',
  '/unnamed_15.png',
  '/unnamed_16.png',
  '/unnamed_17.png',
  '/unnamed_19.png',
  '/unnamed_20.png',
  '/favicon.ico'
])

// 显示头像推荐弹窗
const showAvatarModal = () => {
  avatarModalVisible.value = true
}

// 选择推荐头像
const selectRecommendedAvatar = (avatarUrl: string) => {
  updateForm.userAvatar = avatarUrl
  avatarModalVisible.value = false
  messageApi.success('头像已选择！')
}

// 处理弹窗滚轮事件
const handleModalWheel = (event: WheelEvent) => {
  event.preventDefault()
  if (avatarGridRef.value) {
    const scrollAmount = event.deltaY * 0.5 // 控制滚动速度
    avatarGridRef.value.scrollTop += scrollAmount
  }
}

// 处理头像点击
const handleAvatarClick = () => {
  if (fileInputRef.value) {
    fileInputRef.value.click()
  }
}

// 处理文件选择
const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (!file) return
  
  // 验证文件
  if (!beforeUpload(file)) return
  
  // 处理上传
  handleUpload({ file })
  
  // 清空input值，允许选择相同文件
  target.value = ''
}

// 随机趣味提示
const showRandomMessage = () => {
  const messages = [
    '换来换去还是你最好 💖',
    '这个头像很适合你呢 ✨',
    '新头像，新气象！ 🌟',
    '你的品味真不错 👍',
    '完美的选择！ 🎉'
  ]
  const randomMessage = messages[Math.floor(Math.random() * messages.length)]
  messageApi.success(randomMessage)
}

// 上传前检查
const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    messageApi.error('只能上传图片文件！')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    messageApi.error('图片大小不能超过2MB！')
    return false
  }
  return true
}

// 自定义上传处理
const handleUpload = (options: any) => {
  const { file } = options
  
  // 生成唯一文件名
  const timestamp = Date.now()
  const extension = file.name.split('.').pop()
  const fileName = `avatar_${timestamp}.${extension}`
  
  try {
    // 创建FormData用于文件上传（这里暂时模拟本地保存）
    const reader = new FileReader()
    reader.onload = (e) => {
      // 这里模拟将文件保存到public目录的逻辑
      // 在实际项目中，这里应该调用后端API上传到OSS
      const dataUrl = e.target?.result as string
      
      // 模拟上传成功，设置头像URL
      // 在实际应用中，这里应该是服务器返回的文件URL
      const avatarUrl = `/public/${fileName}`
      
      // 更新头像链接
      updateForm.userAvatar = avatarUrl
      
      // 显示随机趣味提示
      showRandomMessage()
      console.log('头像上传成功，文件名:', fileName)
    }
    
    reader.onerror = () => {
      messageApi.error('头像上传失败，请重试')
    }
    
    reader.readAsDataURL(file)
  } catch (error) {
    console.error('上传头像失败:', error)
    messageApi.error('头像上传失败，请重试')
  }
}

// 初始化页面数据
onMounted(async () => {
  console.log('个人信息编辑页面初始化')
  await loadUserInfo()
})

// 加载用户信息
const loadUserInfo = async () => {
  try {
    // 先从store获取用户信息
    const storeUser = userLoginStore.userlogin
    if (storeUser && storeUser.id) {
      currentUser.value = storeUser
      initFormData(storeUser)
      console.log('从store获取用户信息成功:', storeUser)
    } else {
      // 如果store中没有，从后端获取
      console.log('store中无用户信息，从后端获取...')
      const response = await getLoginUser()
      if (response && response.data && response.data.code === 0 && response.data.data) {
        currentUser.value = response.data.data
        initFormData(response.data.data)
        // 同步更新store
        userLoginStore.setLoginUser(response.data.data)
        console.log('从后端获取用户信息成功:', response.data.data)
      } else {
        console.log('获取用户信息失败，跳转到登录页')
        messageApi.error('获取用户信息失败，请重新登录')
        setTimeout(() => {
          router.push('/user/login')
        }, 1500)
      }
    }
  } catch (error: any) {
    console.error('加载用户信息失败:', error)
    if (error?.response?.status === 401) {
      messageApi.error('用户未登录，请重新登录')
      setTimeout(() => {
        router.push('/user/login')
      }, 1500)
    } else {
      messageApi.error('加载用户信息失败，请稍后重试')
    }
  }
}

// 初始化表单数据
const initFormData = (user: API.UserVo) => {
  // 处理用户昵称，避免显示null
  updateForm.userName = user.userName && user.userName !== 'null' && user.userName.trim() !== '' ? user.userName : ''
  updateForm.userAvatar = user.userAvatar || ''
  updateForm.userProfile = user.userProfile || ''
}

// 处理返回首页
const handleBack = () => {
  router.push('/')
}

// 处理表单提交
const handleSubmit = async () => {
  console.log('handleSubmit 被调用')
  console.log('表单数据:', updateForm)

  // 表单验证
  if (!updateForm.userName?.trim()) {
    console.log('用户昵称为空')
    messageApi.error('请输入用户昵称')
    return
  }

  // 用户昵称长度校验
  if (updateForm.userName.trim().length < 2) {
    console.log('用户昵称太短:', updateForm.userName.trim().length)
    messageApi.error('用户昵称至少需要2个字符')
    return
  }

  if (updateForm.userName.trim().length > 20) {
    console.log('用户昵称太长:', updateForm.userName.trim().length)
    messageApi.error('用户昵称不能超过20个字符')
    return
  }

  // 个人简介长度校验
  if (updateForm.userProfile && updateForm.userProfile.length > 200) {
    console.log('个人简介太长:', updateForm.userProfile.length)
    messageApi.error('个人简介不能超过200个字符')
    return
  }

  loading.value = true

  try {
    // 构建更新数据，包含用户ID
    const updateData: API.UserUpdateDto = {
      id: currentUser.value.id,
      userName: updateForm.userName.trim(),
      userAvatar: updateForm.userAvatar?.trim() || '',
      userProfile: updateForm.userProfile?.trim() || '',
    }

    console.log('准备提交更新数据:', updateData)
    const response = await updateUser(updateData)

    if (response.data.code === 0) {
      // 更新成功
      console.log('用户信息更新成功')
      messageApi.success('🎉 个人信息更新成功！')

      // 更新本地用户信息
      const updatedUser = {
        ...currentUser.value,
        userName: updateData.userName,
        userAvatar: updateData.userAvatar,
        userProfile: updateData.userProfile,
      }
      currentUser.value = updatedUser
      userLoginStore.setLoginUser(updatedUser)

      // 延迟跳转，确保用户能看到成功提示
      setTimeout(() => {
        router.push('/')
      }, 1500)
    } else {
      console.log('更新失败:', response.data.message)
      messageApi.error(response.data.message || '更新失败，请重试')
    }
  } catch (error: any) {
    console.error('更新用户信息失败:', error)
    if (error?.response?.status === 401) {
      messageApi.error('登录已过期，请重新登录')
      setTimeout(() => {
        router.push('/user/login')
      }, 1500)
    } else if (error?.code === 'ERR_NETWORK') {
      messageApi.error('网络连接失败，请检查网络或后端服务是否启动')
    } else {
      messageApi.error('更新失败：' + (error?.response?.data?.message || error?.message || '未知错误'))
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css?family=Raleway:400,700');

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
  font-family: 'Raleway', 'Microsoft YaHei', sans-serif;
}

.update-container {
  background: #fffff0;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
}

/* 确保 Message 组件显示在编辑页面之上 */
:global(.ant-message) {
  z-index: 10000 !important;
}

.update-screen {
  background: rgb(var(--card)) !important;
  position: relative;
  height: 800px;
  width: 580px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1) !important;
  border: 1px solid rgb(var(--border)) !important;
  border-radius: var(--radius) !important;
  overflow: hidden;
}

.update-screen :deep(.ant-card-body) {
  padding: 0;
  height: 100%;
}

.screen__content {
  z-index: 1;
  position: relative;
  height: 100%;
}

.screen__background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  -webkit-clip-path: inset(0 0 0 0);
  clip-path: inset(0 0 0 0);
}

.screen__background__shape {
  transform: rotate(45deg);
  position: absolute;
}

.screen__background__shape1 {
  height: 520px;
  width: 520px;
  background: #ffefd5;
  top: -50px;
  right: 120px;
  border-radius: 0 72px 0 0;
}

.screen__background__shape2 {
  height: 220px;
  width: 220px;
  background: #ffefd5;
  top: -172px;
  right: 0;
  border-radius: 32px;
}

.screen__background__shape3 {
  height: 540px;
  width: 190px;
  background: #ffefd5;
  top: -24px;
  right: 0;
  border-radius: 32px;
}

.screen__background__shape4 {
  height: 400px;
  width: 200px;
  background: #ffefd5;
  top: 520px;
  right: 50px;
  border-radius: 60px;
}

.form-header {
  text-align: center;
  padding: 30px 30px 20px;
  position: relative;
}


.form-title {
  color: rgb(var(--foreground));
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 12px;
  background: linear-gradient(135deg, rgb(var(--primary)), #40a9ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.form-subtitle {
  color: rgb(var(--muted-foreground));
  font-size: 15px;
  font-weight: 400;
  line-height: 1.5;
  max-width: 300px;
  margin: 0 auto;
}

.update-form {
  width: 520px;
  padding: 20px 30px;
  padding-top: 10px;
}

.update__field {
  padding: 16px 0px;
  position: relative;
  transition: all 0.3s ease;
}

.update__field:hover {
  transform: translateY(-1px);
}

.update__field :deep(.ant-form-item) {
  margin-bottom: 0;
}

.update__field :deep(.ant-form-item-control-input) {
  min-height: auto;
}

.field-label {
  display: flex;
  align-items: center;
  color: rgb(var(--foreground));
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 12px;
  margin-left: 2px;
  gap: 8px;
}

.field-label::before {
  content: "●";
  color: rgb(var(--primary));
  font-size: 8px;
}

.update__input {
  border: none !important;
  border-bottom: 2px solid rgb(var(--border)) !important;
  background: none !important;
  padding: 10px !important;
  padding-left: 8px !important;
  font-weight: 500 !important;
  width: 100% !important;
  transition: 0.2s !important;
  color: rgb(var(--foreground)) !important;
  font-size: 14px !important;
  box-shadow: none !important;
  border-radius: 0 !important;
}

.update__input:active,
.update__input:focus,
.update__input:hover {
  outline: none !important;
  border-bottom-color: rgb(var(--primary)) !important;
  box-shadow: none !important;
}

.update__field :deep(.ant-input) {
  border: none !important;
  border-bottom: 2px solid rgb(var(--border)) !important;
  background: none !important;
  padding: 10px !important;
  padding-left: 8px !important;
  font-weight: 500 !important;
  width: 100% !important;
  transition: 0.2s !important;
  color: rgb(var(--foreground)) !important;
  font-size: 14px !important;
  box-shadow: none !important;
  border-radius: 0 !important;
}

.update__field :deep(.ant-input:focus) {
  border-bottom-color: rgb(var(--primary)) !important;
  box-shadow: none !important;
}

.update__field :deep(.ant-input:disabled) {
  background: rgba(0, 0, 0, 0.02) !important;
  color: rgb(var(--muted-foreground)) !important;
  opacity: 0.8;
}

.update__textarea {
  border: 1px solid rgb(var(--border)) !important;
  background: none !important;
  padding: 10px !important;
  font-weight: 500 !important;
  width: 100% !important;
  transition: 0.2s !important;
  color: rgb(var(--foreground)) !important;
  font-size: 14px !important;
  box-shadow: none !important;
  border-radius: 6px !important;
  resize: vertical !important;
}

.update__field :deep(.ant-input) {
  border: 1px solid rgb(var(--border)) !important;
  background: none !important;
  padding: 10px !important;
  font-weight: 500 !important;
  width: 100% !important;
  transition: 0.2s !important;
  color: rgb(var(--foreground)) !important;
  font-size: 14px !important;
  box-shadow: none !important;
  border-radius: 6px !important;
}

.update__field :deep(.ant-input:focus) {
  border-color: rgb(var(--primary)) !important;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1) !important;
}

.update__input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.update__input::placeholder {
  color: #999;
  font-weight: 400;
}

.update__submit {
  background: linear-gradient(135deg, rgb(var(--primary)), #40a9ff) !important;
  font-size: 14px !important;
  margin-top: 16px !important;
  margin-bottom: 16px !important;
  padding: 12px 20px !important;
  border-radius: 8px !important;
  border: none !important;
  text-transform: none !important;
  font-weight: 600 !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  width: 280px !important;
  max-width: 280px !important;
  margin-left: auto !important;
  margin-right: auto !important;
  color: white !important;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3) !important;
  cursor: pointer !important;
  transition: all 0.3s ease !important;
  height: auto !important;
  position: relative !important;
  overflow: hidden !important;
}

.update__submit::before {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.update__submit:hover::before {
  left: 100%;
}

.update__submit:active,
.update__submit:focus,
.update__submit:hover {
  outline: none !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.4) !important;
}

.update__submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.back-section {
  position: absolute;
  height: 80px;
  width: 160px;
  text-align: center;
  bottom: 20px;
  right: 0px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-link {
  color: #1890ff;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  cursor: pointer;
  user-select: none;
}

.back-link:hover {
  color: #40a9ff;
  text-decoration: underline;
}

/* 头像上传相关样式 */
.avatar-upload-section {
  display: flex;
  flex-direction: row;
  gap: 20px;
  align-items: flex-start;
}

.avatar-container {
  width: 140px;
  height: 140px;
  border: 2px dashed rgb(var(--border));
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
  position: relative;
}

.avatar-container:hover {
  border-color: rgb(var(--primary));
  background: rgba(24, 144, 255, 0.05);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
}

.avatar-display {
  width: 100%;
  height: 100%;
  position: relative;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.avatar-container:hover .avatar-image {
  transform: scale(1.05);
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.02);
}

.placeholder-content {
  font-size: 14px;
  color: rgb(var(--muted-foreground));
  font-weight: 500;
  text-align: center;
}

/* 内联字段样式 - 强制一行布局 */
.inline-fields-container {
  display: flex !important;
  gap: 16px !important;
  width: 100% !important;
  flex-wrap: nowrap !important;
}

.inline-field {
  flex: 1 !important;
  display: flex !important;
  flex-direction: column !important;
  min-width: 0 !important;
  width: 100% !important;
}

.inline-field .field-label {
  margin-bottom: 8px !important;
}

.inline-field .update__input {
  width: 100% !important;
}

.upload-controls {
  display: flex;
  gap: 20px;
  align-items: center;
}

.upload-area {
  position: relative;
  flex-shrink: 0;
}

.recommend-section {
  display: flex;
  align-items: center;
  height: 140px;
}

.recommend-link {
  color: #1890ff;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  cursor: pointer;
  user-select: none;
}

.recommend-link:hover {
  color: #40a9ff;
  text-decoration: underline;
}

.avatar-uploader {
  flex-shrink: 0;
}

.avatar-uploader :deep(.ant-upload) {
  width: 140px !important;
  height: 140px !important;
  border: 2px dashed rgb(var(--border)) !important;
  border-radius: 8px !important;
  background: rgba(0, 0, 0, 0.02) !important;
  transition: all 0.3s !important;
}

.avatar-uploader :deep(.ant-upload:hover) {
  border-color: rgb(var(--primary)) !important;
  background: rgba(24, 144, 255, 0.05) !important;
}

.upload-placeholder,
.upload-change {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 8px;
  cursor: pointer;
}

.upload-icon {
  font-size: 24px;
  color: rgb(var(--muted-foreground));
}

.upload-text {
  font-size: 14px;
  color: rgb(var(--muted-foreground));
  font-weight: 500;
}

.avatar-recommendations {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(24, 144, 255, 0.1);
  border-radius: 4px;
  padding: 4px 8px;
}

/* 头像推荐弹窗样式 */
:deep(.avatar-modal .ant-modal-content) {
  background: #ffefd5 !important;
  border-radius: 12px !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15) !important;
}

:deep(.avatar-modal .ant-modal-header) {
  background: #ffefd5 !important;
  border-bottom: 1px solid #f0e6d2 !important;
  border-radius: 12px 12px 0 0 !important;
}

:deep(.avatar-modal .ant-modal-title) {
  color: #8b4513 !important;
  font-weight: 600 !important;
  font-size: 16px !important;
}

:deep(.avatar-modal .ant-modal-body) {
  background: #ffefd5 !important;
  padding: 20px !important;
}

.avatar-grid-modal {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: thin;
  scrollbar-color: #1890ff #f0f0f0;
}

.avatar-grid-modal::-webkit-scrollbar {
  width: 8px;
}

.avatar-grid-modal::-webkit-scrollbar-track {
  background: #f0f0f0;
  border-radius: 4px;
}

.avatar-grid-modal::-webkit-scrollbar-thumb {
  background: #1890ff;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.avatar-grid-modal::-webkit-scrollbar-thumb:hover {
  background: #40a9ff;
}

.recommended-avatar-modal {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  will-change: transform;
  backface-visibility: hidden;
}

.recommended-avatar-modal:hover {
  border-color: #1890ff;
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

.recommended-avatar-modal img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: opacity 0.2s ease;
}

/* 响应式设计 */
@media (max-width: 640px) {
  .update-container {
    padding: 10px;
  }

  .update-screen {
    width: 100%;
    max-width: 580px;
    height: 750px;
  }

  .update-form {
    padding: 15px 20px;
    width: 100%;
  }

  .form-header {
    padding: 20px 20px 5px;
  }

  .inline-fields-container {
    flex-direction: column;
    gap: 16px;
  }

  .upload-controls {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .recommend-section {
    height: auto;
  }

  .avatar-grid-modal {
    grid-template-columns: repeat(auto-fill, minmax(60px, 1fr));
    gap: 8px;
  }

  .recommended-avatar-modal {
    width: 60px;
    height: 60px;
  }
}
</style>

