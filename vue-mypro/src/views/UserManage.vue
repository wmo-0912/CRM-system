<template>
  <div class="user-manage-bg">
    <el-card class="user-manage-card">
      <div class="user-manage-header">
        <div class="header-left">
          <h2 class="page-title">用户管理</h2>
          <p class="page-subtitle">管理系统中的所有用户账户</p>
        </div>
        <div class="header-actions">
          <el-input v-model="search" placeholder="搜索用户名/真实姓名/部门" style="width: 240px; margin-right: 16px;" clearable @input="fetchUsers">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
        </div>
      </div>
      <el-table :data="filteredUsers" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="department" label="部门" />
        <el-table-column prop="position" label="职位" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="primary">管理员</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">客户</el-tag>
            <span v-else>{{ scope.row.status }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="editUser(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 新增/编辑用户弹窗 -->
      <el-dialog v-model="showAddDialog" :title="editMode ? '编辑用户' : '新增用户'">
        <el-form :model="form" label-width="100px">
          <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
          <el-form-item label="密码" v-if="!editMode"><el-input v-model="form.password" type="password" /></el-form-item>
          <el-form-item label="真实姓名"><el-input v-model="form.realName" /></el-form-item>
          <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
          <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
          <el-form-item label="部门"><el-input v-model="form.department" /></el-form-item>
          <el-form-item label="职位"><el-input v-model="form.position" /></el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status" placeholder="请选择状态">
              <el-option label="管理员" :value="0" />
              <el-option label="客户" :value="1" />
            </el-select>
            <div style="font-size: 12px; color: #999; margin-top: 4px;">
              0-管理员，1-客户
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="saveUser">保存</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Plus } from '@element-plus/icons-vue';

const users = ref([]);
const search = ref('');
const showAddDialog = ref(false);
const editMode = ref(false);
const form = ref({
  id: '', username: '', password: '', realName: '', phone: '', email: '', 
  department: '', position: '', status: ''
});

const fetchUsers = async () => {
  const res = await fetch('http://localhost:8080/user/userlist');
  users.value = await res.json();
};
onMounted(fetchUsers);

const filteredUsers = computed(() => {
  if (!search.value) return users.value;
  return users.value.filter(u =>
    u.username?.includes(search.value) ||
    u.realName?.includes(search.value) ||
    u.department?.includes(search.value)
  );
});

const editUser = (row) => {
  Object.assign(form.value, row);
  editMode.value = true;
  showAddDialog.value = true;
};

const saveUser = async () => {
  const url = editMode.value ? 'http://localhost:8080/user/update' : 'http://localhost:8080/user/adduser';
  const method = 'POST';
  const res = await fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(form.value),
  });
  const data = await res.json();
  if (data.code === 0) {
    ElMessage.success(editMode.value ? '编辑成功' : '新增成功');
    showAddDialog.value = false;
    fetchUsers();
    editMode.value = false;
    form.value = { id: '', username: '', password: '', realName: '', phone: '', email: '', 
                   department: '', position: '', status: '' };
  } else {
    ElMessage.error(data.msg || '操作失败');
  }
};

const deleteUser = async (id) => {
  if (!confirm('确定要删除该用户吗？')) return;
  const res = await fetch('http://localhost:8080/user/delete', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(id),
  });
  const data = await res.json();
  if (data.code === 0) {
    ElMessage.success('删除成功');
    fetchUsers();
  } else {
    ElMessage.error(data.msg || '删除失败');
  }
};
</script>

<style scoped>
.user-manage-bg {
  min-height: 100vh;
  background: #f8fafc;
  padding: 20px;
}

.user-manage-card {
  max-width: 1400px;
  margin: 0 auto;
  border-radius: 12px;
}

.user-manage-header {
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
  .user-manage-header {
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