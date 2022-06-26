package com.huangzilin.note.web;
import com.huangzilin.note.po.User;
import com.huangzilin.note.service.UserService;
import com.huangzilin.note.vo.ResultInfo;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@WebServlet("/user")
@MultipartConfig
public class UserServlet extends HttpServlet {

    private UserService userService = new UserService();
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.service(req, resp);
        //设置首页导航高亮
        req.setAttribute("menu_page", "user");

        //接收用户行为
        String actionName = req.getParameter("actionName");
        //判断对应行为，调用对应方法
        if("login".equals(actionName)){
            userLogin(req, resp);
        } else if ("logout".equals(actionName)) {
            //用户退出
            userLogOut(req, resp);
        } else if ("userCenter".equals(actionName)) {
            //个人中心
            userCenter(req, resp);
        } else if ("userHead".equals(actionName)) {
            //加载头像
            userHead(req, resp);
        } else if ("checkNick".equals(actionName)) {
            //验证昵称唯一性
            checkNick(req, resp);
        } else if ("updateUser".equals(actionName)) {
            //修改用户信息
            updateUser(req, resp);
        }
    }

    /*
    * 修改用户信息
    *       1、调用service层方法，传递request对象，返回resultInfo对象
            2、将resultInfo对象存到request作用域中
            3、请求转发跳转个人中心页面 user?actionName=userCenter
    * */
    private void updateUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1、调用service层方法，传递request对象，返回resultInfo对象
        ResultInfo<User> resultInfo = userService.updateUser(req);
        //2、将resultInfo对象存到request作用域中
        req.setAttribute("resultInfo", resultInfo);
        //3、请求转发跳转个人中心页面 user?actionName=userCenter
        req.getRequestDispatcher("user?actionName=userCenter").forward(req, resp);
    }

    /*
    *       1、获取参数
            2、从session作用域获取用户对象，得到用户ID
            3、调用service层方法，得到返回结果
            4、通过字符输出流，将结果响应给前台的ajax的回调函数
            5、关闭资源
     * */
    private void checkNick(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //1、获取参数
        String nick = req.getParameter("nick");
        //2、从session作用域获取用户对象，得到用户ID
        User user = (User)req.getSession().getAttribute("user");
        //3、调用service层方法，得到返回结果
        Integer code = userService.checkNick(nick, user.getUserid());
        //4、通过字符输出流，将结果响应给前台的ajax的回调函数
        resp.getWriter().write(code + "");
        //5、关闭资源
        resp.getWriter().close();

    }

    /*
    * 加载头像
    *





    * */
    private void userHead(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //1、获取参数（图片名称
        String head = req.getParameter("imageName");
        //2、得到图片的存放路径（得到项目的真实路径request.getServletContext().getRealPath("/")
        String realPath = req.getServletContext().getRealPath("/WEB-INF/upload/");
        //3、通过图片的完整路径，得到file对象
        File file = new File(realPath + '/' + head);
        //4、通过截取，得到图片后缀
        String pic = head.substring(head.lastIndexOf(".") + 1);
        //5、通过不同后缀，设置不同的响应类型
        if("PNG".equalsIgnoreCase(pic)){
            resp.setContentType("image/png");
        } else if ("JPG".equalsIgnoreCase(pic) || "JPEG".equalsIgnoreCase(pic)) {
            resp.setContentType("image/jpeg");
        } else if ("GIF".equalsIgnoreCase(pic)) {
            resp.setContentType("image/gif");
        }
        //6、利用FileUtils的copyFile()方法，将图片拷贝给浏览器
        FileUtils.copyFile(file, resp.getOutputStream());

    }

    /*
    * 进入个人中心
    *   1、设置首页动态包含的页面值
    *   2、请求转发跳转到index
    * */
    private void userCenter(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("changePage", "user/info.jsp");

        req.getRequestDispatcher("index.jsp").forward(req, resp);
    }

    /*  1、销毁session对象
        2、删除cookie对象
        3、重定向跳转到登录页面*/
    private void userLogOut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.getSession().invalidate();

        Cookie cookie = new Cookie("user", null);
        cookie.setMaxAge(0);
        resp.addCookie(cookie);

        resp.sendRedirect("login.jsp");
    }

    /*
    *   1、获取参数
        2、调用service层函数，返回ResultInfo对象
        3、判断登录是否成功
            如果失败
                将ResultInfo对象设置到request作用域中
                请求转发到登录页面
            如果成功
                将用户信息存到session中
                判断用户是否选择记住密码(rem==1)
                    如果是，将用户姓名密码存到cookie中，设置失效时间，并响应给客户端
                    如果否，清空原有cookie对象
                重定向跳转至index页面
    * */
    private void userLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("userName");
        String userPwd = req.getParameter("userPwd");

        ResultInfo<User> resultInfo = userService.userLogin(userName, userPwd);

        if(resultInfo.getCode() == 1){
            req.getSession().setAttribute("user", resultInfo.getResult());

            String rem = req.getParameter("rem");
            if("1".equals(rem)){
                Cookie cookie = new Cookie("user", userName + '-' + userPwd);
                cookie.setMaxAge(3*24*60*60);
                resp.addCookie(cookie);
            }
            else {
                Cookie cookie = new Cookie("user", null);
                cookie.setMaxAge(0);
                resp.addCookie(cookie);
            }
            //重定向
            resp.sendRedirect("index");
        }
        else{
            req.setAttribute("resultInfo", resultInfo);
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }

    }
}
