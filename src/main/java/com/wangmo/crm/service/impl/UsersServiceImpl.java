package com.wangmo.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangmo.crm.service.UsersService;
import com.wangmo.crm.dao.UsersMapper;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wangmo.crm.entity.Users;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author 86189
* @description 针对表【users(用户信息表)】的数据库操作Service实现
* @createDate 2025-06-25 12:47:43
*/
@Service
@Transactional
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService{
    @Autowired
    private UsersMapper usersMapper;
    public List<Users> findAll(){
        return usersMapper.selectAllUsers();
    }
    public String register(String username, String password, Integer status){
        try {
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
            Users user = new Users();
            user.setUsername(username);
            user.setPassword(hashed);
            user.setStatus(status);
            this.save(user); // MyBatis-Plus 自动插入
            return "success!";
        } catch (Exception e) {
            throw new RuntimeException("注册失败: " + e.getMessage(), e);
        }
    }
    public Users findByUserName(String userName){
        return usersMapper.selectByUserName(userName);
    }
    public Users login(String uname){
        return usersMapper.selectByUserName(uname);
    }
    public void saveUsers(Users users){
        usersMapper.insert(users);
    }

    public void updateUsers(Users users){
        usersMapper.updateById(users);
    }

    public void deleteUsers(Integer id){
        usersMapper.deleteById(id);
    }
}




