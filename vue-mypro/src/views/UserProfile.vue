<template>
  <div class="user-profile-bg">
    <div class="user-profile-wrapper">
      <!-- 系统标题和Logo -->
      <div class="system-header">
        <img src="@/assets/logo.png" alt="CRM Logo" class="logo" />
        <h1 class="system-title">CRM客户关系管理系统</h1>
        <p class="system-subtitle">Customer Relationship Management System</p>
      </div>
      
      <el-card class="user-profile-card">
        <div class="user-profile-title">
          <el-icon><User /></el-icon>
          <span>完善用户信息</span>
        </div>
        <el-form :model="form" ref="formRef" label-width="90px" class="user-profile-form">
          <el-form-item label="真实姓名">
            <el-input v-model="form.realName" placeholder="请输入真实姓名" clearable />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="form.phone" placeholder="请输入联系电话" clearable />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="form.email" placeholder="请输入邮箱" clearable />
          </el-form-item>
          <!-- 只有管理员才显示部门字段 -->
          <el-form-item v-if="userRole === 0" label="部门">
            <el-input v-model="form.department" placeholder="请输入部门" clearable />
          </el-form-item>
          <!-- 只有管理员才显示职位字段 -->
          <el-form-item v-if="userRole === 0" label="职位">
            <el-input v-model="form.position" placeholder="请输入职位" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" style="width: 100%" @click="handleSubmit">保存并去登录</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { User } from '@element-plus/icons-vue';

const router = useRouter();
const formRef = ref();
const userRole = ref(1); // 默认客户
const form = ref({
  id: '',
  realName: '',
  phone: '',
  email: '',
  department: '',
  position: ''
});

onMounted(async () => {
  // 获取用户角色（从localStorage）
  const role = localStorage.getItem('userRole'); // 存储用户角色
  if (role) {
    userRole.value = parseInt(role);
  }
  
  // 根据角色获取用户ID
  if (userRole.value === 1) {
    // 客户：从customerId获取用户ID
    const customerId = localStorage.getItem('customerId');
    if (customerId) {
      form.value.id = customerId;
    }
  } else {
    // 管理员：需要从注册响应中获取用户ID，这里暂时使用一个临时方案
    // 在实际应用中，应该在后端返回用户ID时存储到localStorage
    const userId = localStorage.getItem('userId');
    if (userId) {
      form.value.id = userId;
    }
  }
});

const handleSubmit = async () => {
  try {
    // 更新用户信息
    const res = await fetch('http://localhost:8080/user/update', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form.value),
    });
    const data = await res.json();
    if (data.code === 0) {
      ElMessage.success('信息保存成功，请登录');
      // 跳转到登录页面
      router.push('/login');
    } else {
      ElMessage.error(data.msg || '信息保存失败');
    }
  } catch (e) {
    ElMessage.error('网络错误');
  }
};
</script>

<style scoped>
.user-profile-bg {
  min-height: 100vh;
  background: linear-gradient(135deg, #f0fdfa 0%, #e0e7ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.user-profile-wrapper {
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

.user-profile-card {
  padding: 40px 30px 20px 30px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.12);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.user-profile-title {
  display: flex;
  align-items: center;
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 30px;
  color: #333;
  justify-content: center;
  gap: 8px;
}

.user-profile-form {
  margin-top: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-profile-wrapper {
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