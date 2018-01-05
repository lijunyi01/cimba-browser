package com.allcom.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ljy on 2017/11/5.
 * ok
 */
@Repository
public class MysqlDao {

    final JdbcTemplate jdbcTemplate;

    @Autowired
    public MysqlDao(@Qualifier("jdbctemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //返回自增长id的值
    public int saveToUser(String userName, String pass, String lastPasswordResetDate,String email){
        int ret = -1;

        KeyHolder key=new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator(){

            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement preState=con.prepareStatement("insert into users(username,password,lastpasswordresetdate,email) values(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
//                preState.setInt(1,umid);
                preState.setString(1,userName);
                preState.setString(2,pass);
                preState.setString(3,lastPasswordResetDate);
                preState.setString(4,email);
                return preState;
            }
        },key);
        ret = key.getKey().intValue();
        return ret;
    }

//    public List<Map<String,Object>> getRegPhoneNum(String umid){
//        return jdbcTemplate.queryForList("select a.phone from cust_info a,cust_account_reletion b where b.service_account=? and a.cust_id=b.cust_id order by a.cust_id desc limit 1",umid);
//    }

    public List<Map<String,Object>> findByUsername(String userName){
        return jdbcTemplate.queryForList("select * from users where username=?",userName);
    }

    public List<Map<String,Object>> findByUserId(int userId){
        return jdbcTemplate.queryForList("select * from users where id=?",userId);
    }

    public void saveToUserRole(int userId,String role){
        jdbcTemplate.update("insert into user_roles(userid,role) values(?,?)",userId,role);
    }

    public List<String> getRolesByUserId(int userId){
        List<String> ret = new ArrayList<>();
        List<Map<String,Object>> mapList = jdbcTemplate.queryForList("select role from user_roles where userid=?",userId);
        for(Map<String,Object> map: mapList){
            if(map.get("role")!=null){
                ret.add(map.get("role").toString());
            }
        }
        return ret;
    }

    public List<Map<String,Object>> getUserParam(int userId,String param_name){
        if(param_name == null || param_name.equals("")){
            return jdbcTemplate.queryForList("select * from user_param where userId=?", userId);
        }else {
            return jdbcTemplate.queryForList("select * from user_param where userId=? and param_name=?", userId,param_name);
        }
    }

    public List<String> getWhiteList(int userId){
        List<String> ret = new ArrayList<>();
        List<Map<String,Object>> mapList = jdbcTemplate.queryForList("select * from whitelist where userid=?",userId);
        for(Map<String,Object> map: mapList){
            if(map.get("domain")!=null){
                ret.add(map.get("domain").toString());
            }
        }
        return ret;
    }

    public List<String> getBlackList(int userId){
        List<String> ret = new ArrayList<>();
        List<Map<String,Object>> mapList = jdbcTemplate.queryForList("select * from blacklist where userid=?",userId);
        for(Map<String,Object> map: mapList){
            if(map.get("domain")!=null){
                ret.add(map.get("domain").toString());
            }
        }
        return ret;
    }

    public boolean domainInWhiteList(int userId,String domain){
        boolean ret = false;
        List<Map<String,Object>> mapList = jdbcTemplate.queryForList("select * from whitelist where userid=? and domain=?",userId,domain);
        if(mapList.size()>0){
            ret = true;
        }
        return ret;
    }

    public void addToWhiteList(int userId,String domain){
        jdbcTemplate.update("insert into whitelist(userid,domain) values(?,?)",userId,domain);
    }
}
