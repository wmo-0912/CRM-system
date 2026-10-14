package com.wangmo.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wangmo.crm.entity.Services;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 86189
* @description 针对表【services(业务信息表)】的数据库操作Service
* @createDate 2025-06-25 12:47:40
*/
@Service
public interface ServicesService extends IService<Services> {
    List<Services> findAll();
}
