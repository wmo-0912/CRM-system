<template>
  <div class="customer-service-bg">
    <el-card class="customer-service-card">
      <div class="customer-service-header">
        <div class="header-left">
          <h2 class="page-title">业务选择</h2>
          <p class="page-subtitle">选择您需要的服务，我们将为您创建工单</p>
        </div>
        <el-input v-model="search" placeholder="搜索业务名称/代码" style="width: 240px;" clearable>
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <div class="service-grid">
        <el-card v-for="service in filteredServices" :key="service.id" class="service-item" @click="selectService(service)">
          <div class="service-content">
            <h3>{{ service.name }}</h3>
            <p class="service-code">代码：{{ service.code }}</p>
            <p class="service-description">{{ service.description }}</p>
            <p class="service-price">价格：¥{{ service.price }}</p>
            <el-button type="primary" size="small" @click.stop="createWorkOrder(service)">选择此业务</el-button>
          </div>
        </el-card>
      </div>
      <!-- 创建工单弹窗 -->
      <el-dialog v-model="showWorkOrderDialog" title="创建工单">
        <el-form :model="workOrderForm" label-width="100px">
          <el-form-item label="业务名称">
            <el-input v-model="workOrderForm.serviceName" disabled />
          </el-form-item>
          <el-form-item label="工单描述">
            <el-input v-model="workOrderForm.description" type="textarea" placeholder="请描述您的需求" />
          </el-form-item>
          <el-form-item label="优先级">
            <el-select v-model="workOrderForm.priority" placeholder="请选择优先级">
              <el-option label="低" :value="1" />
              <el-option label="中" :value="2" />
              <el-option label="高" :value="3" />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showWorkOrderDialog = false">取消</el-button>
          <el-button type="primary" @click="submitWorkOrder">提交工单</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search } from '@element-plus/icons-vue';

const services = ref([]);
const search = ref('');
const showWorkOrderDialog = ref(false);
const workOrderForm = ref({
  serviceId: '',
  serviceName: '',
  description: '',
  priority: 2
});

const fetchServices = async () => {
  const res = await fetch('http://localhost:8080/service/getservices');
  services.value = await res.json();
  
  // 添加调试信息
  console.log('获取到的所有业务:', services.value);
  const availableServices = services.value.filter(s => s.status !== 1);
  const unavailableServices = services.value.filter(s => s.status === 1);
  console.log('可选业务数量:', availableServices.length);
  console.log('不可选业务数量:', unavailableServices.length);
  if (unavailableServices.length > 0) {
    console.log('不可选业务:', unavailableServices.map(s => ({ id: s.id, name: s.name, status: s.status })));
  }
};
onMounted(fetchServices);

const filteredServices = computed(() => {
  // 首先过滤掉不可选的业务（status === 1）
  let availableServices = services.value.filter(s => s.status !== 1);
  
  // 然后根据搜索条件进一步过滤
  if (!search.value) return availableServices;
  return availableServices.filter(s =>
    s.name?.includes(search.value) ||
    s.code?.includes(search.value)
  );
});

const selectService = (service) => {
  // 可以在这里添加业务详情查看逻辑
  console.log('选择业务:', service);
};

const createWorkOrder = (service) => {
  workOrderForm.value.serviceId = service.id;
  workOrderForm.value.serviceName = service.name;
  workOrderForm.value.description = '';
  workOrderForm.value.priority = 2;
  showWorkOrderDialog.value = true;
};

const submitWorkOrder = async () => {
  try {
    // 获取当前客户ID（从localStorage或登录状态）
    const customerId = localStorage.getItem('customerId') || 1; // 默认值
    
    const workOrder = {
      customerId: customerId,
      serviceId: workOrderForm.value.serviceId,
      description: workOrderForm.value.description,
      priority: workOrderForm.value.priority,
      status: 0, // 未分配状态
      orderNo: 'WO' + Date.now() // 生成工单号
    };

    const res = await fetch('http://localhost:8080/workorder/add', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(workOrder),
    });
    
    if (await res.text() === 'success') {
      ElMessage.success('工单创建成功，等待管理员分配');
      showWorkOrderDialog.value = false;
    } else {
      ElMessage.error('工单创建失败');
    }
  } catch (e) {
    ElMessage.error('网络错误');
  }
};
</script>

<style scoped>
.customer-service-bg {
  min-height: 100vh;
  background: #f8fafc;
  padding: 20px;
}

.customer-service-card {
  max-width: 1200px;
  margin: 0 auto;
  border-radius: 12px;
}

.customer-service-header {
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

.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-top: 20px;
}

.service-item {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 12px;
  overflow: hidden;
}

.service-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.service-content {
  padding: 20px;
}

.service-content h3 {
  margin: 0 0 12px 0;
  color: #1f2937;
  font-size: 18px;
  font-weight: 600;
}

.service-code {
  color: #6b7280;
  font-size: 13px;
  margin: 8px 0;
  font-family: 'Courier New', monospace;
}

.service-description {
  color: #4b5563;
  margin: 12px 0;
  line-height: 1.6;
  font-size: 14px;
}

.service-price {
  color: #dc2626;
  font-weight: 600;
  margin: 12px 0 16px 0;
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .customer-service-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .service-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .page-title {
    font-size: 20px;
  }
}
</style> 