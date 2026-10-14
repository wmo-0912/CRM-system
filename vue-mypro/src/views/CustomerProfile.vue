<template>
  <div class="customer-profile-bg">
    <div class="customer-profile-wrapper">
      <el-card class="customer-profile-card">
        <div class="customer-profile-title">
          <el-icon><User /></el-icon>
          <span>{{ isRegisterFlow ? '完善客户信息' : '客户信息修改' }}</span>
        </div>
        <el-form :model="form" ref="formRef" label-width="90px" class="customer-profile-form">
          <el-form-item label="客户名称">
            <el-input v-model="form.name" placeholder="请输入客户名称" clearable />
          </el-form-item>
          <el-form-item label="联系人">
            <el-input v-model="form.contactPerson" placeholder="请输入联系人" clearable />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="form.phone" placeholder="请输入联系电话" clearable />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="form.email" placeholder="请输入邮箱" clearable />
          </el-form-item>
          <el-form-item label="地址">
            <el-input v-model="form.address" placeholder="请输入地址" clearable />
          </el-form-item>
          <el-form-item label="行业">
            <el-input v-model="form.industry" placeholder="请输入行业" clearable />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" style="width: 100%" @click="handleSubmit">
              {{ isRegisterFlow ? '保存并去登录' : '保存修改' }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter, useRoute } from 'vue-router';
import { User } from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();
const formRef = ref();
const form = ref({
  id: '',
  name: '',
  contactPerson: '',
  phone: '',
  email: '',
  address: '',
  industry: '',
  remark: ''
});

// 判断是否为注册流程（通过路由参数或localStorage判断）
const isRegisterFlow = ref(false);

onMounted(async () => {
  // 检查是否为注册流程
  const customerId = localStorage.getItem('customerId');
  if (customerId && !route.query.from) {
    // 有customerId但没有from参数，说明是注册流程
    isRegisterFlow.value = true;
    form.value.id = customerId;
  } else {
    // 从客户仪表盘进入，需要获取现有信息
    isRegisterFlow.value = false;
    if (customerId) {
      // 获取客户信息
      const res = await fetch(`http://localhost:8080/customer/getcustomers`);
      const data = await res.json();
      if (Array.isArray(data)) {
        const customer = data.find(c => c.id == customerId);
        if (customer) Object.assign(form.value, customer);
      }
    }
  }
});

const handleSubmit = async () => {
  try {
    const res = await fetch('http://localhost:8080/customer/update', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form.value),
    });
    const data = await res.text();
    if (data === 'success') {
      if (isRegisterFlow.value) {
        ElMessage.success('信息保存成功，请登录');
        // 注册流程：跳转到登录页面
        router.push('/login');
      } else {
        ElMessage.success('信息修改成功');
        // 非注册流程：跳转回客户仪表盘
        router.push('/customer');
      }
    } else {
      ElMessage.error('信息保存失败');
    }
  } catch (e) {
    ElMessage.error('网络错误');
  }
};
</script>

<style scoped>
.customer-profile-bg {
  min-height: 100vh;
  background: linear-gradient(135deg, #f0fdfa 0%, #e0e7ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.customer-profile-wrapper {
  width: 480px;
}
.customer-profile-card {
  padding: 40px 30px 20px 30px;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
}
.customer-profile-title {
  display: flex;
  align-items: center;
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 30px;
  color: #333;
  justify-content: center;
  gap: 8px;
}
.customer-profile-form {
  margin-top: 10px;
}
</style> 