<template>
  <div class="login-bg">
    <div class="login-wrapper">
      <!-- 系统标题和Logo -->
      <div class="system-header">
        <img src="@/assets/logo.png" alt="CRM Logo" class="logo" />
        <h1 class="system-title">CRM客户关系管理系统</h1>
        <p class="system-subtitle">Customer Relationship Management System</p>
      </div>
      
      <el-card class="login-card">
        <div class="login-title">
          <el-icon><User /></el-icon>
          <span>用户登录</span>
        </div>
        <el-form :model="loginForm" ref="loginFormRef" label-width="70px" class="login-form">
          <el-form-item label="用户名">
            <el-input v-model="loginForm.username" prefix-icon="User" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="loginForm.password" prefix-icon="Lock" type="password" placeholder="请输入密码" show-password clearable />
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="loginForm.status" placeholder="请选择角色" style="width: 100%">
              <el-option label="客户" :value="1" />
              <el-option label="管理员" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" style="width: 100%" @click="handleLogin">登录</el-button>
          </el-form-item>
          <el-form-item>
            <span class="to-register">还没有账号？<el-link type="primary" @click="$router.push('/register')">去注册</el-link></span>
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
import { User, Lock } from '@element-plus/icons-vue';

const router = useRouter();
const loginFormRef = ref();
const loginForm = ref({
  username: '',
  password: '',
  status: 1, // 默认客户
});

const handleLogin = async () => {
  try {
    // 清理之前的用户数据
    localStorage.removeItem('customerId');
    localStorage.removeItem('userId');
    localStorage.removeItem('userRole');
    
    const res = await fetch('http://localhost:8080/user/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(loginForm.value),
    });
    const data = await res.json();
    if (data.code === 0) {
      ElMessage.success('登录成功');
      
      // 设置用户角色
      localStorage.setItem('userRole', loginForm.value.status);
      
      // 如果是客户，设置customerId（客户ID和用户ID一致）
      if (loginForm.value.status === 1 && data.data) {
        localStorage.setItem('customerId', data.data.id);
      }
      
      // 根据角色跳转到不同页面
      if (loginForm.value.status === 0) {
        router.push('/admin/customer/manage');
      } else {
        router.push('/customer');
      }
    } else {
      ElMessage.error(data.msg || '登录失败');
    }
  } catch (e) {
    ElMessage.error('网络错误');
  }
};
</script>

<style scoped>
.login-bg {
  min-height: 100vh;
  background: linear-gradient(135deg, #e0e7ff 0%, #f0fdfa 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-wrapper {
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

.login-card {
  padding: 40px 30px 20px 30px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.12);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.login-title {
  display: flex;
  align-items: center;
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 30px;
  color: #333;
  justify-content: center;
  gap: 8px;
}

.login-form {
  margin-top: 10px;
}

.to-register {
  font-size: 14px;
  color: #888;
  text-align: center;
  width: 100%;
  display: block;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-wrapper {
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