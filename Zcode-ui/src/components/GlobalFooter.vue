<template>
  <div class="global-footer">
    <div class="footer-content">
      <p class="copyright">
        <a
          href="https://github.com/WuJavaLoser/ai-code"
          target="_blank"
          rel="noopener noreferrer"
          class="author-link"
        >
          代码魔方
        </a>
      </p>
      <div class="footer-links">
        <a
          href="https://github.com/WuJavaLoser/ai-code"
          target="_blank"
          rel="noopener noreferrer"
          class="footer-link"
        >
          代码魔方
        </a>
        <span class="divider">|</span>
        <a href="https://github.com" target="_blank" rel="noopener noreferrer" class="footer-link">
          GitHub
        </a>
        <span class="divider">|</span>
        <a href="#" class="footer-link" @click.prevent="showContact"> 联系我们 </a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { message } from 'ant-design-vue'

// 显示联系方式
const showContact = () => {
  message.info('联系方式：请访问代码魔方 GitHub 仓库获取更多信息')
}
</script>

<style scoped>
.global-footer {
  width: 100%;
  background: transparent;
  border-top: none;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;
  text-align: center;
}

.copyright {
  margin: 0 0 12px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

.author-link {
  color: #000;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.author-link:hover {
  color: #1890ff;
  text-decoration: none;
}

.footer-links {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.footer-link {
  color: #666;
  text-decoration: none;
  font-size: 14px;
  font-weight: 400;
  transition: color 0.3s ease;
}

.footer-link:hover {
  color: #000;
}

.divider {
  color: #d9d9d9;
  font-size: 14px;
  margin: 0 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .footer-content {
    padding: 12px 16px;
  }

  .copyright {
    font-size: 13px;
    margin-bottom: 6px;
  }

  .footer-links {
    gap: 6px;
  }

  .footer-link {
    font-size: 12px;
  }
}

@media (max-width: 576px) {
  .footer-content {
    padding: 10px 12px;
  }

  .copyright {
    font-size: 12px;
  }

  .footer-links {
    flex-direction: column;
    gap: 4px;
  }

  .divider {
    display: none;
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .global-footer {
    background: #141414;
    border-top-color: #303030;
  }

  .copyright {
    color: #a6a6a6;
  }

  .footer-link {
    color: #a6a6a6;
  }

  .footer-link:hover {
    color: #1890ff;
  }

  .divider {
    color: #595959;
  }
}
</style>
