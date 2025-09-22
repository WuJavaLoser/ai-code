import { globalIgnores } from 'eslint/config'
import { defineConfigWithVueTs, vueTsConfigs } from '@vue/eslint-config-typescript'
import pluginVue from 'eslint-plugin-vue'
import skipFormatting from '@vue/eslint-config-prettier/skip-formatting'

// To allow more languages other than `ts` in `.vue` files, uncomment the following lines:
// import { configureVueProject } from '@vue/eslint-config-typescript'
// configureVueProject({ scriptLangs: ['ts', 'tsx'] })
// More info at https://github.com/vuejs/eslint-config-typescript/#advanced-setup

export default defineConfigWithVueTs(
  {
    name: 'app/files-to-lint',
    files: ['**/*.{ts,mts,tsx,vue}'],
  },

  globalIgnores(['**/dist/**', '**/dist-ssr/**', '**/coverage/**']),

  pluginVue.configs['flat/essential'],
  vueTsConfigs.recommended,
  skipFormatting,

  // 禁用所有 ESLint 规则
  {
    name: 'app/disable-all-rules',
    rules: {
      // 禁用所有 TypeScript ESLint 规则
      '@typescript-eslint/ban-ts-comment': 'off',
      '@typescript-eslint/no-unused-vars': 'off',
      '@typescript-eslint/no-explicit-any': 'off',
      '@typescript-eslint/no-empty-function': 'off',
      '@typescript-eslint/no-non-null-assertion': 'off',

      // 禁用所有 Vue ESLint 规则
      'vue/multi-word-component-names': 'off',
      'vue/no-unused-vars': 'off',
      'vue/no-unused-components': 'off',

      // 禁用所有基础 ESLint 规则
      'no-unused-vars': 'off',
      'no-console': 'off',
      'no-debugger': 'off',
      'no-empty': 'off',
      'prefer-const': 'off',

      // 禁用未使用的 eslint-disable 指令警告
      'eslint-comments/no-unused-disable': 'off',
    },
  },
)
