<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-icon">
        <img src="/unnamed_13.png" alt="403" class="error-image" />
      </div>
      <div class="error-text">
        <h1>403</h1>
        <h2>权限不足</h2>
        <p>很抱歉，您没有权限访问该页面</p>
        <div class="error-actions">
          <a-button type="primary" @click="goHome">
            <HomeOutlined />
            返回首页
          </a-button>
          <a-button @click="goBack" style="margin-left: 16px">
            <ArrowLeftOutlined />
            返回上页
          </a-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { HomeOutlined, ArrowLeftOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.error-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.error-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  background: white;
  border-radius: 16px;
  padding: 60px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
}

.error-icon {
  margin-bottom: 32px;
}

.error-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 50%;
}

.error-text h1 {
  font-size: 72px;
  font-weight: bold;
  color: #ff4d4f;
  margin: 0 0 16px 0;
  line-height: 1;
}

.error-text h2 {
  font-size: 28px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 16px 0;
}

.error-text p {
  font-size: 16px;
  color: #8c8c8c;
  margin: 0 0 32px 0;
  line-height: 1.6;
}

.error-actions {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 16px;
}

@media (max-width: 768px) {
  .error-content {
    padding: 40px 20px;
  }
  
  .error-text h1 {
    font-size: 56px;
  }
  
  .error-text h2 {
    font-size: 24px;
  }
  
  .error-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .error-actions .ant-btn {
    width: 200px;
  }
}
</style>
