package com.allcom.conf;

import com.allcom.filter.JwtAuthenticationTokenFilter;
import com.allcom.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//import com.allcom.security.JwtTokenUtil;

@Configuration
//@EnableWebSecurity is used to enable Spring Security’s web security support and provide the Spring MVC integration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    // Spring会自动寻找同样类型的具体类注入，这里就是JwtUserDetailsServiceImpl了
    private final UserDetailsService userDetailsService;

    @Autowired
    public WebSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    //父类WebSecurityConfigurerAdapter有AuthenticationManagerBuilder类成员变量 （private AuthenticationManagerBuilder authenticationBuilder;）
    //以下相当于重载了父类的该成员变量（先自动装配一个实例authenticationManagerBuilder，再进行相关设置，然后被引入作为本类实例的一个成员变量),
    //之后该重载后的成员变量会被框架代码使用到。
    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                // 设置UserDetailsService
                .userDetailsService(this.userDetailsService)
                // 使用BCrypt进行密码的hash
                .passwordEncoder(passwordEncoder());
    }

    // 装载BCrypt密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil(){
        return new JwtTokenUtil();
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // 由于使用的是JWT，我们这里不需要csrf,不用担心csrf攻击
                .csrf().disable()
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .authorizeRequests()
                    //.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // 允许对于网站静态资源的无授权访问
                    .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                    ).permitAll()
                    // 对于获取token的rest api要允许匿名访问
                    .antMatchers("/cimba-auth/auth/**").permitAll()
                    // 除上面外的所有请求全部需要鉴权认证。 .and() 相当于标示一个标签的结束，之前相当于都是一个标签项下的内容
                .anyRequest().authenticated().and()

                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

        // 禁用缓存
        httpSecurity.headers().cacheControl();
    }

}
