package com.wangmo.crm.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wangmo.crm.entity.Users;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 86189
* @description 针对表【users(用户信息表)】的数据库操作Mapper
* @createDate 2025-06-25 12:47:43
* @Entity com.wangmo.crm.entity.Users
*/
@Mapper
public interface UsersMapper extends BaseMapper<Users> {
    @Select( value = "select * from users")
    List<Users> selectAllUsers();
    @Select("select * from users where username = #{username}")
    Users selectByUserName(@Param("username") String username);

}




