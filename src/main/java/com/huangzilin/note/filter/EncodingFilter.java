package com.huangzilin.note.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/*
*请求乱码解决
*   乱码原因：
*       服务器默认编码不支持中文
*   乱码情况：
*       POST请求
*           Tomcat7及以下版本   乱码
*           Tomcat8及以上版本   乱码
*       GET请求
*           Tomcat7及以下版本   乱码
*           Tomcat8及以上版本   不乱码
*   解决：
*       POST请求
*           任何版本都会乱码，通过request。setCharacterEncoding("UTF-8")  (只针对POST有效)
*       GET请求
*           Tomcat8及以上版本不需要处理
*           Tomcat7及以下版本
*               new String(request.getParam("xxx").getBytes("ISO-8859-1"), "UTF-8")
* */

@WebFilter("/*")//过滤所有资源
public class EncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //基于Http
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //处理POST请求
        request.setCharacterEncoding("UTF-8");

        //得到请求类型
        String method = request.getMethod();
        //如果是GET请求，则判断服务器版本
        if("GET".equalsIgnoreCase(method)){
            String serverInfo = request.getServletContext().getServerInfo();//Apache Tomcat/8.5.73
            //通过截取字符串，获得版本号
            String version = serverInfo.substring(serverInfo.lastIndexOf('/')+1, serverInfo.indexOf("."));
            //判断服务器版本是否是Tomcat7及以下
            if(version!=null && Integer.parseInt(version) < 8){
                MyWrapper myRequest = new MyWrapper(request);
                //放行资源
                filterChain.doFilter(myRequest, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    /*
    * 1、定义内部类（类的本质是request）
    * 2、继承HttpServletRequestWrapper包装类
    * 3、重写getParameter()方法
    * */
    class MyWrapper extends HttpServletRequestWrapper{
        private HttpServletRequest request;

        //得到需要处理的request对象
        public MyWrapper(HttpServletRequest request) {
            super(request);
            this.request = request;
        }

        //重写getParameter，会比默认方法优先调用
        //在UserServlet的userLogin中被调用
        @Override
        public String getParameter(String name) {
            //获取参数
            String value = request.getParameter(name);
            if(name != null || name.isEmpty()){
                return value;
            }
            try{
                value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
            }catch (Exception e){
                e.printStackTrace();
            }
            return value;
        }
    }

}
