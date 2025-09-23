<template>
  <div class="register-container">
    <component :is="contextHolder" />
    <a-card class="register-screen">
      <div class="screen__content">
        <a-form class="register-form" @submit.prevent="handleSubmit" autocomplete="off">
          <a-form-item class="register__field">
            <a-input
              class="register__input"
              placeholder="用户账号 / 邮箱"
              v-model:value="registerForm.userAccount"
              :disabled="loading"
              autocomplete="off"
            />
          </a-form-item>
          <a-form-item class="register__field">
            <a-input-password
              class="register__input"
              placeholder="密码"
              v-model:value="registerForm.userPassword"
              :disabled="loading"
              :visibilityToggle="true"
              autocomplete="new-password"
            />
          </a-form-item>
          <a-form-item class="register__field">
            <a-input-password
              class="register__input"
              placeholder="确认密码"
              v-model:value="registerForm.checkPassword"
              :disabled="loading"
              :visibilityToggle="true"
              autocomplete="new-password"
            />
          </a-form-item>
          <a-button
            class="register__submit"
            type="primary"
            :loading="loading"
            block
            @click="handleSubmit"
          >
            {{ loading ? '注册中...' : '立即注册' }}
          </a-button>
        </a-form>
        <div class="login-section">
          <a @click="handleLogin" class="login-link">已有账号？立即登录</a>
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
import { message } from 'ant-design-vue'
import { userRegister, userLogin } from '@/api/userController'
import { useUserLoginStore } from '@/stores/UserLogin'

const router = useRouter()
const userLoginStore = useUserLoginStore()

// 使用 Ant Design Vue 的 message API
const [messageApi, contextHolder] = message.useMessage()

// 使用全局 message 配置，确保消息居中显示
onMounted(() => {
  // Message 配置已在 main.ts 中全局配置，无需重复设置
  console.log('Message API 已就绪:', messageApi)
})

// 表单数据
const registerForm = reactive<API.UserRegisterDto>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

// 加载状态
const loading = ref(false)

// 处理登录链接点击
const handleLogin = () => {
  router.push('/user/login')
}

// 处理注册提交
const handleSubmit = async () => {
  console.log('=== 开始注册流程 ===')
  console.log('handleSubmit 被调用')
  console.log('表单数据:', registerForm)

  // 表单验证
  if (!registerForm.userAccount?.trim()) {
    console.log('用户账号为空')
    messageApi.error('请输入用户账号')
    return
  }
  if (!registerForm.userPassword?.trim()) {
    console.log('密码为空')
    messageApi.error('请输入密码')
    return
  }
  if (!registerForm.checkPassword?.trim()) {
    console.log('确认密码为空')
    messageApi.error('请输入确认密码')
    return
  }

  // 密码长度校验
  if (registerForm.userPassword.length <= 8) {
    console.log('密码长度不足:', registerForm.userPassword.length)
    messageApi.error('密码长度必须大于8位')
    return
  }

  // 密码确认校验
  if (registerForm.userPassword !== registerForm.checkPassword) {
    console.log('两次密码不一致')
    messageApi.error('两次输入的密码不一致')
    return
  }

  loading.value = true
  console.log('开始发送注册请求...')

  try {
    const response = await userRegister(registerForm)
    console.log('注册响应:', response)

    if (response && response.data && response.data.code === 0) {
      console.log('注册成功，准备显示成功消息')
      // 注册成功，显示成功消息
      messageApi.success('🎉 注册成功！正在为您自动登录...')

      // 稍微延迟后自动登录，让用户看到成功提示
      setTimeout(async () => {
        console.log('开始自动登录...')
        await autoLogin()
      }, 1000)
    } else {
      console.log('注册失败:', response?.data?.message)
      messageApi.error(response?.data?.message || '注册失败，请重试')
    }
  } catch (error: any) {
    console.error('注册请求失败:', error)
    // 检查是否是网络错误
    if (error?.code === 'ERR_NETWORK' || error?.message?.includes('Network Error')) {
      messageApi.error('网络连接失败，请检查网络或后端服务是否启动')
    } else {
      messageApi.error('注册失败：' + (error?.message || '未知错误'))
    }
  } finally {
    loading.value = false
    console.log('注册流程结束')
  }
}

// 自动登录函数
const autoLogin = async () => {
  console.log('=== 开始自动登录流程 ===')
  try {
    const loginData = {
      userAccount: registerForm.userAccount,
      userPassword: registerForm.userPassword,
    }

    console.log('发送自动登录请求:', { userAccount: loginData.userAccount })
    const loginResponse = await userLogin(loginData)
    console.log('自动登录响应:', loginResponse)

    if (
      loginResponse &&
      loginResponse.data &&
      loginResponse.data.code === 0 &&
      loginResponse.data.data
    ) {
      console.log('自动登录成功，保存用户信息')
      // 登录成功，保存登录态到全局状态
      userLoginStore.setLoginUser(loginResponse.data.data)
      messageApi.success('✨ 自动登录成功！欢迎来到代码魔方！')

      console.log('准备跳转到首页...')
      // 延迟跳转到首页，让用户看到完整的成功流程
      setTimeout(() => {
        console.log('跳转到首页')
        router.push('/')
      }, 1500)
    } else {
      console.log('自动登录失败，响应数据:', loginResponse?.data)
      // 自动登录失败，跳转到登录页面
      messageApi.warning('注册成功，但自动登录失败，请手动登录')
      setTimeout(() => {
        console.log('跳转到登录页面')
        router.push('/user/login')
      }, 2000)
    }
  } catch (error: any) {
    console.error('自动登录请求失败:', error)
    // 自动登录失败，跳转到登录页面
    if (error?.code === 'ERR_NETWORK' || error?.message?.includes('Network Error')) {
      messageApi.warning('注册成功，但网络连接失败，请手动登录')
    } else {
      messageApi.warning('注册成功，但自动登录失败，请手动登录')
    }
    setTimeout(() => {
      console.log('跳转到登录页面')
      router.push('/user/login')
    }, 2000)
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

.register-container {
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

/* 确保 Message 组件显示在注册页面之上 */
:global(.ant-message) {
  z-index: 10000 !important;
}

.register-screen {
  background: rgb(var(--card)) !important;
  position: relative;
  height: 650px;
  width: 360px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1) !important;
  border: 1px solid rgb(var(--border)) !important;
  border-radius: var(--radius) !important;
  overflow: hidden;
}

.register-screen :deep(.ant-card-body) {
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

.register-form {
  width: 320px;
  padding: 30px;
  padding-top: 120px;
}

.register__field {
  padding: 20px 0px;
  position: relative;
}

.register__field :deep(.ant-form-item) {
  margin-bottom: 0;
}

.register__field :deep(.ant-form-item-control-input) {
  min-height: auto;
}

.register__input {
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

.register__input:active,
.register__input:focus,
.register__input:hover {
  outline: none !important;
  border-bottom-color: rgb(var(--primary)) !important;
  box-shadow: none !important;
}

.register__field :deep(.ant-input) {
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

.register__field :deep(.ant-input:focus) {
  border-bottom-color: rgb(var(--primary)) !important;
  box-shadow: none !important;
}

.register__field :deep(.ant-input-password) {
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

.register__field :deep(.ant-input-password:focus-within) {
  border-bottom-color: rgb(var(--primary)) !important;
  box-shadow: none !important;
}

.register__field :deep(.ant-input-password .ant-input) {
  border: none !important;
  border-bottom: none !important;
  background: none !important;
  padding: 0 !important;
  font-weight: 700 !important;
  color: rgb(var(--foreground)) !important;
  font-size: 14px !important;
  box-shadow: none !important;
}

.register__field :deep(.ant-input-password .ant-input:focus) {
  border: none !important;
  box-shadow: none !important;
}

.register__input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.register__input::placeholder {
  color: #999;
  font-weight: 400;
}

.register__submit {
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

.register__submit:active,
.register__submit:focus,
.register__submit:hover {
  background: #ffefd5 !important;
  outline: none !important;
  transform: translateY(-1px) !important;
  border-color: rgb(var(--border)) !important;
}

.register__submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.login-section {
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

.login-link {
  color: #1890ff;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  cursor: pointer;
  user-select: none;
}

.login-link:hover {
  color: #40a9ff;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .register-container {
    padding: 10px;
  }

  .register-screen {
    width: 100%;
    max-width: 360px;
    height: 600px;
  }

  .register-form {
    padding: 20px;
    padding-top: 100px;
  }

  .register__input {
    width: 85%;
  }
}
</style>
