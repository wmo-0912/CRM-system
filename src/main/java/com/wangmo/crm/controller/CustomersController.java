package com.wangmo.crm.controller;

import com.wangmo.crm.entity.Customers;
import com.wangmo.crm.service.CustomersService;
import com.wangmo.crm.service.UsersService;
import com.wangmo.crm.service.WorkOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomersController {
    @Autowired
    private CustomersService customersService;
    @Autowired
    private UsersService usersService;
    @Autowired
    private WorkOrdersService workOrdersService;

    @GetMapping("/getcustomers")
    public List<Customers> getCustomers(){
        return customersService.getAll();
    }

    @PutMapping("/update")
    public String updateCustomer(@RequestBody Customers customer) {
        customersService.updateById(customer);
        return "success";
    }

    @PostMapping("/add")
    public String addCustomer(@RequestBody Customers customer) {
        customersService.save(customer);
        return "success";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id) {
        try {
            // 先删除该客户的所有工单
            List<com.wangmo.crm.entity.WorkOrders> workOrders = workOrdersService.findAll();
            for (com.wangmo.crm.entity.WorkOrders workOrder : workOrders) {
                if (workOrder.getCustomerId() != null && workOrder.getCustomerId().equals(id)) {
                    workOrdersService.removeById(workOrder.getId());
                }
            }
            
            // 删除客户记录
            customersService.removeById(id);
            
            // 删除用户记录
            usersService.deleteUsers(id);
            
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }

    @GetMapping("/get/{id}")
    public Customers getCustomer(@PathVariable Integer id) {
        return customersService.getById(id);
    }

    @PostMapping("/import")
    public String importCustomers() {
        // TODO: 实现Excel/CSV导入
        return "not implemented";
    }

    @GetMapping("/export")
    public String exportCustomers() {
        // TODO: 实现Excel/CSV导出
        return "not implemented";
    }
}
