package com.wangmo.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangmo.crm.service.WorkOrdersService;
import com.wangmo.crm.dao.WorkOrdersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import com.wangmo.crm.entity.WorkOrders;

/**
* @author 86189
* @description 针对表【work_orders(工单信息表)】的数据库操作Service实现
* @createDate 2025-06-25 12:47:46
*/
@Service
public class WorkOrdersServiceImpl extends ServiceImpl<WorkOrdersMapper, WorkOrders>
    implements WorkOrdersService{
    @Autowired
    private WorkOrdersMapper workOrdersMapper;
    
    public List<WorkOrders> findAll(){
        return workOrdersMapper.selectAllWorkOrders();
    }
}




