package com.wangmo.crm.controller;

import com.wangmo.crm.entity.Users;
import com.wangmo.crm.entity.Customers;
import com.wangmo.crm.result.Result;
import com.wangmo.crm.service.UsersService;
import com.wangmo.crm.service.CustomersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173") //解决跨域问题
public class UsersController {
    //注入userService
    @Autowired
    private UsersService usersService;

    @Autowired
    private CustomersService customersService;

    /**
     * 用户注册接口
     * @param user 包含用户名和密码的用户对象，通过RequestBody接收前端传来的JSON数据
     * @return 返回注册结果，如果用户名不存在，则返回注册成功结果；否则返回错误信息
     * 对参数没有进行验证，仅用于演示
     */
    @PostMapping("/register")
    public Result register (@RequestBody Users user) {
        String username = user.getUsername();
        String password = user.getPassword();
        Integer status = user.getStatus() != null ? user.getStatus() : 1; // 默认客户
        try {
            Users byuser = usersService.findByUserName(username);
            if (byuser == null){
                // 先注册用户
                Users newUser = new Users();
                newUser.setUsername(username);
                newUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
                newUser.setStatus(status);
                newUser.setEmail(user.getEmail());
                usersService.save(newUser); // 保存后id自动回填
                
                // 只有当用户注册为客户时，才创建客户记录
                if (status == 1) {
                    Customers customer = new Customers();
                    customer.setId(newUser.getId());
                    customer.setName(user.getUsername());
                    customer.setEmail(newUser.getEmail());
                    customersService.save(customer);
                    // 返回新customer的id
                    return Result.success(customer.getId());
                } else {
                    // 管理员注册成功，返回用户id
                    return Result.success(newUser.getId());
                }
            }
            return Result.error("用户名已被占用！");
        } catch (Exception e) {
            return Result.error("注册失败：" + e.getMessage());
        }
    }
    /**
     * 用户登录接口
     * @param user 包含用户名和密码的用户对象，通过RequestBody接收前端传来的JSON数据
     * @return 返回登录结果，如果用户名正确且密码匹配，则返回登录成功结果；否则返回错误信息
     * 此登陆接口没有涉及对密码的加密，因此仅用于演示，在实际应用中应使用加密技术保护密码
     * 也没有设计token，因此登录成功后，用户无法退出，需要手动关闭浏览器或重新打开浏览器
     */
    @PostMapping("/login")
    public Result login(@RequestBody Users user) {
        String username = user.getUsername();
        String password = user.getPassword();
        Integer status = user.getStatus();
        Users byUser = usersService.findByUserName(username);
        if (byUser == null){
            return Result.error("用户名错误！");
        }
        if (!BCrypt.checkpw(password, byUser.getPassword())) {
            return Result.error("密码错误！");
        }
        if (!byUser.getStatus().equals(status)){
            return Result.error("角色不匹配！");
        }
        // 返回用户信息，包括用户ID
        return Result.success(byUser);
    }

    @GetMapping("/userlist")
    public List<Users> getUsers(){
        return  usersService.findAll();
    }

    @PostMapping("/adduser")
    public Result addUser(@RequestBody Users user){
        usersService.saveUsers(user);
        return  Result.success(user);
    }
    @PostMapping("/delete")
    public Result deleteUser(@RequestBody Integer id){
        usersService.deleteUsers(id);
        return Result.success("删除成功");
    }

    /**
     * 管理员修改用户信息接口
     * @param user 包含用户ID和更新信息的用户对象
     * @return 返回修改结果
     */
    @PostMapping("/update")
    public Result updateUser(@RequestBody Users user) {
        try {
            usersService.updateUsers(user);
            // 如果是客户，自动同步更新客户表的邮箱
            if (user.getStatus() != null && user.getStatus() == 1) {
                // 客户ID和用户ID一致
                Customers customer = customersService.getById(user.getId());
                if (customer != null) {
                    customer.setEmail(user.getEmail());
                    customersService.updateById(customer);
                }
            }
            return Result.success("用户信息修改成功");
        } catch (Exception e) {
            return Result.error("修改失败：" + e.getMessage());
        }
    }
}
