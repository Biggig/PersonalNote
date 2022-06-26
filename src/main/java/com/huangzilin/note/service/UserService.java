package com.huangzilin.note.service;

import com.huangzilin.note.dao.BaseDao;
import com.huangzilin.note.dao.UserDao;
import com.huangzilin.note.po.User;
import com.huangzilin.note.vo.ResultInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

public class UserService {
    private  UserDao userDao = new UserDao();

    /*
    *   1、判断参数是否为空
            如果为空
                设置ResultInfo的状态码和提示信息
                返回ResultInfo对象
        2、如果不为空，通过用户名查询用户对象
        3、判断用户对象是否为空
            如果为空
                设置ResultInfo的状态码和提示信息
                返回ResultInfo对象
        4、用户对象不为空，将数据库中查询到的用户密码与前台传递的密码作比较（将密码加密再比较）
                如果密码不正确
                    设置ResultInfo的状态码和提示信息
                    返回ResultInfo对象
        5、如果密码正确
                设置ResultInfo的状态码和提示信息
        6、返回ResultInfo对象
    * */
    public  ResultInfo<User> userLogin(String userName, String userPwd){
        ResultInfo<User> resultInfo = new ResultInfo<User>();

        //数据回显：当登录实现时，将登录信息返回给页面显示
        User u = new User();
        u.setUname(userName);
        u.setUpwd(userPwd);
        resultInfo.setResult(u);

        if(userName == null || userPwd == null || userName.isEmpty() || userPwd.isEmpty()){
            resultInfo.setCode(0);
            resultInfo.setMessage("用户名或密码不能为空！");//前台判断了，后台也要判断
            return resultInfo;
        }
        User user = userDao.queryUserByName(userName);
        if(user == null){
            resultInfo.setCode(0);
            resultInfo.setMessage("用户不存在！");
            return resultInfo;
        }
        if(!userPwd.equals(user.getUpwd())){
            resultInfo.setCode(0);
            resultInfo.setMessage("密码错误！");
            return resultInfo;
        }
        resultInfo.setCode(1);
        resultInfo.setResult(user);
        return resultInfo;
    }
    /*
    * 验证昵称的唯一性
    *       1、判断参数是否为空
                如果为空，返回”0“
            2、调用DAO层，通过用户ID和昵称查询用户对象
            3、判断用户对象存在
                存在，返回”0“
                不存在，返回”1“
    * */
    public Integer checkNick(String nick, Integer userid){
        if(nick == null || nick.length() == 0){
            return 0;
        }
        //2、调用DAO层，通过用户ID和昵称查询用户对象
        User user = userDao.queryByNickAndUserid(nick, userid);
        if(user != null){
            return 0;
        }else {
            return 1;
        }
    }

    /*
    *       1、获取参数 （昵称、心情
            2、参数的非空校验 （判断必填参数非空
                如果昵称为空，将状态码和错误信息设置到resultInfo对象中，并return
            3、从session作用域中获取用户对象 （获取用户对象的默认头像
            4、实现上传文件
                1、获取Part对象 request.getPart("name")  name代表的是file文件域的name属性值
                2、通过Part对象获取上传文件的文件名
                3、判断文件名是否为空
                4、获取文件存放路径 WEB-INF/upload
                5、上传文件到指定目录
            5、更新用户头像 （将原本用户对象中的默认头像设置为上传的文件名
            6、调用Dao层的更新方法，返回受影响的行数
            7、判断受影响的行数
                大于0，则修改成功，否则修改失败
            8、返回resultInfo对象
    * */
    public ResultInfo<User> updateUser(HttpServletRequest req) {
        ResultInfo<User> resultInfo = new ResultInfo<>();
        //1、获取参数 （昵称、心情
        String nick = req.getParameter("nick");
        String mood = req.getParameter("mood");
        // 2、参数的非空校验 （判断必填参数非空
        if(nick == null || nick.length() == 0){
            resultInfo.setCode(0);
            resultInfo.setMessage("用户昵称不能为空");
            return resultInfo;
        }
        //3、从session作用域中获取用户对象 （获取用户对象的默认头像
        User user = (User) req.getSession().getAttribute("user");
        user.setNick(nick);
        user.setMood(mood);
        //4、实现上传文件
        try {
            //1、获取Part对象 request.getPart("name")  name代表的是file文件域的name属性值
            Part part = req.getPart("img");
            //2、通过Part对象获取上传文件的文件名
            String header = part.getHeader("Content-Disposition");
            //获取具体的请求头对应的值
            String str = header.substring(header.lastIndexOf("=") + 2);
            //获取上传的文件名
            String fileName = str.substring(0, str.length()-1);
            //3、判断文件名是否为空
            if(fileName != null && fileName.length() != 0){
                //更新用户头像
                user.setHead(fileName);
                //4、获取文件存放路径 WEB-INF/upload
                String filePath = req.getServletContext().getRealPath("/WEB-INF/upload/");
                //5、上传文件到指定目录
                part.write(filePath + fileName );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        int row = UserDao.updateUser(user);
        if(row>0){
            resultInfo.setCode(1);
            //更新session中的用户对象
            req.getSession().setAttribute("user", user);
        } else {
            resultInfo.setCode(0);
            resultInfo.setMessage("更新失败");
        }
        return resultInfo;

    }
}
