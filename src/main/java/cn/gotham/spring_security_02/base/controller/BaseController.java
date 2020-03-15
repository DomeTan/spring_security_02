package cn.gotham.spring_security_02.base.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author tanchong
 * Create Date: 2020/3/15
 */
@Controller
public class BaseController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * 登录成功跳转页面
     * @return
     */
    @GetMapping("/")
    public String index(){

        return "web/index";
    }

    /**
     * 跳转登录界面
     * @return
     */
    @GetMapping("/login")
    public String login(){
        LOGGER.info("登录页面");
        return "web/login";
    }
}
