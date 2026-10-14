<template>
  <div style="padding: 40px;">
    <el-button type="primary" @click="testApi">测试后端连通性</el-button>
    <div v-if="result" style="margin-top: 20px;">
      <el-alert :title="result" type="success" v-if="success" />
      <el-alert :title="result" type="error" v-else />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const result = ref('')
const success = ref(false)

const testApi = async () => {
  try {
    const res = await fetch('http://localhost:8080/user/userlist')
    if (!res.ok) {
      result.value = 'HTTP状态码: ' + res.status
      success.value = false
      return
    }
    const data = await res.json()
    result.value = '成功获取用户数量: ' + (Array.isArray(data) ? data.length : JSON.stringify(data))
    success.value = true
  } catch (e) {
    result.value = '请求失败: ' + e
    success.value = false
  }
}
</script>
