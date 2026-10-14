package com.wangmo.crm.controller;

import com.wangmo.crm.entity.WorkOrders;
import com.wangmo.crm.service.WorkOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/workorder")
public class WorkOrdersController {
    @Autowired
    private WorkOrdersService workOrdersService;

    @GetMapping("/getworkorders")
    public List<WorkOrders> getWorkOrders(){
        try {
            System.out.println("开始查询工单数据...");
            List<WorkOrders> result = workOrdersService.findAll();
            System.out.println("查询到工单数量: " + (result != null ? result.size() : 0));
            return result;
        } catch (Exception e) {
            System.err.println("查询工单时发生错误: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/add")
    public String addWorkOrder(@RequestBody WorkOrders workOrder) {
        workOrdersService.save(workOrder);
        return "success";
    }

    @PutMapping("/update")
    public String updateWorkOrder(@RequestBody WorkOrders workOrder) {
        workOrdersService.updateById(workOrder);
        return "success";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteWorkOrder(@PathVariable Integer id) {
        workOrdersService.removeById(id);
        return "success";
    }

    // 接单
    @PostMapping("/accept")
    public String acceptWorkOrder(@RequestBody WorkOrders workOrder) {
        workOrder.setStatus(1); // 设置为已接单
        workOrdersService.updateById(workOrder);
        return "success";
    }

    // 开始处理
    @PostMapping("/start")
    public String startWorkOrder(@RequestBody WorkOrders workOrder) {
        workOrder.setStatus(2); // 设置为处理中
        workOrdersService.updateById(workOrder);
        return "success";
    }

    // 完结
    @PostMapping("/complete")
    public String completeWorkOrder(@RequestBody WorkOrders workOrder) {
        workOrder.setStatus(3); // 设置为已结束
        workOrdersService.updateById(workOrder);
        return "success";
    }

    // 退单
    @PostMapping("/return")
    public String returnOrder(@RequestBody WorkOrders workOrder) {
        workOrder.setStatus(4); // 设置为已退单
        workOrdersService.updateById(workOrder);
        return "success";
    }
}
