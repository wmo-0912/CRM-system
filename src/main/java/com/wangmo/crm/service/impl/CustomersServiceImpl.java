package com.wangmo.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangmo.crm.service.CustomersService;
import com.wangmo.crm.dao.CustomersMapper;
import com.wangmo.crm.entity.Customers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 86189
* @description 针对表【customers(客户信息表)】的数据库操作Service实现
* @createDate 2025-06-25 12:47:19
*/
@Service
public class CustomersServiceImpl extends ServiceImpl<CustomersMapper, Customers>
    implements CustomersService{
    @Autowired
    private CustomersMapper customersMapper;
    public List<Customers> getAll() {
        return customersMapper.selectAllCustomers();
    }
}




