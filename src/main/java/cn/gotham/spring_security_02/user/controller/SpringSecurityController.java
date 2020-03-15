package cn.gotham.spring_security_02.user.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 准备了三个页面 用来测试SpringSecurity的权限控制
 * @author tanchong
 * Create Date: 2020/3/15
 */
@Controller
public class SpringSecurityController {

    // 需要WAM_ADMIN 才能访问
    @PreAuthorize("hasAuthority('WAM_ADMIN')")
    @GetMapping("/admin")
    public String admin() {
        return "web/verify/admin";
    }

    // WAM_USER 才能访问
    @PreAuthorize("hasAuthority('WAM_USER')")
    @GetMapping("/user")
    public String user() {
        return "web/verify/user";
    }
    // WAM_ADMIN WAM_USER 任意一个权限即可访问
    @PreAuthorize("hasAnyAuthority('WAM_ADMIN','WAM_USER')")
    @GetMapping("/common")
    public String common() {
        return "web/verify/common";
    }
}


