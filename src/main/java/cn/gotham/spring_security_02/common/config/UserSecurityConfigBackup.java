package cn.gotham.spring_security_02.common.config;

import cn.gotham.spring_security_02.common.bean.ErrorResponse;
import cn.gotham.spring_security_02.common.bean.GlobalAuthenticationFailureHandler;
import cn.gotham.spring_security_02.user.enumeration.Authority;
import cn.gotham.spring_security_02.user.model.Role;
import cn.gotham.spring_security_02.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Objects;

/**
 * 该类是 Spring Security 的配置类，该类的三个注解分别是标识该类是配置类、开启全局 Securtiy 注解。
 * 这里我们还指定了密码的加密方式（5.0 版本强制要求设置），因为我们数据库是明文存储的，所以明文返回即可，如下所示：
 * @author tanchong
 * Create Date: 2020/3/8
 */

//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class UserSecurityConfigBackup extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSecurityConfigBackup.class);

    private UserRepository userRepository;

    private ObjectMapper objectMapper;

    private GlobalAuthenticationFailureHandler globalAuthenticationFailureHandler;

    @Autowired
    public UserSecurityConfigBackup(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    /**
     *
     * 可将该方法单独封装、本文采用重写方法
     * 重写 userDetailsService() 将用户信息和权限注入进来
     */
    @Override
    protected UserDetailsService userDetailsService() {

        return (username)  -> {
            // 从数据库中取出用户信息
            var user = userRepository.findByUsername(username).orElse(null);
            // 判断用户是否存在
            if (user == null) {
                throw new UsernameNotFoundException("用户[ "+username+" ]不存在!");
            }

//            返回UserDetails实现类写法一
//            var authorities = new ArrayList<GrantedAuthority>();
//            List<Role> roles = user.getRoles();
//            roles.forEach(role -> {
//                role.getAuthorityList().forEach(authority -> {
//                    authorities.add(new SimpleGrantedAuthority(authority.toString()));
//                });
//            });
//            UserDetails build = User.withUsername(username).password(user.getPassword()).authorities(authorities).build();
            // 返回UserDetails实现类写法二
            return User.withUsername(username) //添加用户名
                    .password(user.getPassword()) //添加用户密码
                    //添加用户权限
                    .authorities(user.getRoles()
                            .stream()
                            .filter(Objects::nonNull)
                            .map(Role::getAuthorityList)
                            .filter(Objects::nonNull)
                            .flatMap(Collection::stream)
                            .filter(Objects::nonNull)
                            .map(Authority::toString)
                            .map(SimpleGrantedAuthority::new)
                            .toArray(SimpleGrantedAuthority[]::new))
                    .build();
        };
    }
    /**
     * 指定密码 加密  与 校验
     * (加密方式可修改)
     * @return
     */
    @Bean
    public PasswordEncoder md5PasswordEncoderForUser(){
            return new PasswordEncoder(){

                @Override
                public String encode(CharSequence rawPassword) {
                    return DigestUtils.md5Hex(rawPassword.toString());
                }

                @Override
                public boolean matches(CharSequence rawPassword, String encodedPassword) {
                    return encodedPassword.equals(encode(rawPassword));
                }
            };
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                // 自定义 authenticationEntryPoint
                .authenticationEntryPoint(unauthorizedEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
                .and()
                // 详情见 https://www.springcloud.cc/spring-security.html#headers
                .headers()
                .frameOptions()
                .disable()
                .and()
                // 方法详情见 https://www.springcloud.cc/spring-security.html#jc-authorize-requests
                .authorizeRequests()
                .antMatchers("/public/**") // 释放静态资源
                .permitAll() // 任何人都可访问
                // 任何请求 ↓
                .anyRequest()
                // 都需要具备以下权限
                .hasAnyAuthority("WAM_USER","WAM_ADMIN")
                .and()
                // 登录配置
                .formLogin()
                // 登录页面接口
                .loginPage("/login")
                // 登录处理 接口
                .loginProcessingUrl("/api/v1/login")
                .permitAll()
                //  登录成功访问的接口
                .defaultSuccessUrl("/")
                // 登录成功 处理方法
                .successHandler(authenticationSuccessHandler())
                // 失败处理(器) 方法
                .failureHandler(globalAuthenticationFailureHandler)
                .and()
                // 退出登录配置
                .logout()
                // 退出登录接口
                .logoutUrl("/api/v1/logout")
                // 退出登录处理
                .logoutSuccessHandler(logoutSuccessHandler())
                // 退出时清除session (默认为true)
                .invalidateHttpSession(true)
                // 删除cookie
                .deleteCookies("JSESSIONID");

    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 替换默认userDetailsService () 方法
        auth.userDetailsService(userDetailsService());
    }

    /**
     * 自定义身份验证成功处理方法:
     *      身份验证通过后,返回首页接口
     * @return
     */
    private AuthenticationSuccessHandler authenticationSuccessHandler(){

        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication ) ->{
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");

            var root = objectMapper.createObjectNode();
            root.put("redirect",
                    request.getRequestURI().equals("/api/v1/login") ? "/" : request.getRequestURI());
            response.getOutputStream().write(root.toString().getBytes());
        };
    }
    /**
     * 自定义退出登录处理方法:
     *  退出登录成功之后,返回登录页面接口
     * @return
     */
    private LogoutSuccessHandler logoutSuccessHandler() {

        return (HttpServletRequest request,HttpServletResponse response,Authentication authentication) -> {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            var root = objectMapper.createObjectNode();
            root.put("redirect","/login");
            response.getOutputStream().write(root.toString().getBytes());

        };
    }
    /**
     * 无权请求资源处理
     * @return
     */
    private AccessDeniedHandler accessDeniedHandler() {

        return (HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) -> {
            var requestedWithHeader = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(requestedWithHeader)) {
                var errorResponse = new ErrorResponse(accessDeniedException.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getOutputStream().write(objectMapper.writeValueAsBytes(errorResponse));
            } else {
                response.sendRedirect("/login");
            }
        };
    }
    /**
     *  默认情况下登陆失败会跳转页面，这里自定义，
     *  同时判断是否ajax请求，是ajax请求则返回json，否则跳转失败页面
     * @return
     */
    private AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) -> {
            var requestedWithHeader = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(requestedWithHeader)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getOutputStream().write(authException.getMessage().getBytes());
            } else {

                response.sendRedirect("/login");
            }
        };
    }

}
