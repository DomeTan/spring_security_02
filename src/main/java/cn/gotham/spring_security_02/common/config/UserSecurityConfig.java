package cn.gotham.spring_security_02.common.config;

import cn.gotham.spring_security_02.common.repository.MongoTokenRepositoryImpl;
import cn.gotham.spring_security_02.user.enumeration.Authority;
import cn.gotham.spring_security_02.user.model.Role;
import cn.gotham.spring_security_02.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

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

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class UserSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSecurityConfig.class);

    private UserRepository userRepository;

    private ObjectMapper objectMapper;

    @Autowired
    public UserSecurityConfig(UserRepository userRepository, ObjectMapper objectMapper) {
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

        http.headers()
                .frameOptions()
                .disable();
        http.authorizeRequests()
                //设置拦截忽略，可以对以下资源放行
                .antMatchers(
                        "/login", "/public/**")
                .permitAll()
                .anyRequest()
                // 登录必须权限
                .hasAnyAuthority("WAM_USER","WAM_ADMIN");
        http.formLogin()
                // 设置登录页
                .loginPage("/login")
                // 设置登录处理接口
                .loginProcessingUrl("/api/v1/login")
                .permitAll()
                .defaultSuccessUrl("/")
                // 设置登录成功处理方法
                .successHandler(authenticationSuccessHandler());
        http.rememberMe() // 记住我
                .rememberMeParameter("wam_remember_me")
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(userDetailsService())
                .tokenValiditySeconds(7 * 24 * 60 * 60);
        http.logout() //登出配置
                .logoutUrl("/api/v1/logout")
                .logoutSuccessHandler(ajaxLogoutSuccessHandler())
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
        http.csrf()
                .disable();

    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
            return new MongoTokenRepositoryImpl();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 替换默认userDetailsService () 方法
        auth.userDetailsService(userDetailsService());
    }

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
    private LogoutSuccessHandler ajaxLogoutSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            var root = objectMapper.createObjectNode();
            root.put("redirect", "/login");

            response.getOutputStream().write(root.toString().getBytes());
        };
    }
}
