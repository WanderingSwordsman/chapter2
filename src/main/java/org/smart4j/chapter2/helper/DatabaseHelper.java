package org.smart4j.chapter2.helper;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.chapter2.util.CollectionUtil;
import org.smart4j.chapter2.util.PropsUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
/**
 * Created by ShangJun on 2016/7/6.
 */
public final class DatabaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);
    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();
    //连接池
    private static final BasicDataSource DATA_SOURCE;
    //将ThreadLocal作为Connection的容器，不会出现线程安全问题且可以隐藏掉Connection的创建与关闭
    private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<Connection>();
    static {

        Properties conf = PropsUtil.loadProps("config.properties");
        DRIVER = conf.getProperty("jdbc.driver");
        URL = conf.getProperty("jdbc.url");
        USERNAME = conf.getProperty("jdbc.username");
        PASSWORD = conf.getProperty("jdbc.password");

        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(DRIVER);
        DATA_SOURCE.setUrl(URL);
        DATA_SOURCE.setUsername(USERNAME);
        DATA_SOURCE.setPassword(PASSWORD);
        try{
            Class.forName(DRIVER);
        }catch (ClassNotFoundException e){
            LOGGER.error("can not load jdbc driver", e );
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection(){
        Connection conn = CONNECTION_HOLDER.get();
        if(conn==null){
            try{
                conn = DATA_SOURCE.getConnection();
            }catch(SQLException e){
                LOGGER.error("get connection failure",e);
                throw new RuntimeException(e);
            }finally{
                CONNECTION_HOLDER.set(conn);
            }
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     */
    public static void closeConnection(){
        Connection conn = CONNECTION_HOLDER.get();
        if(conn != null){
            try{
                conn.close();
            }catch (SQLException e){
                LOGGER.error("close connection failure",e);
                throw new RuntimeException(e);
            }finally{
                CONNECTION_HOLDER.remove();
            }
        }
    }
    /**
     * 查询实体列表
     */
    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql,Object... params){
        List<T> entityList;
        try{
            Connection conn = getConnection();
            entityList = QUERY_RUNNER.query(conn,sql,new BeanListHandler<T>(entityClass),params);
        }catch(SQLException e){
            LOGGER.error("query entitylist failure",e);
            throw new RuntimeException(e);
        }finally{
            closeConnection();
        }
        return entityList;
    }
    /**
     * 查询实体
     */
    public static <T>T queryEntity(Class<T> entityClass, String sql,Object... params){
        T entity;
        try{
            Connection conn = getConnection();
            entity = QUERY_RUNNER.query(conn,sql,new BeanHandler<T>(entityClass),params);
        }catch(SQLException e){
            LOGGER.error("query entity failure",e);
            throw new RuntimeException(e);
        }finally{
            closeConnection();
        }
        return entity;
    }
    /**
     * 执行查询语句
     */
    public static List<Map<String,Object>> executeQuery( String sql,Object... params){
        List<Map<String,Object>> result;
        try{
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn,sql,new MapListHandler(),params);
        }catch(SQLException e){
            LOGGER.error("executequery failure",e);
            throw new RuntimeException(e);
        }finally{
            closeConnection();
        }
        return result;
    }
    /**
     * 执行更新语句（增删改）
     */
    public static int executeUpdate( String sql,Object... params){
        int rows = 0;
        try{
            Connection conn = getConnection();
            rows = QUERY_RUNNER.update(conn, sql, params);
        }catch(SQLException e){
            LOGGER.error("executeupdate entity failure",e);
            throw new RuntimeException(e);
        }finally{
            closeConnection();
        }
        return rows;
    }
    /**
     * 插入实体类
     */
    public static <T> boolean insertEntity(Class<T> entityClass, Map<String,Object> fieldMap){
        if(CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("can not insert entity: fieldMap is empty");
            return false;
        }

        String sql = "INSERT INTO" + getTableName(entityClass);
        StringBuilder colums = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for(String fieldName : fieldMap.keySet()){
            colums.append(fieldName).append(",");
            values.append("?,");
        }
        colums.replace(colums.lastIndexOf(","),values.length(),")");
        values.replace(colums.lastIndexOf(","),values.length(),")");
        sql +=  colums + "values" + values;

        Object[] params = fieldMap.values().toArray();
        return executeUpdate(sql,params) == 1;
    }
    /**
     * 更新实体类
     */
    public static <T> boolean updateEntity(Class<T> entityClass, long id,Map<String, Object> fieldMap){
        if(CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("can not update entity: fieldMap is empty");
            return false;
        }
        String sql = "UPDATE " + getTableName(entityClass) + "SET ";
        StringBuilder colums = new StringBuilder();
        for(String fieldName : fieldMap.keySet()){
            colums.append(fieldName).append("=?,");
        }
        sql +=  colums.substring(0,colums.lastIndexOf(",")) + "where id = ?";
        List<Object> paramList = new ArrayList<Object>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);
        return executeUpdate(sql,paramList) == 1;
    }
    /**
     * 删除实体类
     */
    public static <T> boolean deleteEntity(Class<T> entityClass, long id){
        String sql = "DELETE FROM " + getTableName(entityClass) + "WHERE ID = ? ";
        return executeUpdate(sql,id) == 1;
    }

    private static String getTableName(Class<?> entityClass){
        return entityClass.getSimpleName();
    }

}
