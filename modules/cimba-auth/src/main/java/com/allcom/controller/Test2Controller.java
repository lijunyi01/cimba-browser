package com.allcom.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ljy on 17/7/7.
 * ok
 */
@RestController
@RequestMapping("/cimba-auth/test2")
public class Test2Controller {

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/user")
    public String test2(){
        return "role user can access";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/admin")
    public String test3(){
        return "role admin can access";
    }

    @RequestMapping(value = "/all")
    public String test4(){
        return "anybody can access";
    }
}
