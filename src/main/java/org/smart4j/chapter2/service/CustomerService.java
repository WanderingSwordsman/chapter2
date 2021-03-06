package org.smart4j.chapter2.service;

import org.slf4j.LoggerFactory;
import org.smart4j.chapter2.helper.DatabaseHelper;
import org.smart4j.chapter2.model.Customer;
import org.slf4j.Logger;

import java.awt.image.DataBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.smart4j.chapter2.helper.DatabaseHelper.closeConnection;

/**
 * 提供“客户”数据服务
 * Created by ShangJun on 2016/7/3.
 */
public class CustomerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);
    /**
     * 获取客户列表
     */
    public List<Customer> getCustomerList(String keyword){
        Connection conn = null;
        try{
            List<Customer> customerList = new ArrayList<Customer>();
            conn = DatabaseHelper.getConnection();
            String sql = "SELECT * FROM customer";
            customerList = DatabaseHelper.queryEntityList(Customer.class,sql);
            return customerList;
        }finally{
            DatabaseHelper.closeConnection();
        }
    }
    /**
    *获取客户
     */
    public Customer getCustomer(long id){
        return null;
    }
    /**
    *创建客户
     */
    public boolean createCustomer(Map<String,Object> fieldMap){
        return DatabaseHelper.insertEntity(Customer.class,fieldMap);
    }
    /**
    *更新客户
     */
    public boolean updateCustomer(long id,Map<String,Object> fieldMap){
        return DatabaseHelper.updateEntity(Customer.class,id,fieldMap);
    }
    /**
     * 删除客户
     */
    public boolean deleteCustomer(long id){
        return DatabaseHelper.deleteEntity(Customer.class,id);
    }

}
