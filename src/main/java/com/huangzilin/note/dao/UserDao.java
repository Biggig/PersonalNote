package com.huangzilin.note.dao;

import com.huangzilin.note.po.User;
import com.huangzilin.note.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    /*
    *    通过userid修改用户信息
            1、定义sql语句
                String sql = "update tb_user set nick = ?, mood = ?, head = ? where userid = ?";
            2、设置参数集合
            3、调用BaseDao更新方法，返回受影响的行数
    * */
    public static int updateUser(User user) {
        int row = 0;
        String sql = "update tb_user set nick = ?, mood = ?, head = ? where userid = ?";
        List<Object> params = new ArrayList<>();
        params.add(user.getNick());
        params.add(user.getMood());
        params.add(user.getHead());
        params.add(user.getUserid());
        row = BaseDao.executeUpdate(sql, params);
        return row;
    }

    /*
    * 通过用户名查询用户对象
    * 1、定义sql语句
    * 2、设置参数集合
    * 3、调用BaseDao
    * */
    public User queryUserByName(String name){
        User user = null;
        String sql = "select * from tb_user where uname = ?";

        List<Object> params = new ArrayList<>();
        params.add(name);

        user = (User) BaseDao.queryRow(sql, params, User.class);
        return user;

    }
    public User queryUserByName02(String name){
        User user = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtil.getConnection();//获取连接
            String sql = "select * from tb_user where uname = ?";//预编译
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();//查询
            if(resultSet.next()){
                user = new User();
                user.setUserid(resultSet.getInt("userid"));
                user.setUname(name);
                user.setHead(resultSet.getString("head"));
                user.setMood(resultSet.getString("mood"));
                user.setNick(resultSet.getString("nick"));
                user.setUpwd(resultSet.getString("upwd"));//密码已加密
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.close(resultSet, preparedStatement, connection);
        }
        return user;
    }

    /*
    * 验证昵称唯一性
    *       1、定义SQL语句
                通过用户ID查询除了当前登录用户之外是否有其他用户占用了该昵称
                    指定昵称  nick（前台传递
                    当前用户 userid（session中
                    String sql = "select * from tb_user where nick = ? and userid != ?"
            2、设置参数集合
            3、调用BaseDao的查询方法
    * */
    public User queryByNickAndUserid(String nick, Integer userid) {
        String sql = "select * from tb_user where nick = ? and userid != ?";
        List<Object> params = new ArrayList<>();
        params.add(nick);
        params.add(userid);
        User user = (User) BaseDao.queryRow(sql, params, User.class);
        return user;
    }
}
