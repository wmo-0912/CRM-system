package com.wangmo.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wangmo.crm.entity.Customers;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 86189
* @description 针对表【customers(客户信息表)】的数据库操作Service
* @createDate 2025-06-25 12:47:19
*/
@Service
public interface CustomersService extends IService<Customers> {
    List<Customers> getAll();
}
