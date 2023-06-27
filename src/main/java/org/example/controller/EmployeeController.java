package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseResult;
import org.example.entity.Employee;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     * @param httpServletRequest
     * @param employee
     * @return
     * */
    @PostMapping("/login")
    public BaseResult<Employee> login(HttpServletRequest httpServletRequest, @RequestBody Employee employee) {

        //将密码进行md5处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //根据提交的用户名查询用户
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee  employeeByLogin = employeeService.getOne(queryWrapper);

        //如果用户不存在
        if (employeeByLogin == null) {
            return BaseResult.error("用户不存在");
        }

        //比对密码
        if(!employeeByLogin.getPassword().equals(password)) {
            return BaseResult.error("密码错误");
        }

        //查看员工状态
        if(employeeByLogin.getStatus() == 0) {
            return BaseResult.error("该员工已被禁用");
        }

        //确定登录成功
        httpServletRequest.getSession().setAttribute("employee", employee.getId());
        return BaseResult.success(employeeByLogin);
    }

    /**
     * 员工登出
     * @param httpServletRequest
     * @return
     * */
    @PostMapping("/logout")
    public BaseResult<String> logout(HttpServletRequest httpServletRequest) {
        //清除session
        httpServletRequest.getSession().removeAttribute("employee");
        return BaseResult.success("登出成功");
    }
}
