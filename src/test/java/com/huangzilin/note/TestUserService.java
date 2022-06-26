package com.huangzilin.note;

import com.huangzilin.note.po.User;
import com.huangzilin.note.service.UserService;
import com.huangzilin.note.vo.ResultInfo;
import org.junit.jupiter.api.Test;

public class TestUserService {
    @Test
    public void testUserService(){
        UserService userService = new UserService();
        ResultInfo<User> resultInfo = userService.userLogin(null, null);
        System.out.println(resultInfo.getMessage());
    }
}
