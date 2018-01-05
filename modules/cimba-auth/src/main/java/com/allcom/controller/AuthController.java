package com.allcom.controller;

import com.allcom.bean.User;
import com.allcom.security.JwtAuthenticationRequest;
import com.allcom.security.JwtAuthenticationResponse;
import com.allcom.security.JwtTokenUtil;
import com.allcom.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by ljy on 17/7/7.
 * ok
 */
@RestController
@RequestMapping("/cimba-auth")
public class AuthController {

    @Value("${jwt.header}")
    private String tokenHeader;

    private final AuthService authService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AuthController(AuthService authService,JwtTokenUtil jwtTokenUtil) {
        this.authService = authService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST)
//    public String createAuthenticationToken(
//            @RequestParam(value="username") String username,@RequestParam(value="password") String password) throws AuthenticationException{
//        final String token = authService.login(username, password);
//
//        // Return the token
//        return token;
//    }
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException{
        final String token = authService.login(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        int userId = -1;
        if(token!=null && token.length()>10){
            userId = authService.getUserIdByName(authenticationRequest.getUsername());
        }
        // Return the token
        return ResponseEntity.ok(new JwtAuthenticationResponse(token,userId));
    }

//    public JwtAuthenticationResponse createAuthenticationToken(
//            @RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException{
//        final String token = authService.login(authenticationRequest.getUsername(), authenticationRequest.getPassword());
//
//        // Return the token
//        return new JwtAuthenticationResponse(token);
//    }

    @RequestMapping(value = "${jwt.route.authentication.refresh}", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(
            HttpServletRequest request) throws AuthenticationException{
        String token = request.getHeader(tokenHeader);
        String userName = jwtTokenUtil.getUsernameFromToken(token);
        int userId = authService.getUserIdByName(userName);
        String refreshedToken = authService.refresh(token);

        if(refreshedToken == null || userId < 0) {
            return ResponseEntity.badRequest().body(null);
        } else {
            return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken,userId));
        }
    }

    @RequestMapping(value = "${jwt.route.authentication.register}", method = RequestMethod.POST)
    public User register(@RequestBody User addedUser) throws AuthenticationException {
        return authService.register(addedUser);
    }
}
