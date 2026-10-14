<template>
  <div class="service-manage-bg">
    <el-card class="service-manage-card">
      <div class="service-manage-header">
        <div class="header-left">
          <h2 class="page-title">服务管理</h2>
          <p class="page-subtitle">管理系统中的所有业务服务</p>
        </div>
        <div class="header-actions">
          <el-input v-model="search" placeholder="搜索业务名称/代码/描述" style="width: 240px; margin-right: 16px;" clearable @input="fetchServices">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            新增业务
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
      <el-table :data="filteredServices" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="业务名称" />
        <el-table-column prop="code" label="业务代码" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="price" label="价格" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="success">可选</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="danger">不可选</el-tag>
            <span v-else>{{ scope.row.status }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="editService(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteService(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 新增/编辑业务弹窗 -->
      <el-dialog v-model="showAddDialog" :title="editMode ? '编辑业务' : '新增业务'">
        <el-form :model="form" label-width="80px">
          <el-form-item label="业务名称"><el-input v-model="form.name" /></el-form-item>
          <el-form-item label="业务代码"><el-input v-model="form.code" /></el-form-item>
          <el-form-item label="描述"><el-input v-model="form.description" /></el-form-item>
          <el-form-item label="价格"><el-input v-model="form.price" type="number" /></el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status" placeholder="请选择状态">
              <el-option label="可选" :value="0" />
              <el-option label="不可选" :value="1" />
            </el-select>
            <div style="font-size: 12px; color: #999; margin-top: 4px;">
              0-可选，1-不可选
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="saveService">保存</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Plus, Download, Upload } from '@element-plus/icons-vue';

const services = ref([]);
const search = ref('');
const showAddDialog = ref(false);
const editMode = ref(false);
const form = ref({
  id: '', name: '', code: '', description: '', price: '', status: ''
});

const fetchServices = async () => {
  const res = await fetch('http://localhost:8080/service/getservices');
  services.value = await res.json();
};

onMounted(fetchServices);

const filteredServices = computed(() => {
  if (!search.value) return services.value;
  return services.value.filter(s =>
    s.name?.includes(search.value) ||
    s.code?.includes(search.value) ||
    s.description?.includes(search.value)
  );
});

const editService = (row) => {
  Object.assign(form.value, row);
  editMode.value = true;
  showAddDialog.value = true;
};

const saveService = async () => {
  const url = editMode.value ? 'http://localhost:8080/service/update' : 'http://localhost:8080/service/add';
  const method = editMode.value ? 'PUT' : 'POST';
  const res = await fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(form.value),
  });
  if (await res.text() === 'success') {
    ElMessage.success(editMode.value ? '编辑成功' : '新增成功');
    showAddDialog.value = false;
    fetchServices();
    editMode.value = false;
    form.value = { id: '', name: '', code: '', description: '', price: '', status: '' };
  } else {
    ElMessage.error('操作失败');
  }
};

const deleteService = async (id) => {
  if (!confirm('确定要删除该业务吗？')) return;
  const res = await fetch(`http://localhost:8080/service/delete/${id}`, { method: 'DELETE' });
  if (await res.text() === 'success') {
    ElMessage.success('删除成功');
    fetchServices();
  } else {
    ElMessage.error('删除失败');
  }
};

const handleExport = async () => {
  try {
    // 准备导出数据
    const exportData = services.value.map(service => ({
      '业务名称': service.name,
      '业务代码': service.code,
      '描述': service.description,
      '价格': service.price,
      '状态': service.status === 0 ? '可选' : '不可选',
      '创建时间': service.createdTime
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
    link.setAttribute('download', `业务数据_${new Date().toISOString().split('T')[0]}.csv`);
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
        const service = {};
        headers.forEach((header, index) => {
          service[header] = values[index] || '';
        });
        return service;
      });

      // 转换数据格式
      const servicesToImport = importData.map(item => ({
        name: item['业务名称'] || item.name,
        code: item['业务代码'] || item.code,
        description: item['描述'] || item.description,
        price: parseFloat(item['价格'] || item.price) || 0,
        status: (item['状态'] || item.status) === '可选' ? 0 : 1
      }));

      // 批量导入
      for (const service of servicesToImport) {
        if (service.name) { // 只导入有业务名称的数据
          await fetch('http://localhost:8080/service/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(service),
          });
        }
      }

      ElMessage.success(`成功导入 ${servicesToImport.length} 条业务数据`);
      fetchServices(); // 刷新数据
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
.service-manage-bg {
  min-height: 100vh;
  background: #f8fafc;
  padding: 20px;
}

.service-manage-card {
  max-width: 1400px;
  margin: 0 auto;
  border-radius: 12px;
}

.service-manage-header {
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
  .service-manage-header {
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