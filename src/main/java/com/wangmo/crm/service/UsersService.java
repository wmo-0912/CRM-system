package com.wangmo.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wangmo.crm.entity.Users;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 86189
* @description 针对表【users(用户信息表)】的数据库操作Service
* @createDate 2025-06-25 12:47:43
*/
@Service
public interface UsersService extends IService<Users> {
    List<Users> findAll();

    String register(String username, String password, Integer status);

    Users login(String username);

    Users findByUserName(String username);

    void saveUsers(Users users);

    void updateUsers(Users users);

    void deleteUsers(Integer id);
}
