# reggie_take_out
瑞吉外卖项目练习

## 2023.6.27
**登录界面逻辑:**

1. 将页面提交的密码password进行md5加密处理

2. 根据页面提交的用户名username查询数据库

3. 如果没有查询到则返回登灵失败结果

4. 密码比对，如果不一致则返回登录失败结果

5. 查看员工状态，如果为已禁用状态，则返回员工已禁用结

6. 登录成功，将员工id存入Session并返回登录成功结果

7. 完善登录功能，添加拦截器，拦截未登录的请求，如果未登录则跳转到登录页面
    
        过滤器具体的处理逻辑如下
        1、获取本次请求的URI
        2、判断本次请求是否需要处理
        3、如果不需要处理，则直接放行
        4、判断登录状态，如果已登录，则直接放行
        5、如果未登录则返回未登录结果

**登录后退出功能：**

        员工登录成功后，页面跳转到后台系统首页面(backend/index.html)，此时会显示当前登录用户的姓名
        如果员工需要退出系统，直接点击右侧的退出按钮即可退出系统，退出系统后页面应跳转回登录页面
        用户点击页面中退出按钮，发送请求，请求地址为/employee/logout，请求方式为POST.我们只需要在Controller中创建对应的处理方法即可，具体的处理逻辑:

1. 清理Session中的用户id

2. 返回结果

TODO
        
        拦截器拦截跳转到登录页面的请求，如果已登录则跳转到后台首页


## 2023.6.28
**员工管理功能：**

1. 新增员工功能

        1.1、点击新增按钮，弹出新增员工对话框
        1.2、在新增员工对话框中输入员工信息
        1.3、点击保存按钮，保存员工信息
        1.4、保存成功，关闭对话框，刷新页面
        1.5、保存失败，提示失败信息
