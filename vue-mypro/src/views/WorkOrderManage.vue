<template>
  <div class="workorder-manage-bg">
    <el-card class="workorder-manage-card">
      <div class="workorder-manage-header">
        <div class="header-left">
          <h2 class="page-title">工单管理</h2>
          <p class="page-subtitle">管理系统中的所有工单和流程</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            新建工单
          </el-button>
          <el-button @click="handleExport" type="success">
            <el-icon><Download /></el-icon>
            导出工单
          </el-button>
        </div>
      </div>
      <div class="search-filters">
        <el-input v-model="search" placeholder="搜索工单号/描述/客户ID" style="width: 200px; margin-right: 16px;" clearable @input="fetchWorkOrders">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 120px; margin-right: 16px;" clearable @change="applyFilters">
          <el-option label="未分配" :value="0" />
          <el-option label="已接单" :value="1" />
          <el-option label="处理中" :value="2" />
          <el-option label="已结束" :value="3" />
          <el-option label="已退单" :value="4" />
        </el-select>
        <el-select v-model="priorityFilter" placeholder="优先级筛选" style="width: 120px; margin-right: 16px;" clearable @change="applyFilters">
          <el-option label="低" :value="1" />
          <el-option label="中" :value="2" />
          <el-option label="高" :value="3" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 240px; margin-right: 16px;"
          @change="applyFilters"
        />
      </div>
      <!-- 统计信息 -->
      <div class="statistics-row" style="margin: 20px 0; display: flex; gap: 20px;">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ workOrders.filter(w => w.status === 0).length }}</div>
            <div class="stat-label">未分配</div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ workOrders.filter(w => w.status === 1).length }}</div>
            <div class="stat-label">已接单</div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ workOrders.filter(w => w.status === 2).length }}</div>
            <div class="stat-label">处理中</div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ workOrders.filter(w => w.status === 3).length }}</div>
            <div class="stat-label">已结束</div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ workOrders.filter(w => w.status === 4).length }}</div>
            <div class="stat-label">已退单</div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ workOrders.length }}</div>
            <div class="stat-label">总计</div>
          </div>
        </el-card>
      </div>
      <el-table :data="filteredWorkOrders" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="工单号" />
        <el-table-column prop="customerId" label="客户ID" />
        <el-table-column prop="serviceId" label="业务ID" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="priority" label="优先级" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="info">未分配</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="primary">已接单</el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="success">处理中</el-tag>
            <el-tag v-else-if="scope.row.status === 3" type="warning">已结束</el-tag>
            <el-tag v-else-if="scope.row.status === 4" type="danger">已退单</el-tag>
            <span v-else>{{ scope.row.status }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="expectedStartTime" label="预期开始时间" />
        <el-table-column prop="expectedEndTime" label="预期结束时间" />
        <el-table-column prop="actualStartTime" label="实际开始时间" />
        <el-table-column prop="actualEndTime" label="实际结束时间" />
        <el-table-column prop="feedback" label="反馈" />
        <el-table-column prop="returnReason" label="退单原因" />
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column label="操作" width="300">
          <template #default="scope">
            <el-button size="small" @click="editWorkOrder(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteWorkOrder(scope.row.id)">删除</el-button>
            <el-button v-if="scope.row.status === 0" size="small" type="primary" @click="acceptWorkOrder(scope.row)">接单</el-button>
            <el-button v-if="scope.row.status === 1" size="small" type="success" @click="startWorkOrder(scope.row)">开始处理</el-button>
            <el-button v-if="scope.row.status === 2" size="small" type="warning" @click="completeWorkOrder(scope.row)">已完结</el-button>
            <el-button v-if="scope.row.status < 3" size="small" type="danger" @click="returnWorkOrder(scope.row)">退单</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 新增/编辑工单弹窗 -->
      <el-dialog v-model="showAddDialog" :title="editMode ? '编辑工单' : '新建工单'">
        <el-form :model="form" label-width="120px">
          <el-form-item label="工单号"><el-input v-model="form.orderNo" /></el-form-item>
          <el-form-item label="客户ID"><el-input v-model="form.customerId" type="number" /></el-form-item>
          <el-form-item label="业务ID"><el-input v-model="form.serviceId" type="number" /></el-form-item>
          <el-form-item label="描述"><el-input v-model="form.description" /></el-form-item>
          <el-form-item label="优先级"><el-input v-model="form.priority" type="number" /></el-form-item>
          <el-form-item label="状态"><el-input v-model="form.status" type="number" /></el-form-item>
          <el-form-item label="预期开始时间"><el-input v-model="form.expectedStartTime" type="datetime-local" /></el-form-item>
          <el-form-item label="预期结束时间"><el-input v-model="form.expectedEndTime" type="datetime-local" /></el-form-item>
          <el-form-item label="实际开始时间"><el-input v-model="form.actualStartTime" type="datetime-local" /></el-form-item>
          <el-form-item label="实际结束时间"><el-input v-model="form.actualEndTime" type="datetime-local" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="saveWorkOrder">保存</el-button>
        </template>
      </el-dialog>
      <!-- 回单弹窗 -->
      <el-dialog v-model="showFeedbackDialog" title="工单回单">
        <el-form :model="feedbackForm" label-width="100px">
          <el-form-item label="反馈内容"><el-input v-model="feedbackForm.feedback" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showFeedbackDialog = false">取消</el-button>
          <el-button type="primary" @click="submitFeedback">提交</el-button>
        </template>
      </el-dialog>
      <!-- 退单弹窗 -->
      <el-dialog v-model="showReturnDialog" title="工单退单">
        <el-form :model="returnForm" label-width="100px">
          <el-form-item label="退单原因"><el-input v-model="returnForm.returnReason" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showReturnDialog = false">取消</el-button>
          <el-button type="primary" @click="submitReturn">提交</el-button>
        </template>
      </el-dialog>
      <!-- 接单弹窗 -->
      <el-dialog v-model="showAcceptDialog" title="接单">
        <el-form :model="acceptForm" label-width="120px">
          <el-form-item label="预期开始时间"><el-input v-model="acceptForm.expectedStartTime" type="datetime-local" /></el-form-item>
          <el-form-item label="预期结束时间"><el-input v-model="acceptForm.expectedEndTime" type="datetime-local" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showAcceptDialog = false">取消</el-button>
          <el-button type="primary" @click="submitAccept">接单</el-button>
        </template>
      </el-dialog>
      <!-- 开始处理弹窗 -->
      <el-dialog v-model="showStartDialog" title="开始处理">
        <el-form :model="startForm" label-width="120px">
          <el-form-item label="实际开始时间"><el-input v-model="startForm.actualStartTime" type="datetime-local" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showStartDialog = false">取消</el-button>
          <el-button type="primary" @click="submitStart">开始处理</el-button>
        </template>
      </el-dialog>
      <!-- 已完结弹窗 -->
      <el-dialog v-model="showCompleteDialog" title="工单完结">
        <el-form :model="completeForm" label-width="120px">
          <el-form-item label="实际结束时间"><el-input v-model="completeForm.actualEndTime" type="datetime-local" /></el-form-item>
          <el-form-item label="反馈内容"><el-input v-model="completeForm.feedback" type="textarea" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showCompleteDialog = false">取消</el-button>
          <el-button type="primary" @click="submitComplete">完结</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus, Download, Search } from '@element-plus/icons-vue';

const workOrders = ref([]);
const search = ref('');
const showAddDialog = ref(false);
const showFeedbackDialog = ref(false);
const showReturnDialog = ref(false);
const showAcceptDialog = ref(false);
const showStartDialog = ref(false);
const showCompleteDialog = ref(false);
const editMode = ref(false);
const form = ref({
  id: '', orderNo: '', customerId: '', serviceId: '', description: '', priority: '', status: '', 
  expectedStartTime: '', expectedEndTime: '', actualStartTime: '', actualEndTime: ''
});
const feedbackForm = ref({ id: '', feedback: '' });
const returnForm = ref({ id: '', returnReason: '' });
const acceptForm = ref({ expectedStartTime: '', expectedEndTime: '' });
const startForm = ref({ actualStartTime: '' });
const completeForm = ref({ actualEndTime: '', feedback: '' });
const statusFilter = ref(null);
const priorityFilter = ref(null);
const dateRange = ref(null);

const fetchWorkOrders = async () => {
  const res = await fetch('http://localhost:8080/workorder/getworkorders');
  workOrders.value = await res.json();
};
onMounted(fetchWorkOrders);

const filteredWorkOrders = computed(() => {
  let filtered = workOrders.value;
  
  // 文本搜索过滤
  if (search.value) {
    filtered = filtered.filter(w =>
      w.orderNo?.includes(search.value) ||
      w.description?.includes(search.value) ||
      String(w.customerId).includes(search.value)
    );
  }
  
  // 状态过滤
  if (statusFilter.value !== null) {
    filtered = filtered.filter(w => w.status === statusFilter.value);
  }
  
  // 优先级过滤
  if (priorityFilter.value !== null) {
    filtered = filtered.filter(w => w.priority === priorityFilter.value);
  }
  
  // 日期范围过滤
  if (dateRange.value && dateRange.value.length === 2) {
    const startDate = new Date(dateRange.value[0]);
    const endDate = new Date(dateRange.value[1]);
    endDate.setHours(23, 59, 59); // 设置为当天结束时间
    
    filtered = filtered.filter(w => {
      const createdAt = new Date(w.createdAt);
      return createdAt >= startDate && createdAt <= endDate;
    });
  }
  
  return filtered;
});

const editWorkOrder = (row) => {
  Object.assign(form.value, row);
  editMode.value = true;
  showAddDialog.value = true;
};

const saveWorkOrder = async () => {
  const url = editMode.value ? 'http://localhost:8080/workorder/update' : 'http://localhost:8080/workorder/add';
  const method = editMode.value ? 'PUT' : 'POST';
  const res = await fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(form.value),
  });
  if (await res.text() === 'success') {
    ElMessage.success(editMode.value ? '编辑成功' : '新增成功');
    showAddDialog.value = false;
    fetchWorkOrders();
    editMode.value = false;
    form.value = { id: '', orderNo: '', customerId: '', serviceId: '', description: '', priority: '', status: '', 
                   expectedStartTime: '', expectedEndTime: '', actualStartTime: '', actualEndTime: '' };
  } else {
    ElMessage.error('操作失败');
  }
};

const deleteWorkOrder = async (id) => {
  if (!confirm('确定要删除该工单吗？')) return;
  const res = await fetch(`http://localhost:8080/workorder/delete/${id}`, { method: 'DELETE' });
  if (await res.text() === 'success') {
    ElMessage.success('删除成功');
    fetchWorkOrders();
  } else {
    ElMessage.error('删除失败');
  }
};

const feedbackWorkOrder = (row) => {
  feedbackForm.value = { id: row.id, feedback: row.feedback || '' };
  showFeedbackDialog.value = true;
};
const submitFeedback = async () => {
  const res = await fetch('http://localhost:8080/workorder/feedback', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(feedbackForm.value),
  });
  if (await res.text() === 'success') {
    ElMessage.success('回单成功');
    showFeedbackDialog.value = false;
    fetchWorkOrders();
  } else {
    ElMessage.error('回单失败');
  }
};

const returnWorkOrder = (row) => {
  returnForm.value = { 
    id: row.id, 
    returnReason: row.returnReason || '' 
  };
  showReturnDialog.value = true;
};
const submitReturn = async () => {
  const res = await fetch('http://localhost:8080/workorder/return', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(returnForm.value),
  });
  if (await res.text() === 'success') {
    ElMessage.success('退单成功');
    showReturnDialog.value = false;
    fetchWorkOrders();
  } else {
    ElMessage.error('退单失败');
  }
};

const acceptWorkOrder = (row) => {
  acceptForm.value = { 
    id: row.id,
    expectedStartTime: row.expectedStartTime, 
    expectedEndTime: row.expectedEndTime 
  };
  showAcceptDialog.value = true;
};
const submitAccept = async () => {
  const res = await fetch('http://localhost:8080/workorder/accept', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(acceptForm.value),
  });
  if (await res.text() === 'success') {
    ElMessage.success('接单成功');
    showAcceptDialog.value = false;
    fetchWorkOrders();
  } else {
    ElMessage.error('接单失败');
  }
};

const startWorkOrder = (row) => {
  startForm.value = { 
    id: row.id,
    actualStartTime: row.actualStartTime 
  };
  showStartDialog.value = true;
};
const submitStart = async () => {
  const res = await fetch('http://localhost:8080/workorder/start', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(startForm.value),
  });
  if (await res.text() === 'success') {
    ElMessage.success('开始处理成功');
    showStartDialog.value = false;
    fetchWorkOrders();
  } else {
    ElMessage.error('开始处理失败');
  }
};

const completeWorkOrder = (row) => {
  completeForm.value = { 
    id: row.id,
    actualEndTime: row.actualEndTime, 
    feedback: row.feedback || '' 
  };
  showCompleteDialog.value = true;
};
const submitComplete = async () => {
  const res = await fetch('http://localhost:8080/workorder/complete', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(completeForm.value),
  });
  if (await res.text() === 'success') {
    ElMessage.success('工单完结成功');
    showCompleteDialog.value = false;
    fetchWorkOrders();
  } else {
    ElMessage.error('工单完结失败');
  }
};

const applyFilters = () => {
  // 这个函数会在筛选条件改变时自动触发
  // 由于使用了computed属性，不需要额外处理
  console.log('应用筛选条件:', {
    status: statusFilter.value,
    priority: priorityFilter.value,
    dateRange: dateRange.value
  });
};

const handleExport = async () => {
  try {
    // 准备导出数据
    const exportData = workOrders.value.map(workOrder => ({
      '工单号': workOrder.orderNo,
      '客户ID': workOrder.customerId,
      '业务ID': workOrder.serviceId,
      '描述': workOrder.description,
      '优先级': workOrder.priority === 1 ? '低' : workOrder.priority === 2 ? '中' : workOrder.priority === 3 ? '高' : workOrder.priority,
      '状态': workOrder.status === 0 ? '未分配' : 
              workOrder.status === 1 ? '已接单' : 
              workOrder.status === 2 ? '处理中' : 
              workOrder.status === 3 ? '已结束' : 
              workOrder.status === 4 ? '已退单' : workOrder.status,
      '预期开始时间': workOrder.expectedStartTime,
      '预期结束时间': workOrder.expectedEndTime,
      '实际开始时间': workOrder.actualStartTime,
      '实际结束时间': workOrder.actualEndTime,
      '反馈': workOrder.feedback,
      '退单原因': workOrder.returnReason,
      '创建时间': workOrder.createdAt
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
    link.setAttribute('download', `工单数据_${new Date().toISOString().split('T')[0]}.csv`);
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
</script>

<style scoped>
.workorder-manage-bg {
  min-height: 100vh;
  background: #f8fafc;
  padding: 20px;
}

.workorder-manage-card {
  max-width: 1400px;
  margin: 0 auto;
  border-radius: 12px;
}

.workorder-manage-header {
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

.search-filters {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
}

.stat-card {
  flex: 1;
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s;
  border-radius: 8px;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-content {
  padding: 16px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .workorder-manage-header {
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
  
  .search-filters {
    flex-wrap: wrap;
    gap: 8px;
  }
}
</style> 