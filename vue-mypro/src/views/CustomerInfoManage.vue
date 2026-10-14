<template>
  <div class="customer-info-manage-bg">
    <el-card class="customer-info-manage-card">
      <div class="customer-info-manage-header">
        <div class="header-left">
          <h2 class="page-title">个人信息管理</h2>
          <p class="page-subtitle">查看和修改您的个人信息</p>
        </div>
        <el-button type="primary" @click="editCustomerInfo">
          <el-icon><Edit /></el-icon>
          修改信息
        </el-button>
      </div>
      <el-table :data="customerInfo" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="客户名称" />
        <el-table-column prop="contactPerson" label="联系人" />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="industry" label="行业" />
        <el-table-column prop="status" label="状态" />
        <el-table-column prop="createdTime" label="创建时间" />
      </el-table>
      <!-- 修改信息弹窗 -->
      <el-dialog v-model="showEditDialog" title="修改个人信息">
        <el-form :model="form" label-width="100px">
          <el-form-item label="客户名称"><el-input v-model="form.name" /></el-form-item>
          <el-form-item label="联系人"><el-input v-model="form.contactPerson" /></el-form-item>
          <el-form-item label="联系电话"><el-input v-model="form.phone" /></el-form-item>
          <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
          <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
          <el-form-item label="行业"><el-input v-model="form.industry" /></el-form-item>
          <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="saveCustomerInfo">保存</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Edit } from '@element-plus/icons-vue';

const customerInfo = ref([]);
const showEditDialog = ref(false);
const form = ref({
  id: '', name: '', contactPerson: '', phone: '', email: '', 
  address: '', industry: '', remark: ''
});

const fetchCustomerInfo = async () => {
  try {
    const customerId = localStorage.getItem('customerId');
    console.log('获取客户信息，客户ID:', customerId);
    
    if (customerId) {
      const res = await fetch('http://localhost:8080/customer/getcustomers');
      const data = await res.json();
      console.log('所有客户数据:', data);
      
      if (Array.isArray(data)) {
        const customer = data.find(c => c.id == customerId);
        console.log('找到的客户:', customer);
        if (customer) {
          customerInfo.value = [customer];
        }
      }
    } else {
      console.log('没有找到客户ID');
    }
  } catch (e) {
    console.error('获取客户信息错误:', e);
    ElMessage.error('获取客户信息失败');
  }
};

onMounted(fetchCustomerInfo);

const editCustomerInfo = () => {
  if (customerInfo.value.length > 0) {
    Object.assign(form.value, customerInfo.value[0]);
    showEditDialog.value = true;
  }
};

const saveCustomerInfo = async () => {
  try {
    const res = await fetch('http://localhost:8080/customer/update', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form.value),
    });
    const data = await res.text();
    if (data === 'success') {
      ElMessage.success('信息修改成功');
      showEditDialog.value = false;
      fetchCustomerInfo(); // 刷新数据，不跳转页面
    } else {
      ElMessage.error('信息修改失败');
    }
  } catch (e) {
    ElMessage.error('网络错误');
  }
};
</script>

<style scoped>
.customer-info-manage-bg {
  min-height: 100vh;
  background: #f8fafc;
  padding: 20px;
}

.customer-info-manage-card {
  max-width: 1200px;
  margin: 0 auto;
  border-radius: 12px;
}

.customer-info-manage-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e5e7eb;
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .customer-info-manage-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .page-title {
    font-size: 20px;
  }
}
</style> 