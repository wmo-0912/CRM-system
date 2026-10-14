<template>
  <div class="customer-workorder-bg">
    <el-card class="customer-workorder-card">
      <div class="customer-workorder-header">
        <div class="header-left">
          <h2 class="page-title">我的工单</h2>
          <p class="page-subtitle">查看和管理您的所有工单</p>
        </div>
        <div class="header-actions">
          <el-input v-model="search" placeholder="搜索工单号/描述" style="width: 240px; margin-right: 16px;" clearable>
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button @click="handleExport" type="success">
            <el-icon><Download /></el-icon>
            导出工单
          </el-button>
          <el-upload :show-file-list="false" :before-upload="handleImport" accept=".csv,.xlsx">
            <el-button type="primary">
              <el-icon><Upload /></el-icon>
              导入工单
            </el-button>
          </el-upload>
        </div>
      </div>
      <el-table :data="filteredWorkOrders" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="orderNo" label="工单号" width="120" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="priority" label="优先级" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.priority === 1" type="info">低</el-tag>
            <el-tag v-else-if="scope.row.priority === 2" type="warning">中</el-tag>
            <el-tag v-else-if="scope.row.priority === 3" type="danger">高</el-tag>
            <span v-else>{{ scope.row.priority }}</span>
          </template>
        </el-table-column>
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
        <el-table-column prop="expectedStartTime" label="预期开始时间" width="150" />
        <el-table-column prop="expectedEndTime" label="预期结束时间" width="150" />
        <el-table-column prop="actualStartTime" label="实际开始时间" width="150" />
        <el-table-column prop="actualEndTime" label="实际结束时间" width="150" />
        <el-table-column prop="feedback" label="反馈" show-overflow-tooltip />
        <el-table-column prop="returnReason" label="退单原因" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.status === 4 && scope.row.returnReason" style="color: #f56c6c;">
              {{ scope.row.returnReason }}
            </span>
            <span v-else-if="scope.row.status === 4" style="color: #f56c6c;">
              暂无退单原因
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="150" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewWorkOrderDetail(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 工单详情弹窗 -->
      <el-dialog v-model="showDetailDialog" title="工单详情" width="700px">
        <div v-if="selectedWorkOrder">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="工单号">{{ selectedWorkOrder.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag v-if="selectedWorkOrder.status === 0" type="info">未分配</el-tag>
              <el-tag v-else-if="selectedWorkOrder.status === 1" type="primary">已接单</el-tag>
              <el-tag v-else-if="selectedWorkOrder.status === 2" type="success">处理中</el-tag>
              <el-tag v-else-if="selectedWorkOrder.status === 3" type="warning">已结束</el-tag>
              <el-tag v-else-if="selectedWorkOrder.status === 4" type="danger">已退单</el-tag>
              <span v-else>{{ selectedWorkOrder.status }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="优先级">
              <el-tag v-if="selectedWorkOrder.priority === 1" type="info">低</el-tag>
              <el-tag v-else-if="selectedWorkOrder.priority === 2" type="warning">中</el-tag>
              <el-tag v-else-if="selectedWorkOrder.priority === 3" type="danger">高</el-tag>
              <span v-else>{{ selectedWorkOrder.priority }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ selectedWorkOrder.createdAt }}</el-descriptions-item>
            <el-descriptions-item label="预期开始时间">{{ selectedWorkOrder.expectedStartTime || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="预期结束时间">{{ selectedWorkOrder.expectedEndTime || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="实际开始时间">{{ selectedWorkOrder.actualStartTime || '未开始' }}</el-descriptions-item>
            <el-descriptions-item label="实际结束时间">{{ selectedWorkOrder.actualEndTime || '未结束' }}</el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">{{ selectedWorkOrder.description || '无描述' }}</el-descriptions-item>
            <el-descriptions-item label="反馈" :span="2">{{ selectedWorkOrder.feedback || '暂无反馈' }}</el-descriptions-item>
            <el-descriptions-item v-if="selectedWorkOrder.status === 4" label="退单原因" :span="2">
              <div style="color: #f56c6c; font-weight: bold;">
                {{ selectedWorkOrder.returnReason || '暂无退单原因' }}
              </div>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Download, Upload } from '@element-plus/icons-vue';

const workOrders = ref([]);
const search = ref('');
const showDetailDialog = ref(false);
const selectedWorkOrder = ref(null);

const fetchWorkOrders = async () => {
  try {
    const res = await fetch('http://localhost:8080/workorder/getworkorders');
    
    if (!res.ok) {
      throw new Error(`HTTP error! status: ${res.status}`);
    }
    
    const allWorkOrders = await res.json();
    
    // 检查返回的数据是否为数组
    if (!Array.isArray(allWorkOrders)) {
      console.error('返回的数据不是数组:', allWorkOrders);
      workOrders.value = [];
      ElMessage.error('获取工单数据格式错误');
      return;
    }
    
    // 获取当前客户ID（从localStorage）
    const customerId = localStorage.getItem('customerId');
    
    if (customerId) {
      // 过滤出当前客户的工单
      workOrders.value = allWorkOrders.filter(wo => wo.customerId == customerId);
    } else {
      // 如果没有customerId，显示所有工单（用于测试）
      workOrders.value = allWorkOrders;
    }
    
    console.log('客户ID:', customerId);
    console.log('所有工单:', allWorkOrders);
    console.log('过滤后工单:', workOrders.value);
    
    // 检查是否有退单的工单
    const returnedOrders = workOrders.value.filter(wo => wo.status === 4);
    if (returnedOrders.length > 0) {
      console.log('退单工单:', returnedOrders);
      returnedOrders.forEach(order => {
        console.log(`工单 ${order.orderNo} 退单原因:`, order.returnReason);
      });
    }
  } catch (e) {
    console.error('获取工单错误:', e);
    workOrders.value = [];
    ElMessage.error('获取工单失败: ' + e.message);
  }
};

onMounted(fetchWorkOrders);

const filteredWorkOrders = computed(() => {
  if (!search.value) return workOrders.value;
  return workOrders.value.filter(wo =>
    wo.orderNo?.includes(search.value) ||
    wo.description?.includes(search.value)
  );
});

const viewWorkOrderDetail = (workOrder) => {
  selectedWorkOrder.value = workOrder;
  showDetailDialog.value = true;
};

const handleExport = async () => {
  try {
    // 准备导出数据
    const exportData = workOrders.value.map(workOrder => ({
      '工单号': workOrder.orderNo,
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
    link.setAttribute('download', `我的工单_${new Date().toISOString().split('T')[0]}.csv`);
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
        const workOrder = {};
        headers.forEach((header, index) => {
          workOrder[header] = values[index] || '';
        });
        return workOrder;
      });

      // 转换数据格式
      const workOrdersToImport = importData.map(item => {
        const workOrder = {
          orderNo: item['工单号'] || item.orderNo,
          description: item['描述'] || item.description,
          priority: item['优先级'] === '低' ? 1 : item['优先级'] === '中' ? 2 : item['优先级'] === '高' ? 3 : parseInt(item['优先级']) || 2,
          status: item['状态'] === '未分配' ? 0 : 
                  item['状态'] === '已接单' ? 1 : 
                  item['状态'] === '处理中' ? 2 : 
                  item['状态'] === '已结束' ? 3 : 
                  item['状态'] === '已退单' ? 4 : parseInt(item['状态']) || 0,
          expectedStartTime: item['预期开始时间'] || item.expectedStartTime,
          expectedEndTime: item['预期结束时间'] || item.expectedEndTime,
          actualStartTime: item['实际开始时间'] || item.actualStartTime,
          actualEndTime: item['实际结束时间'] || item.actualEndTime,
          feedback: item['反馈'] || item.feedback,
          returnReason: item['退单原因'] || item.returnReason,
          customerId: localStorage.getItem('customerId') || 1 // 使用当前客户ID
        };
        return workOrder;
      });

      // 批量导入
      let successCount = 0;
      for (const workOrder of workOrdersToImport) {
        if (workOrder.orderNo) { // 只导入有工单号的数据
          try {
            const res = await fetch('http://localhost:8080/workorder/add', {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify(workOrder),
            });
            if (await res.text() === 'success') {
              successCount++;
            }
          } catch (error) {
            console.error('导入工单失败:', workOrder.orderNo, error);
          }
        }
      }

      ElMessage.success(`成功导入 ${successCount} 条工单数据`);
      fetchWorkOrders(); // 刷新数据
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
.customer-workorder-bg {
  min-height: 100vh;
  background: #f8fafc;
  padding: 20px;
}

.customer-workorder-card {
  max-width: 1400px;
  margin: 0 auto;
  border-radius: 12px;
}

.customer-workorder-header {
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
  .customer-workorder-header {
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