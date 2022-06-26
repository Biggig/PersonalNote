package com.huangzilin.note;

import com.huangzilin.note.dao.BaseDao;
import com.huangzilin.note.dao.UserDao;
import com.huangzilin.note.po.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TestUser {
    @Test
    public void testQueryUserByName(){
        UserDao userDao = new UserDao();
        User user = userDao.queryUserByName("admin");
        System.out.println(user.getUpwd());
    }

    @Test
    public void testAddUser(){
        String sql = "insert into tb_user (uname, upwd, nick, head, mood) values (?, ?, ?, ?, ?)";
        List<Object> params = new ArrayList<>();
        params.add("bcfgykl");
        params.add("1234");
        params.add("abcdf");
        params.add("404.jpg");
        params.add("GG");
        System.out.println(BaseDao.executeUpdate(sql, params));
    }


}
