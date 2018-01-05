package com.allcom.service;

import com.allcom.bean.User;
import com.allcom.dao.MysqlDao;
import com.allcom.security.JwtTokenUtil;
import com.allcom.security.JwtUser;
import com.allcom.toolkit.GlobalTools;
import com.allcom.toolkitexception.ToolLibException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Created by ljy on 17/7/7.
 * ok
 */
@Service
public class AuthService {

    private static Logger logger = LoggerFactory.getLogger(AuthService.class);

    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;
    private JwtTokenUtil jwtTokenUtil;
//    private UserTRepository userTRepository;
    private MysqlDao mysqlDao;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    public AuthService(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtTokenUtil jwtTokenUtil,
            MysqlDao mysqlDao) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.mysqlDao = mysqlDao;
    }

    public User register(User userToAdd) {
        final String username = userToAdd.getUsername();
        if(mysqlDao.findByUsername(username).size()>0) {
            return null;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        final String rawPassword = userToAdd.getPassword();
        userToAdd.setPassword(encoder.encode(rawPassword));
        userToAdd.setLastPasswordResetDate(new Date());
        userToAdd.setRoles(asList("ROLE_USER"));
        saveUser(username,userToAdd.getPassword(),userToAdd.getLastPasswordResetDate(),userToAdd.getRoles(),userToAdd.getEmail());
        if(mysqlDao.findByUsername(username).size()>0){
            return userToAdd;
        }else{
            return null;
        }
    }

    @Transactional
    private void saveUser(String userName,String pass, Date lastPasswordResetDate, List<String> roles,String email){
        SimpleDateFormat m_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = m_format.format(lastPasswordResetDate);
        int userId = mysqlDao.saveToUser(userName,pass,s,email);
        if(userId > -1) {
            for (String role : roles) {
                mysqlDao.saveToUserRole(userId, role);
            }
        }
    }


    public String login(String username, String password) {
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        final Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(userDetails);
        return token;
    }

    public String refresh(String oldToken) {
        final String token = oldToken.substring(tokenHead.length());
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
        if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())){
            return jwtTokenUtil.refreshToken(token);
        }
        return null;
    }

    public int getUserIdByName(String userName){
        int ret = -1;
        List<Map<String,Object>> mapList = mysqlDao.findByUsername(userName);
        if(mapList.size()==1){
            Map<String,Object> map = mapList.get(0);
            if(map.get("id")!=null){
                try {
                    ret = GlobalTools.convertStringToInt(map.get("id").toString());
                } catch (ToolLibException e) {
                    logger.error("tool lib exception:{}",e.toString());
                }
            }
        }
        return ret;
    }
}
