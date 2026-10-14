package com.wangmo.crm.controller;

import com.wangmo.crm.entity.Services;
import com.wangmo.crm.service.ServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service")
public class ServicesController {
    @Autowired
    private ServicesService servicesService;
    @GetMapping("/getservices")
    public List<Services> getServices(){
        return servicesService.findAll();
    }

    @PostMapping("/add")
    public String addService(@RequestBody Services service) {
        servicesService.save(service);
        return "success";
    }

    @PutMapping("/update")
    public String updateService(@RequestBody Services service) {
        servicesService.updateById(service);
        return "success";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteService(@PathVariable Integer id) {
        servicesService.removeById(id);
        return "success";
    }

    @GetMapping("/get/{id}")
    public Services getService(@PathVariable Integer id) {
        return servicesService.getById(id);
    }

    @PostMapping("/import")
    public String importServices() {
        // TODO: 实现Excel/CSV导入
        return "not implemented";
    }

    @GetMapping("/export")
    public String exportServices() {
        // TODO: 实现Excel/CSV导出
        return "not implemented";
    }
}
