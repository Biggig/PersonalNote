package com.huangzilin.note.util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/*数据库的工具类
   1、得到数据库连接
   2、关闭资源
* */
public class DBUtil {
    //得到配置文件对象
    private static Properties properties = new Properties();
    static {
        try {
            //加载配置文件（输入流）
            InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            //通过load()方法将输入流的内容加载到配置文件对象中
            properties.load(in);
            //通过配置文件对象的getProperty()方法获取驱动名，并加载驱动
            Class.forName(properties.getProperty("jdbcName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
    * 获取数据库连接
    * */
    public static Connection getConnection(){
        Connection connection = null;
        try{
            String dbUrl = properties.getProperty("dbUrl");
            String dbName = properties.getProperty("dbName");
            String dbPwd = properties.getProperty("dbPwd");
            System.out.println("正在连接数据库...");
            connection = DriverManager.getConnection(dbUrl, dbName, dbPwd);
        } catch (SQLException throwables){
            throwables.printStackTrace();
        }

        return connection;
    }

    /*
    * 关闭资源
    * */
    public  static void close(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection){
        try{
            if(resultSet != null){
                resultSet.close();
            }
            if(preparedStatement != null){
                preparedStatement.close();
            }
            if(connection != null){
                connection.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
