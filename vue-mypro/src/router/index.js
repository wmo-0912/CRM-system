import { createRouter, createWebHistory } from 'vue-router';
import Register from '../views/Register.vue';
import Login from '../views/Login.vue';
import ApiTest from '../views/ApiTest.vue'
import CustomerProfile from '../views/CustomerProfile.vue';
import CustomerManage from '../views/CustomerManage.vue';
import ServiceManage from '../views/ServiceManage.vue';
import AdminDashboard from '../views/AdminDashboard.vue';
import WorkOrderManage from '../views/WorkOrderManage.vue';
import UserManage from '../views/UserManage.vue';
import CustomerDashboard from '../views/CustomerDashboard.vue';
import CustomerService from '../views/CustomerService.vue';
import CustomerWorkOrder from '../views/CustomerWorkOrder.vue';
import CustomerInfoManage from '../views/CustomerInfoManage.vue';
import UserProfile from '../views/UserProfile.vue';

const routes = [
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/', redirect: '/login' },
  { path: '/apitest', component: ApiTest },
  { path: '/user/profile', component: UserProfile },
  { path: '/customer/profile', component: CustomerProfile },
  { path: '/customer', component: CustomerDashboard },
  { 
    path: '/admin', 
    component: AdminDashboard, 
    children: [
      { path: 'customer/manage', component: CustomerManage },
      { path: 'service/manage', component: ServiceManage },
      { path: 'workorder/manage', component: WorkOrderManage },
      { path: 'user/manage', component: UserManage },
      { path: '', redirect: 'customer/manage' }
    ] 
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;