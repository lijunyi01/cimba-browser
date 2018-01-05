package com.allcom.controller;

import com.allcom.bean.BWInfoResp;
import com.allcom.bean.GeneralResp;
import com.allcom.security.JwtTokenUtil;
import com.allcom.service.AppService;
import com.allcom.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ljy on 17/7/7.
 * ok
 */
@RestController
@RequestMapping("/cimba-auth/info")
@PreAuthorize("hasRole('USER')")
public class InfoController {

    private static Logger logger = LoggerFactory.getLogger(InfoController.class);

    @Value("${jwt.header}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;


    final AppService appService;
    final AuthService authService;
    final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public InfoController(AppService appService,JwtTokenUtil jwtTokenUtil,AuthService authService) {
        this.appService = appService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authService = authService;
    }

    @RequestMapping("/bwinfo")
    public BWInfoResp getBWInfo(
            @RequestParam(value="userid",required = false,defaultValue = "") int userId2,   //实际没必要通过参数传，应从token 里获取
            @RequestParam(value="username",required = false,defaultValue = "") String username2,   //为了演示客户端传两个参数的情况，保留这两个参数
            HttpServletRequest httpServletRequest){

        String requestIp = httpServletRequest.getRemoteAddr();

        //能进到这里说明token合法，且对应于"User"角色；否则直接报403拒绝访问了
        String authHeader = httpServletRequest.getHeader(tokenHeader);
        String token = authHeader.substring(tokenHead.length());
        String userName = jwtTokenUtil.getUsernameFromToken(token);
        int userId = authService.getUserIdByName(userName);

        logger.debug("client [{}] get bwinfo username:{} userid:{}", requestIp,userName,userId);

        if(userId>-1) {
            //0:whitelist   1:blacklist   2:white first and black then   3:black first and white then
            int bwFlag = appService.getBWFlag(userId);
            return appService.getBWInfo(userId, bwFlag);
        }else{
            return new BWInfoResp();
        }
    }

    @RequestMapping("/addtowhitelist")
    public GeneralResp addToWhiteList(
            @RequestParam(value="userid",required = false,defaultValue = "") int userId2,   //实际没必要通过参数传，应从token 里获取
            @RequestParam(value="username",required = false,defaultValue = "") String username2,   //为了演示客户端传两个参数的情况，保留这两个参数
            @RequestParam(value="domain") String domain,
            @RequestParam(value="passwd") String passwd,
            HttpServletRequest httpServletRequest){

        String requestIp = httpServletRequest.getRemoteAddr();

        //能进到这里说明token合法，且对应于"User"角色；否则直接报403拒绝访问了
        String authHeader = httpServletRequest.getHeader(tokenHeader);
        String token = authHeader.substring(tokenHead.length());
        String userName = jwtTokenUtil.getUsernameFromToken(token);
        int userId = authService.getUserIdByName(userName);

        logger.debug("client [{}] addtowhitelist username:{} userid:{} domain:{} pass:{}", requestIp,userName,userId,domain,passwd);

        if(userId>-1) {
            //0:whitelist   1:blacklist   2:white first and black then   3:black first and white then
            return appService.addToWhiteList(userId,domain,passwd);
        }else{
            return new GeneralResp();
        }
    }
}
