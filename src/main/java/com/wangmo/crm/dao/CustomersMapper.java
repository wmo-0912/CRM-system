package com.wangmo.crm.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wangmo.crm.entity.Customers;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 86189
* @description 针对表【customers(客户信息表)】的数据库操作Mapper
* @createDate 2025-06-25 12:47:19
* @Entity com.wangmo.crm.entity.Customers
*/
@Mapper
public interface CustomersMapper extends BaseMapper<Customers> {
    @Select( value = "select * from customers")
    List<Customers> selectAllCustomers();

}




