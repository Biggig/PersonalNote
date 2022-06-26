package com.huangzilin.note.dao;

import com.huangzilin.note.util.DBUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/*
* 基础JDBC操作类
*   更新
*       添加、修改、删除
*       1、得到数据库连接
*       2、定义sql语句
*       3、预编译
*       4、如果有参数，设置参数，下标从1开始（数组或集合，循环设置参数）
*       5、执行更新，返回受影响的行数
*       6、关闭资源
*   查询
*       1、查询一个字段
*       2、查询集合
*       3、查询某个对象
* */
public class BaseDao {
    public static int executeUpdate(String sql, List<Object> params){
        int row = 0;//受影响行数
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = DBUtil.getConnection();

            preparedStatement = connection.prepareStatement(sql);
            //判断有无参数
            if(params!=null && params.size()>0){
                //循环设置参数，设置参数类型为object
                for(int i=1;i<=params.size();i++){
                    preparedStatement.setObject(i, params.get(i-1));
                }
            }
            //执行更新，返回受影响的行数
            row = preparedStatement.executeUpdate();
        }catch (Exception e){
             e.printStackTrace();
        }finally {
            DBUtil.close(null, preparedStatement, connection);
        }
        return row;
    }

    /*
     *      1、得到数据库连接
     *      2、定义sql语句
     *      3、预编译
     *      4、如果有参数，设置参数，下标从1开始（数组或集合，循环设置参数）
     *      5、执行查询，返回结果集(使用object，因为不确定返回的字段类型
     *      6、判断并分析结果集
     *      7、关闭资源
    * */
    public static Object findSingleValue(String sql, List<Object> params){
        Object result = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            //判断有无参数
            if(params!=null && params.size()>0){
                //循环设置参数，设置参数类型为object
                for(int i=1;i<=params.size();i++){
                    preparedStatement.setObject(i, params.get(i-1));
                }
            }
            //执行查询
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                result = resultSet.getObject(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.close(resultSet, preparedStatement, connection);
        }
        return result;
    }

    /*查询集合（javaBean中的字段与数据库中表的字段名称要相同）
     *      1、得到数据库连接
     *      2、定义sql语句
     *      3、预编译
     *      4、如果有参数，设置参数，下标从1开始（数组或集合，循环设置参数）
     *      5、执行查询，返回结果集
     *      6、得到结果集的元数据？（查询到的字段数量以及查询了哪些字段
     *      7、判断并分析结果
     *          8、实例化对象
     *          9、遍历查询的字段数量，得到数据库中查询到的每一个列名
     *          10、通过反射，使用列名得到对应的field对象
     *          11、拼接set方法，得到字符串
     *          12、通过反射，将set方法的字符串反射成类中的指定set方法
     *          13、通过invoke调用set方法
     *          14、将对应javabean设置到集合中
     *      15、关闭资源
    * */
    public static List queryRows(String sql, List<Object> params, Class cls){
        List list = new ArrayList();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            //判断有无参数
            if(params!=null && params.size()>0){
                //循环设置参数，设置参数类型为object
                for(int i=1;i<=params.size();i++){
                    preparedStatement.setObject(i, params.get(i-1));
                }
            }
            //执行查询
            resultSet = preparedStatement.executeQuery();

            //得到结果集的元数据
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            //得到查询的字段数量
            int fieldNum = resultSetMetaData.getColumnCount();
            //判断并分析对象
            while(resultSet.next()){
                //实例化对象, 通过Class类对象创建对象，通过无参构造器创建
                Object object = cls.newInstance();
                //遍历查询的字段数量，得到数据库中查询的每一个列名
                for(int i=1;i<=fieldNum;i++){
                    //得到查询的每一个列名
                    //getColumnName():获取列名
                    //getColumnLabel():获取别名
                    String columnName = resultSetMetaData.getColumnName(i);
                    //通过反射，使用列名得到对应的field对象，因为列名与javaBean中变量名相同
                    Field field = cls.getDeclaredField(columnName);//因为是private
                    //拼接set方法
                    String setMethod = "set" + columnName.substring(0,1).toUpperCase() + columnName.substring(1);
                    //通过反射，由字符串得到对应方法
                    Method method = cls.getMethod(setMethod,field.getType());
                    //得到每一个字段对应的值
                    Object value = resultSet.getObject(columnName);
                    //通过invoke方法调用set方法
                    method.invoke(object,value);
                }
                //将javaBean设置到集合中
                list.add(object);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.close(resultSet, preparedStatement, connection);
        }
        return list;
    }

    /*
    *查找单个对象
     */
    public static Object queryRow(String sql, List<Object> params, Class cls){
        Object object = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            //判断有无参数
            if(params!=null && params.size()>0){
                //循环设置参数，设置参数类型为object
                for(int i=1;i<=params.size();i++){
                    preparedStatement.setObject(i, params.get(i-1));
                }
            }
            //执行查询
            resultSet = preparedStatement.executeQuery();

            //得到结果集的元数据
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            //得到查询的字段数量
            int fieldNum = resultSetMetaData.getColumnCount();
            //判断并分析对象
            if(resultSet.next()){
                //实例化对象, 通过Class类对象创建对象，通过无参构造器创建
                object = cls.newInstance();
                //遍历查询的字段数量，得到数据库中查询的每一个列名
                for(int i=1;i<=fieldNum;i++){
                    //得到查询的每一个列名
                    //getColumnName():获取列名
                    //getColumnLabel():获取别名
                    String columnName = resultSetMetaData.getColumnName(i);
                    //通过反射，使用列名得到对应的field对象，因为列名与javaBean中变量名相同
                    Field field = cls.getDeclaredField(columnName);//因为是private
                    //拼接set方法
                    String setMethod = "set" + columnName.substring(0,1).toUpperCase() + columnName.substring(1);
                    //通过反射，由字符串得到对应方法
                    Method method = cls.getMethod(setMethod,field.getType());
                    //得到每一个字段对应的值
                    Object value = resultSet.getObject(columnName);
                    //通过invoke方法调用set方法
                    method.invoke(object,value);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.close(resultSet, preparedStatement, connection);
        }
        return object;
    }
}
