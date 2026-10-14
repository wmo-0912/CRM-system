package com.wangmo.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangmo.crm.entity.Services;
import com.wangmo.crm.service.ServicesService;
import com.wangmo.crm.dao.ServicesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 86189
* @description 针对表【services(业务信息表)】的数据库操作Service实现
* @createDate 2025-06-25 12:47:40
*/
@Service
public class ServicesServiceImpl extends ServiceImpl<ServicesMapper, Services>
    implements ServicesService{
    @Autowired
    private ServicesMapper servicesMapper;
    public List<Services> findAll(){
        return servicesMapper.selectAllServices();
    }
}




