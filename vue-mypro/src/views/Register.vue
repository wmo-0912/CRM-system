<template>
  <div class="register-bg">
    <div class="register-wrapper">
      <!-- 系统标题和Logo -->
      <div class="system-header">
        <img src="@/assets/logo.png" alt="CRM Logo" class="logo" />
        <h1 class="system-title">CRM客户关系管理系统</h1>
        <p class="system-subtitle">Customer Relationship Management System</p>
      </div>
      
      <el-card class="register-card">
        <div class="register-title">
          <el-icon><UserFilled /></el-icon>
          <span>用户注册</span>
        </div>
        <el-form :model="registerForm" ref="registerFormRef" label-width="80px" class="register-form">
          <el-form-item label="用户名">
            <el-input v-model="registerForm.username" prefix-icon="User" placeholder="请输入用户名" autocomplete="off" clearable />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="registerForm.password" prefix-icon="Lock" type="password" show-password placeholder="请输入密码" autocomplete="off" clearable />
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="registerForm.status" placeholder="请选择角色" style="width: 100%">
              <el-option label="客户" :value="1" />
              <el-option label="管理员" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" style="width: 100%" @click="handleRegister">注册</el-button>
          </el-form-item>
          <el-form-item>
            <span class="to-login">已有账号？<el-link type="primary" @click="$router.push('/login')">去登录</el-link></span>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { UserFilled, User, Lock } from '@element-plus/icons-vue';

const router = useRouter();
const registerFormRef = ref();
const registerForm = ref({
  username: '',
  password: '',
  status: 1, // 默认客户
});

const handleRegister = async () => {
  try {
    const res = await fetch('http://localhost:8080/user/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(registerForm.value),
    });
    const data = await res.json();
    if (data.code === 0) {
      ElMessage.success(data.msg || '注册成功');
      localStorage.setItem('userRole', registerForm.value.status);
      
      // 根据角色决定后续流程
      if (registerForm.value.status === 1) {
        // 客户注册，需要完善信息
        if (data.data) localStorage.setItem('customerId', data.data);
        router.push('/user/profile');
      } else {
        // 管理员注册，存储用户ID并跳转到完善信息页面
        if (data.data) localStorage.setItem('userId', data.data);
        router.push('/user/profile');
      }
    } else {
      ElMessage.error(data.msg || '注册失败');
    }
  } catch (e) {
    ElMessage.error('网络错误');
  }
};
</script>

<style scoped>
.register-bg {
  min-height: 100vh;
  background: linear-gradient(135deg, #e0e7ff 0%, #f0fdfa 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.register-wrapper {
  width: 480px;
  text-align: center;
}

.system-header {
  margin-bottom: 30px;
  text-align: center;
}

.logo {
  width: 80px;
  height: 80px;
  margin-bottom: 16px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.system-title {
  font-size: 28px;
  font-weight: bold;
  color: #2c3e50;
  margin: 0 0 8px 0;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.system-subtitle {
  font-size: 14px;
  color: #7f8c8d;
  margin: 0;
  font-weight: 500;
  letter-spacing: 1px;
}

.register-card {
  padding: 40px 30px 20px 30px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.12);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.register-title {
  display: flex;
  align-items: center;
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 30px;
  color: #333;
  justify-content: center;
  gap: 8px;
}

.register-form {
  margin-top: 10px;
}

.to-login {
  font-size: 14px;
  color: #888;
  text-align: center;
  width: 100%;
  display: block;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-wrapper {
    width: 100%;
    max-width: 400px;
  }
  
  .system-title {
    font-size: 24px;
  }
  
  .logo {
    width: 60px;
    height: 60px;
  }
}
</style> 