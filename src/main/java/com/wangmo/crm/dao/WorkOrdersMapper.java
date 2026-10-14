package com.wangmo.crm.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wangmo.crm.entity.WorkOrders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 86189
* @description 针对表【work_orders(工单信息表)】的数据库操作Mapper
* @createDate 2025-06-25 12:47:46
* @Entity com.wangmo.crm.entity.WorkOrders
*/
@Mapper
public interface WorkOrdersMapper extends BaseMapper<WorkOrders> {
    @Select("select * from work_orders")
    List<WorkOrders> selectAllWorkOrders();
}




