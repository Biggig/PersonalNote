package com.huangzilin.note.filter;

import com.huangzilin.note.po.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
* 非法访问拦截
*   拦截所有资源
*   需要被放行的资源
*       1、指定页面（不需登录即可访问的，例如登录页面、注册页面
*       2、静态资源（存放在statics中的资源
*       3、指定行为（用户无需登录即可执行的操作
*       4、登录状态（判断session作用域中是否存在user对象，存在则放行，不存在则拦截跳转至登录页面
*
*   免登录
*       通过cookie实现
*       什么时候免登录：
*           当用户处于未登录状态，且去请求需要登录才能访问的资源，调用自动登录
*       目的：
*           自动调用登录方法
*       实现：
*           从cookie对象中获取用户的姓名和密码，自动执行登录
*               1、获取Cookie数组 request.getCookies()
*               2、判断Cookie数组
*               3、遍历Cookie数组，获取指定Cookie对象（name为"user"的对象）
*               4、得到对应的Cookie对象的value（用户名和密码 userName-userPwd)
*               5、通过split分割value，分别得到对应的姓名与密码
*               6、请求转发到登录操作
*               7、return
*   如果以上判断皆不满足，则拦截跳转到登录界面
*
* */
@WebFilter("/*")
public class LoginAccessFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //基于HTTP
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //得到访问的路径
        String path = request.getRequestURI(); //项目路径/资源路径

        if(path.contains("/login.jsp")){
            //1、获取Cookie数组 request.getCookies()
            Cookie[] cookies = request.getCookies();
            //2、判断Cookie数组
            if(cookies != null && cookies.length >0){
                for(Cookie cookie:cookies){
                    //4、得到对应的Cookie对象的value（用户名和密码 userName-userPwd)
                    if("user".equals(cookie.getName())){
                        String value = cookie.getValue();
                        //5、通过split分割value，分别得到对应的姓名与密码
                        String[] val = value.split("-");
                        String userName = val[0];
                        String userPwd = val[1];
                        //6、请求转发到登录操作
                        String url = "user?actionName=login&rem=1&userName=" + userName + "&userPwd=" + userPwd;
                        request.getRequestDispatcher(url).forward(request, response);
                        return;

                    }
                }
            }
            filterChain.doFilter(request, response);
            return;
        }
        if(path.contains("/statics")){
            filterChain.doFilter(request, response);
            return;
        }
        if(path.contains("/user")){
            String actionName = request.getParameter("actionName");
            if("login".equals(actionName)){
                filterChain.doFilter(request, response);
                return;
            }
        }
        //获取session作用域中的user对象
        User user = (User)request.getSession().getAttribute("user");
        //判断User对象是否为空
        if(user != null){
            filterChain.doFilter(request, response);
            return;
        }

        /*      免登录
         *           从cookie对象中获取用户的姓名和密码，自动执行登录
         */
        //1、获取Cookie数组 request.getCookies()
        Cookie[] cookies = request.getCookies();
        //2、判断Cookie数组
        if(cookies != null && cookies.length >0){
            for(Cookie cookie:cookies){
                //4、得到对应的Cookie对象的value（用户名和密码 userName-userPwd)
                if("user".equals(cookie.getName())){
                    String value = cookie.getValue();
                    //5、通过split分割value，分别得到对应的姓名与密码
                    String[] val = value.split("-");
                    String userName = val[0];
                    String userPwd = val[1];
                    //6、请求转发到登录操作
                    String url = "user?actionName=loginByCookie&rem=1&userName=" + userName + "&userPwd=" + userPwd;
                    request.getRequestDispatcher(url).forward(request, response);
                    return;

                }
            }
        }
        //拦截请求，重定向回login页面
        response.sendRedirect("login.jsp");

    }

    @Override
    public void destroy() {

    }
}
