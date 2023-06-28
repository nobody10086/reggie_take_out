package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.example.common.BaseResult;
import org.example.entity.Employee;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

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

    /**
     * 员工添加功能
     * */
    @PostMapping
    public BaseResult<String> addEmployee(HttpServletRequest httpServletRequest, @RequestBody Employee employee) {
        log.info("新增员工： {}", employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        Long empId = (Long)httpServletRequest.getSession().getAttribute("employee");
        log.info("httpServletRequest.getSession().getAttribute(\"employee\") : {}",httpServletRequest.getSession().getAttribute("employee") );

        employee.setCreateUser(1L);
        employee.setUpdateUser(1L);

        employeeService.save(employee);
        return BaseResult.success("添加成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public BaseResult<Page> page(int page, int pageSize, String name){
        log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return BaseResult.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public BaseResult<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        Long empId = (Long)request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return BaseResult.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public BaseResult<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return BaseResult.success(employee);
        }
        return BaseResult.error("没有查询到对应员工信息");
    }
}
