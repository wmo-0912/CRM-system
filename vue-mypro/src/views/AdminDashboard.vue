<template>
  <div class="dashboard">
    <aside class="sidebar">
      <!-- 系统标题和Logo -->
      <div class="sidebar-header">
        <img src="@/assets/logo.png" alt="CRM Logo" class="sidebar-logo" />
        <h2 class="sidebar-title">CRM客户关系管理系统</h2>
        <p class="sidebar-subtitle">管理员控制台</p>
      </div>
      
      <ul class="sidebar-menu">
        <li :class="{active: $route.path.includes('/admin/customer/manage')}" @click="goTo('customer/manage')">
          <el-icon><User /></el-icon>
          <span>客户管理</span>
        </li>
        <li :class="{active: $route.path.includes('/admin/service/manage')}" @click="goTo('service/manage')">
          <el-icon><Service /></el-icon>
          <span>服务管理</span>
        </li>
        <li :class="{active: $route.path.includes('/admin/workorder/manage')}" @click="goTo('workorder/manage')">
          <el-icon><Document /></el-icon>
          <span>工单管理</span>
        </li>
        <li :class="{active: $route.path.includes('/admin/user/manage')}" @click="goTo('user/manage')">
          <el-icon><Setting /></el-icon>
          <span>用户管理</span>
        </li>
        <li class="logout-item" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
        </li>
      </ul>
    </aside>
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router';
import { onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { User, Service, Document, Setting, SwitchButton } from '@element-plus/icons-vue';
const router = useRouter();
const route = useRoute();

onMounted(() => {
  if (route.path === '/admin') {
    router.replace('/admin/customer/manage');
  }
});

function goTo(path) {
  router.push(`/admin/${path}`);
}

const handleLogout = () => {
  // 清理用户数据
  localStorage.removeItem('customerId');
  localStorage.removeItem('userId');
  localStorage.removeItem('userRole');
  
  ElMessage.success('已退出登录');
  router.push('/login');
};
</script>

<style scoped>
.dashboard {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 250px;
  background: linear-gradient(180deg, #2d3a4b 0%, #1a2233 100%);
  color: #fff;
  box-shadow: 2px 0 8px rgba(0,0,0,0.1);
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  background: rgba(255,255,255,0.05);
}

.sidebar-logo {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
}

.sidebar-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 4px 0;
  color: #fff;
  line-height: 1.2;
}

.sidebar-subtitle {
  font-size: 12px;
  margin: 0;
  color: rgba(255,255,255,0.7);
  font-weight: 500;
}

.sidebar-menu {
  list-style: none;
  padding: 0;
  margin: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.sidebar-menu li {
  padding: 16px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 12px;
  border-left: 3px solid transparent;
}

.sidebar-menu li:hover {
  background: rgba(255,255,255,0.1);
  border-left-color: #409eff;
}

.sidebar-menu li.active {
  background: rgba(64, 158, 255, 0.2);
  border-left-color: #409eff;
  color: #409eff;
}

.sidebar-menu li.logout-item {
  margin-top: auto;
  border-top: 1px solid rgba(255,255,255,0.1);
  color: #ff6b6b;
}

.sidebar-menu li.logout-item:hover {
  background: rgba(255, 107, 107, 0.1);
  border-left-color: #ff6b6b;
  color: #ff6b6b;
}

.sidebar-menu li .el-icon {
  font-size: 18px;
}

.sidebar-menu li span {
  font-size: 14px;
  font-weight: 500;
}

.content {
  flex: 1;
  padding: 20px;
  background: #f5f6fa;
  overflow: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 200px;
  }
  
  .sidebar-title {
    font-size: 14px;
  }
  
  .sidebar-logo {
    width: 40px;
    height: 40px;
  }
}
</style> 