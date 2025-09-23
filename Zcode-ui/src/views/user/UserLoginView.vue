<template>
  <div class="login-container">
    <component :is="contextHolder" />
    <a-card class="login-screen">
      <div class="screen__content">
        <a-form class="login-form" @submit.prevent="handleSubmit" autocomplete="off">
          <a-form-item class="login__field">
            <a-input
              class="login__input"
              placeholder="用户账号 / 邮箱"
              v-model:value="loginForm.userAccount"
              :disabled="loading"
              autocomplete="off"
            />
          </a-form-item>
          <a-form-item class="login__field">
            <a-input-password
              class="login__input"
              placeholder="密码"
              v-model:value="loginForm.userPassword"
              :disabled="loading"
              :visibilityToggle="true"
              autocomplete="new-password"
            />
          </a-form-item>
          <a-button
            class="login__submit"
            type="primary"
            :loading="loading"
            block
            @click="handleSubmit"
          >
            {{ loading ? '登录中...' : '立即登录' }}
          </a-button>
        </a-form>
        <div class="register-section">
          <a @click="handleRegister" class="register-link">注册</a>
        </div>
      </div>
      <div class="screen__background">
        <span class="screen__background__shape screen__background__shape4"></span>
        <span class="screen__background__shape screen__background__shape3"></span>
        <span class="screen__background__shape screen__background__shape2"></span>
        <span class="screen__background__shape screen__background__shape1"></span>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { userLogin } from '@/api/userController'
import { useUserLoginStore } from '@/stores/UserLogin'

const router = useRouter()
const userLoginStore = useUserLoginStore()

// 使用 Ant Design Vue 的 message API
const [messageApi, contextHolder] = message.useMessage()

// 使用全局 message 配置，确保消息居中显示
onMounted(() => {
  // Message 配置已在 main.ts 中全局配置，消息将自动居中显示
})

// 表单数据
const loginForm = reactive<API.UserLoginDto>({
  userAccount: '',
  userPassword: '',
})

// 加载状态
const loading = ref(false)

// 处理注册链接点击
const handleRegister = () => {
  router.push('/user/register')
}

// 处理登录提交
const handleSubmit = async () => {
  console.log('handleSubmit 被调用')
  console.log('表单数据:', loginForm)

  // 表单验证
  if (!loginForm.userAccount?.trim()) {
    console.log('用户账号为空')
    messageApi.error('请输入用户账号')
    return
  }
  if (!loginForm.userPassword?.trim()) {
    console.log('密码为空')
    messageApi.error('请输入密码')
    return
  }

  // 密码长度校验
  if (loginForm.userPassword.length <= 8) {
    console.log('密码长度不足:', loginForm.userPassword.length)
    messageApi.error('密码长度必须大于8位')
    return
  }

  loading.value = true

  try {
    const response = await userLogin(loginForm)

    if (response.data.code === 0 && response.data.data) {
      // 登录成功，保存登录态到全局状态
      userLoginStore.setLoginUser(response.data.data)
      console.log('准备显示登录成功消息')
      messageApi.success('🎉 登录成功！欢迎回来！')

      // 延迟跳转，确保用户能看到成功提示
      setTimeout(() => {
        // 获取重定向地址
        const redirect = new URLSearchParams(window.location.search).get('redirect')
        // 跳转到重定向地址或首页
        if (redirect) {
          window.location.href = redirect
        } else {
          router.push('/')
        }
      }, 1500)
    } else {
      // 检查是否是用户不存在的错误
      if (response.data.message && response.data.message.includes('用户不存在')) {
        // 弹出注册提示对话框
        Modal.confirm({
          title: '用户不存在',
          content: '该账号尚未注册，是否前往注册页面创建新账号？',
          okText: '确定',
          cancelText: '取消',
          centered: true,
          width: 400,
          wrapClassName: 'register-modal',
          okType: 'primary',
          onOk() {
            // 跳转到注册页面
            router.push('/user/register')
          },
          onCancel() {
            // 显示遗憾的全局提醒
            messageApi.info('😔 很遗憾失去了一次相遇的机会，期待您的使用！')
          },
        })
      } else {
        messageApi.error(response.data.message || '登录失败')
      }
    }
  } catch (error) {
    console.error('登录失败:', error)
    messageApi.error('登录失败，请检查网络连接')
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

.login-container {
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

/* 确保 Message 组件显示在登录页面之上 */
:global(.ant-message) {
  z-index: 10000 !important;
}

/* 自定义注册模态框样式 */
:global(.register-modal .ant-modal-content) {
  background: #ffefd5 !important;
  border-radius: 12px !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15) !important;
}

:global(.register-modal .ant-modal-header) {
  background: #ffefd5 !important;
  border-bottom: 1px solid #f0e6d2 !important;
  border-radius: 12px 12px 0 0 !important;
}

:global(.register-modal .ant-modal-title) {
  color: #8b4513 !important;
  font-weight: 600 !important;
  font-size: 16px !important;
}

:global(.register-modal .ant-modal-body) {
  background: #ffefd5 !important;
  color: #654321 !important;
  font-size: 14px !important;
  padding: 20px 24px !important;
}

:global(.register-modal .ant-modal-footer) {
  background: #ffefd5 !important;
  border-top: 1px solid #f0e6d2 !important;
  border-radius: 0 0 12px 12px !important;
}

:global(.register-modal .ant-btn-default) {
  background: #fff !important;
  border-color: #d9d9d9 !important;
  color: #666 !important;
}

:global(.register-modal .ant-btn-default:hover) {
  background: #f5f5f5 !important;
  border-color: #b8b8b8 !important;
  color: #333 !important;
}

:global(.register-modal .ant-btn-primary) {
  background: #8b4513 !important;
  border-color: #8b4513 !important;
  color: #fff !important;
}

:global(.register-modal .ant-btn-primary:hover) {
  background: #a0522d !important;
  border-color: #a0522d !important;
}

.login-screen {
  background: rgb(var(--card)) !important;
  position: relative;
  height: 600px;
  width: 360px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1) !important;
  border: 1px solid rgb(var(--border)) !important;
  border-radius: var(--radius) !important;
  overflow: hidden;
}

.login-screen :deep(.ant-card-body) {
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
  top: 420px;
  right: 50px;
  border-radius: 60px;
}

.login-form {
  width: 320px;
  padding: 30px;
  padding-top: 156px;
}

.login__field {
  padding: 20px 0px;
  position: relative;
}

.login__field :deep(.ant-form-item) {
  margin-bottom: 0;
}

.login__field :deep(.ant-form-item-control-input) {
  min-height: auto;
}

.password-toggle {
  position: absolute;
  top: 30px;
  right: 0;
  color: rgb(var(--muted-foreground));
  font-size: 12px;
  cursor: pointer;
  transition: 0.2s;
  user-select: none;
}

.password-toggle:hover {
  color: rgb(var(--primary));
  transform: scale(1.1);
}

.password-toggle.active {
  color: rgb(var(--primary));
}

.login__icon {
  position: absolute;
  top: 30px;
  color: rgb(var(--muted-foreground));
  font-size: 16px;
  left: 0;
}

.login__input {
  border: none !important;
  border-bottom: 2px solid rgb(var(--border)) !important;
  background: none !important;
  padding: 10px !important;
  padding-left: 24px !important;
  font-weight: 700 !important;
  width: 75% !important;
  transition: 0.2s !important;
  color: rgb(var(--foreground)) !important;
  font-size: 14px !important;
  box-shadow: none !important;
}

.login__input:active,
.login__input:focus,
.login__input:hover {
  outline: none !important;
  border-bottom-color: rgb(var(--primary)) !important;
  box-shadow: none !important;
}

.login__field :deep(.ant-input) {
  border: none !important;
  border-bottom: 2px solid rgb(var(--border)) !important;
  background: none !important;
  padding: 10px !important;
  padding-left: 24px !important;
  font-weight: 700 !important;
  width: 75% !important;
  transition: 0.2s !important;
  color: rgb(var(--foreground)) !important;
  font-size: 14px !important;
  box-shadow: none !important;
  border-radius: 0 !important;
}

.login__field :deep(.ant-input:focus) {
  border-bottom-color: rgb(var(--primary)) !important;
  box-shadow: none !important;
}

.login__field :deep(.ant-input-password) {
  border: none !important;
  border-bottom: 2px solid rgb(var(--border)) !important;
  background: none !important;
  padding: 10px !important;
  padding-left: 24px !important;
  font-weight: 700 !important;
  width: 75% !important;
  transition: 0.2s !important;
  color: rgb(var(--foreground)) !important;
  font-size: 14px !important;
  box-shadow: none !important;
  border-radius: 0 !important;
}

.login__field :deep(.ant-input-password:focus-within) {
  border-bottom-color: rgb(var(--primary)) !important;
  box-shadow: none !important;
}

.login__field :deep(.ant-input-password .ant-input) {
  border: none !important;
  border-bottom: none !important;
  background: none !important;
  padding: 0 !important;
  font-weight: 700 !important;
  color: rgb(var(--foreground)) !important;
  font-size: 14px !important;
  box-shadow: none !important;
}

.login__field :deep(.ant-input-password .ant-input:focus) {
  border: none !important;
  box-shadow: none !important;
}

.login__input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login__input::placeholder {
  color: #999;
  font-weight: 400;
}

.login__submit {
  background: #fffff0 !important;
  font-size: 14px !important;
  margin-top: 30px !important;
  padding: 16px 20px !important;
  border-radius: var(--radius) !important;
  border: 1px solid rgb(var(--border)) !important;
  text-transform: uppercase !important;
  font-weight: 700 !important;
  display: flex !important;
  align-items: center !important;
  width: 100% !important;
  color: rgb(var(--foreground)) !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1) !important;
  cursor: pointer !important;
  transition: 0.2s !important;
  height: auto !important;
}

.login__submit:active,
.login__submit:focus,
.login__submit:hover {
  background: #ffefd5 !important;
  outline: none !important;
  transform: translateY(-1px) !important;
  border-color: rgb(var(--border)) !important;
}

.login__submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.button__icon {
  font-size: 20px;
  margin-left: auto;
  color: #7875b5;
}

.register-section {
  position: absolute;
  height: 140px;
  width: 160px;
  text-align: center;
  bottom: 20px;
  right: 0px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-link {
  color: #1890ff;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  cursor: pointer;
  user-select: none;
}

.register-link:hover {
  color: #40a9ff;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-container {
    padding: 10px;
  }

  .login-screen {
    width: 100%;
    max-width: 360px;
    height: 500px;
  }

  .login-form {
    padding: 20px;
    padding-top: 120px;
  }

  .login__input {
    width: 85%;
  }
}
</style>
