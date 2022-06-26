package com.huangzilin.note;

import com.huangzilin.note.util.DBUtil;
import org.junit.jupiter.api.Test;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestDB {
    /*
    * 单元测试方法：
    * 1、方法返回值一般使用void
    * 2、参数列表一般为空
    * 3、方法上需要设置@Test注解
    * 4、每个方法都能独立运行
    * */

    //使用日志工厂类
    private Log log = LogFactory.getLog(TestDB.class);
    @Test
    public void testDB(){
        System.out.println(DBUtil.getConnection());

        log.info( "获取数据库连接: " + DBUtil.getConnection());
    }

}
