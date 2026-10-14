package com.wangmo.crm.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wangmo.crm.entity.Services;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 86189
* @description 针对表【services(业务信息表)】的数据库操作Mapper
* @createDate 2025-06-25 12:47:40
* @Entity com.wangmo.crm.entity.Services
*/
@Mapper
public interface ServicesMapper extends BaseMapper<Services> {
    @Select( value = "select * from services")
    List<Services> selectAllServices();
}




