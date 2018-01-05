package com.allcom.service;

import com.allcom.bean.BWInfoResp;
import com.allcom.bean.GeneralResp;
import com.allcom.dao.MysqlDao;
import com.allcom.toolkit.GlobalTools;
import com.allcom.toolkitexception.ToolLibException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by ljy on 17/7/7.
 * ok
 */
@Service
public class AppService {

    private static Logger logger = LoggerFactory.getLogger(AppService.class);

    private MysqlDao mysqlDao;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AppService(
            MysqlDao mysqlDao,PasswordEncoder passwordEncoder) {
        this.mysqlDao = mysqlDao;
        this.passwordEncoder = passwordEncoder;
    }

    public int getBWFlag(int userId){
        int ret = 0; //如果没有设置相关参数，默认值取0
        List<Map<String,Object>> mapList = mysqlDao.getUserParam(userId,"bwflag");
        if(mapList.size()==1){
            Map<String,Object> map = mapList.get(0);
            if(map.get("bwflag")!=null){
                try {
                    ret = GlobalTools.convertStringToInt(map.get("bwflag").toString());
                } catch (ToolLibException e) {
                    logger.error("tool lib exception:{}",e.toString());
                }
            }
        }
        return ret;
    }

    public BWInfoResp getBWInfo(int userId, int bwFlag){
        BWInfoResp bwInfoResp = new BWInfoResp();
        //bwflag   0:whitelist   1:blacklist   2:white first and black then   3:black first and white then
        bwInfoResp.setBwFlag(bwFlag);

        if (bwFlag == 0) {
            List<String> whiteList = mysqlDao.getWhiteList(userId);
            bwInfoResp.setWhiteList(whiteList);
        }else if(bwFlag == 1){
            List<String> blackList = mysqlDao.getBlackList(userId);
            bwInfoResp.setBlackList(blackList);
        }else{
            List<String> whiteList = mysqlDao.getWhiteList(userId);
            bwInfoResp.setWhiteList(whiteList);
            List<String> blackList = mysqlDao.getBlackList(userId);
            bwInfoResp.setBlackList(blackList);
        }

        return bwInfoResp;
    }

    public GeneralResp addToWhiteList(int userId, String domain,String passwd){
        GeneralResp generalResp = new GeneralResp();
        if(passwdVerifyOk(userId,passwd)){
            if(!mysqlDao.domainInWhiteList(userId,domain)){
                mysqlDao.addToWhiteList(userId,domain);
            }

            if(mysqlDao.domainInWhiteList(userId,domain)){
                generalResp.setErrorCode(0);
                generalResp.setErrorMsg("success");
                logger.info("success to add to whitelist! userid:{} domain:{}",userId,domain);
            }else{
                generalResp.setErrorCode(-3);
                generalResp.setErrorMsg("failed to add domain to whitelist");
                logger.info("failed to add to whitelist ! userid:{} domain:{}",userId,domain);
            }
        }else{
            generalResp.setErrorCode(-2);
            generalResp.setErrorMsg("password verify failed");
            logger.info("failed to add to whitelist ! password verify failed! userid:{} domain:{}",userId,domain);
        }
        return generalResp;
    }

    private boolean passwdVerifyOk(int userId,String passwd){
        boolean ret = false;

//        String encodedPassword = passwordEncoder.encode(passwd);
        String storedPassword = null;
        List<Map<String,Object>> mapList = mysqlDao.findByUserId(userId);
        if(mapList.size()==1){
            Map<String,Object> map = mapList.get(0);
            if(map.get("password")!=null){
                storedPassword = map.get("password").toString();
            }
        }
        if(storedPassword !=null) {
            if (passwordEncoder.matches(passwd,storedPassword)) {
                ret = true;
            }
        }
        return ret;
    }
}
