<template>
  <div class="customer-manage-bg">
    <el-card class="customer-manage-card">
      <div class="customer-manage-header">
        <div class="header-left">
          <h2 class="page-title">客户管理</h2>
          <p class="page-subtitle">管理系统中的所有客户信息</p>
        </div>
        <div class="header-actions">
          <el-input v-model="search" placeholder="搜索客户名称/联系人/电话" style="width: 240px; margin-right: 16px;" clearable @input="fetchCustomers">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            新增客户
          </el-button>
          <el-button @click="handleExport" type="success">
            <el-icon><Download /></el-icon>
            导出
          </el-button>
          <el-upload :show-file-list="false" :before-upload="handleImport" accept=".csv,.xlsx">
            <el-button type="warning">
              <el-icon><Upload /></el-icon>
              导入
            </el-button>
          </el-upload>
        </div>
      </div>
      <el-table :data="filteredCustomers" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="name" label="客户名称" />
        <el-table-column prop="contactPerson" label="联系人" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="industry" label="行业" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="editCustomer(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteCustomer(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 新增/编辑客户弹窗 -->
      <el-dialog v-model="showAddDialog" :title="editMode ? '编辑客户' : '新增客户'">
        <el-form :model="form" label-width="80px">
          <el-form-item label="客户名称"><el-input v-model="form.name" /></el-form-item>
          <el-form-item label="联系人"><el-input v-model="form.contactPerson" /></el-form-item>
          <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
          <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
          <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
          <el-form-item label="行业"><el-input v-model="form.industry" /></el-form-item>
          <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="saveCustomer">保存</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Plus, Download, Upload } from '@element-plus/icons-vue';

const customers = ref([]);
const search = ref('');
const showAddDialog = ref(false);
const editMode = ref(false);
const form = ref({
  id: '', name: '', contactPerson: '', phone: '', email: '', address: '', industry: '', remark: ''
});

const fetchCustomers = async () => {
  const res = await fetch('http://localhost:8080/customer/getcustomers');
  customers.value = await res.json();
};

onMounted(fetchCustomers);

const filteredCustomers = computed(() => {
  if (!search.value) return customers.value;
  return customers.value.filter(c =>
    c.name?.includes(search.value) ||
    c.contactPerson?.includes(search.value) ||
    c.phone?.includes(search.value)
  );
});

const editCustomer = (row) => {
  Object.assign(form.value, row);
  editMode.value = true;
  showAddDialog.value = true;
};

const saveCustomer = async () => {
  const url = editMode.value ? 'http://localhost:8080/customer/update' : 'http://localhost:8080/customer/add';
  const method = editMode.value ? 'PUT' : 'POST';
  const res = await fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(form.value),
  });
  if (await res.text() === 'success') {
    ElMessage.success(editMode.value ? '编辑成功' : '新增成功');
    showAddDialog.value = false;
    fetchCustomers();
    editMode.value = false;
    form.value = { id: '', name: '', contactPerson: '', phone: '', email: '', address: '', industry: '', remark: '' };
  } else {
    ElMessage.error('操作失败');
  }
};

const deleteCustomer = async (id) => {
  if (!confirm('确定要删除该客户吗？删除后将同时删除该客户的所有工单！')) return;
  
  try {
    const res = await fetch(`http://localhost:8080/customer/delete/${id}`, { method: 'DELETE' });
    const result = await res.text();
    
    if (result === 'success') {
      ElMessage.success('删除成功');
      fetchCustomers();
    } else {
      // 显示具体的错误信息
      const errorMsg = result.startsWith('error: ') ? result.substring(7) : '删除失败';
      ElMessage.error(errorMsg);
    }
  } catch (error) {
    console.error('删除客户时发生错误:', error);
    ElMessage.error('网络错误，删除失败');
  }
};

const handleExport = async () => {
  try {
    // 准备导出数据
    const exportData = customers.value.map(customer => ({
      '客户名称': customer.name,
      '联系人': customer.contactPerson,
      '电话': customer.phone,
      '邮箱': customer.email,
      '地址': customer.address,
      '行业': customer.industry,
      '备注': customer.remark
    }));

    // 创建CSV内容
    const headers = Object.keys(exportData[0]);
    const csvContent = [
      headers.join(','),
      ...exportData.map(row => headers.map(header => {
        const value = row[header] || '';
        // 如果值包含逗号、引号或换行符，需要用引号包围并转义内部引号
        if (value.includes(',') || value.includes('"') || value.includes('\n')) {
          return `"${value.replace(/"/g, '""')}"`;
        }
        return value;
      }).join(','))
    ].join('\n');

    // 创建下载链接，添加BOM确保Excel正确识别UTF-8编码
    const BOM = '\uFEFF';
    const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', `客户数据_${new Date().toISOString().split('T')[0]}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    ElMessage.success('导出成功');
  } catch (error) {
    console.error('导出失败:', error);
    ElMessage.error('导出失败');
  }
};

const handleImport = async (file) => {
  try {
    const reader = new FileReader();
    reader.onload = async (e) => {
      const text = e.target.result;
      const lines = text.split('\n');
      const headers = lines[0].split(',').map(h => h.replace(/"/g, '').trim());
      
      const importData = lines.slice(1).filter(line => line.trim()).map(line => {
        const values = line.split(',').map(v => v.replace(/"/g, '').trim());
        const customer = {};
        headers.forEach((header, index) => {
          customer[header] = values[index] || '';
        });
        return customer;
      });

      // 转换数据格式
      const customersToImport = importData.map(item => ({
        name: item['客户名称'] || item.name,
        contactPerson: item['联系人'] || item.contactPerson,
        phone: item['电话'] || item.phone,
        email: item['邮箱'] || item.email,
        address: item['地址'] || item.address,
        industry: item['行业'] || item.industry,
        remark: item['备注'] || item.remark
      }));

      // 批量导入
      for (const customer of customersToImport) {
        if (customer.name) { // 只导入有客户名称的数据
          await fetch('http://localhost:8080/customer/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(customer),
          });
        }
      }

      ElMessage.success(`成功导入 ${customersToImport.length} 条客户数据`);
      fetchCustomers(); // 刷新数据
    };
    reader.readAsText(file, 'UTF-8');
  } catch (error) {
    console.error('导入失败:', error);
    ElMessage.error('导入失败');
  }
  return false; // 阻止默认上传行为
};
</script>

<style scoped>
.customer-manage-bg {
  min-height: 100vh;
  background: #f8fafc;
  padding: 20px;
}

.customer-manage-card {
  max-width: 1400px;
  margin: 0 auto;
  border-radius: 12px;
}

.customer-manage-header {
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

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .customer-manage-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .header-actions {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .page-title {
    font-size: 20px;
  }
}
</style> 