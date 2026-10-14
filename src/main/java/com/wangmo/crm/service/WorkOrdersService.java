package com.wangmo.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wangmo.crm.entity.WorkOrders;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 86189
* @description 针对表【work_orders(工单信息表)】的数据库操作Service
* @createDate 2025-06-25 12:47:46
*/
@Service
public interface WorkOrdersService extends IService<WorkOrders> {
    List<WorkOrders> findAll();
}
